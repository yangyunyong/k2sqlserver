package com.xjst.cn.k2sqlserver.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * kafka过滤器，过滤垃圾数据。
 */
public class FilterKafkaMessage {
    private static final Logger log= LoggerFactory.getLogger(FilterKafkaMessage.class);
    public static boolean fileterMessage(String msg) {

        if (msg==null || msg.length()<10) {
            //返回true将会被丢弃

            return true;
        }else if (!(msg.startsWith("{")&&msg.endsWith("}"))) {/*如果数据不是json就将数据丢弃*/
            //返回true将会被丢弃
            log.info("filterContainerFactory  filter  : " + msg);
            return true;
        }
        return false;
    }
}
