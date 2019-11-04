package com.xjst.cn.k2sqlserver;

import com.xjst.cn.k2sqlserver.dbDao.Ddldao;
import com.xjst.cn.k2sqlserver.util.JsonUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@EnableKafka
@Component
public class SqlServerConsumer {

    private static final Logger log= Logger.getLogger(SqlServerConsumer.class);

    @Autowired
    private Ddldao ddldao;
    /**
     * topics= {"mytopic"}   topics= "${mytopic}"   topicPattern = "fullfillment.*" 正则匹配
     * @param records
     * @param ack
     */
    @KafkaListener(topicPattern = "sqlserverHQXT.*",containerFactory = "batchContainerFactory")
    public void batchListener(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {

        records.forEach(record -> consumer(record));
        ack.acknowledge(); //手动提交
    }


    /**
     * 单条消费
     */
    public void consumer(ConsumerRecord<String, String> record) {
        ddlData(record);
    }
    /* @Bean
        public NewTopic batchTopic() {
            return new NewTopic("mytopic", 1, (short) 1);
        }
    */

    /**
     * 解析接受到的数据，进行增删改操作
     * @param record
     */
    public void ddlData(ConsumerRecord<String, String> record){
        String topic = record.topic();
        String[] split = topic.split("\\.");
        String tablename = split[2];
        String result = record.value().toString();
        Map<String, String> jsonToMap = JsonUtil.JsonToMap(result);
        String op_type = jsonToMap.get("op");
        String source = jsonToMap.get("source");
        Map<String, String> sourceToMap = JsonUtil.JsonToMap(source);

        Map<String, String> insOrupMap = null;
        Map<String, String> delMap = null;
        Map<String,String> updateMapB = null;//更新前数据
        Map<String,String> updateMapA = null;//更新后数据

        if("c".equals(op_type)){//新增数据
            String after = jsonToMap.get("after");
            insOrupMap = JsonUtil.JsonToMap(after);

            ddldao.saveData(insOrupMap,tablename);
        }else if("d".equals(op_type)){//删除数据
            String before = jsonToMap.get("before");
            delMap = JsonUtil.JsonToMap(before);

            ddldao.delData(delMap,tablename);
        }else if("u".equals(op_type)){//更新数据
            String before = jsonToMap.get("before");
            updateMapB = JsonUtil.JsonToMap(before);
            //delData(updateMapB,tablename);

            String after = jsonToMap.get("after");
            updateMapA = JsonUtil.JsonToMap(after);

            Map<String,String> jsMap = JsonUtil.getSameKV(updateMapB,updateMapA);
            //得到修改的数据
            for(String str :jsMap.keySet()){
                updateMapA.remove(str);
            }

            ddldao.updateData(updateMapA,jsMap,tablename);

        }
    }


}
