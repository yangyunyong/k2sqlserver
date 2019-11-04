package com.xjst.cn.k2sqlserver.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

public class JsonUtil {
    /**
     * json 转成 map格式
     * @param jsonString
     * @return
     */
    public static Map<String,String> JsonToMap(String jsonString) {
        Map<String, String> map = JSON.parseObject(jsonString,
                new TypeReference<Map<String, String>>() {
                });
        Map<String,String> map1 = getStandardMap(map);
        return map1;
    }

    /**
     * 取Map集合的交集（String,String）相同的key和value
     *
     * @param map1 集合
     * @param map2 集合
     * @return 两个集合的交集
     */
    public static Map<String, String> getSameKV(Map<String, String> map1, Map<String, String> map2) {

        Map<String, String> result = Maps.newHashMap();
        for(String key1 : map1.keySet()){
            String map1value = map1.get(key1);
            String map2value = map2.get(key1);
            if (map1value!=null && map1value.equals(map2value)) {
                result.put(key1,map1value);
            }
        }
        return result;
    }

    /**
     * 获取标准的map格式数据
     * @param map
     * @return
     */
    public static Map<String, String> getStandardMap(Map<String, String> map) {
        for(String key : map.keySet()){
            String value = map.get(key);
            if(value !=null && value.length()==29 && isDateTime(value)){
                map.put(key,"TO_TIMESTAMP('"+value+"','yyyy-MM-dd hh24:mi:ss ff')");
            }
            if(value !=null && (value.length()==13|| value.length()==12) && isNumber(value) && !"expire".equals(key)){
                long time = Long.valueOf(value);

                map.put(key, timestampToDate(time));
            }
            if(value !=null && value.length()==13 && "-".equals(value.substring(0,1)) && isNumber(value.substring(1,13))){
                long time = Long.valueOf(value);
                map.put(key,timestampToDate(time));
            }
            if(value !=null && value.length()==19 && isDateTime(value)){
                map.put(key,"TO_DATE('"+value+"','yyyy-MM-dd hh24:mi:ss')");
            }
        }
        return map;
    }

    /**
     * 时间戳格式 1476381699000  转换成时间格式
     * @param time
     * @return
     */
    public static String timestampToDate(long time) {
        if (0L<time && time < 10000000000L) {
            time = time * 1000;
        }
        SimpleDateFormat sdf = null;
        if(time<0L){
            sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00.000");
        }else {
            sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        }
        long st = 60*60000*8;
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))-Long.parseLong(st+"")));
        return sd;
    }

    /**
     * 判断是否是数字
     * @param Str
     * @return
     */
    public static boolean isNumber(String Str){
        Pattern p = Pattern.compile("^[1-9]\\d*|0$");//匹配正整数（整数 + 0）
        return p.matcher(Str).matches();
    }
    /**
     * 判断是否是时间 yyyy-MM-dd HH:mm:ss格式
     * @param datetime
     * @return
     */
    public static boolean isDateTime(String datetime){
        String datatime1 = datetime.substring(0,19);
        Pattern p = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1][0-9])|([2][0-4]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");

        return p.matcher(datatime1).matches();

    }
}
