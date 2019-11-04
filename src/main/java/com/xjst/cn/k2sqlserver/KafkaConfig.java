package com.xjst.cn.k2sqlserver;


import com.xjst.cn.k2sqlserver.filter.FilterKafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private Boolean autoCommit;

/*    @Value("${spring.kafka.consumer.auto-commit-interval}")
    private Integer autoCommitInterval;*/

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.max-poll-records}")
    private Integer maxPollRecords;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;


    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 120000);
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 180000);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return props;
    }

    @Bean("batchContainerFactory")
    public ConcurrentKafkaListenerContainerFactory listenerContainer() {
        ConcurrentKafkaListenerContainerFactory container = new ConcurrentKafkaListenerContainerFactory();
        container.setConsumerFactory(new DefaultKafkaConsumerFactory(consumerConfigs()));
        //设置并发量，小于或等于Topic的分区数
        container.setConcurrency(1);
        //设置为批量监听
        container.setBatchListener(true);
        container.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);//设置提交偏移量的方式
        container.getContainerProperties().setPollTimeout(1000);
        container.setRecordFilterStrategy((RecordFilterStrategy) consumerRecord ->
                FilterKafkaMessage.fileterMessage((String) consumerRecord.value()));/*丢弃不需要的数据*/
        return container;
    }
    /**
     * RECORD
     * 每处理一条commit一次
     * BATCH(默认)
     * 每次poll的时候批量提交一次，频率取决于每次poll的调用频率
     * TIME
     * 每次间隔ackTime的时间去commit(跟auto commit interval有什么区别呢？)
     * COUNT
     * 累积达到ackCount次的ack去commit
     * COUNT_TIME
     * ackTime或ackCount哪个条件先满足，就commit
     * MANUAL
     * listener负责ack，但是背后也是批量上去
     * MANUAL_IMMEDIATE
     * listner负责ack，每调用一次，就立即commit
     */

}
