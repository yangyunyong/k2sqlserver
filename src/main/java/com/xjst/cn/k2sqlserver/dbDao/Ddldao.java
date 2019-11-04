package com.xjst.cn.k2sqlserver.dbDao;

import com.xjst.cn.k2sqlserver.SqlServerConsumer;
import com.xjst.cn.k2sqlserver.util.DBUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Ddldao {
    private static final Logger log= Logger.getLogger(Ddldao.class);
    /**
     * 保存或是更新数据
     * @return
     */
    public int saveData(Map<String, String> map, String tablename){
        Connection conn = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        StringBuffer sqlbuff = null;
        int i=0;
        try {
            
            conn = DBUtil.getConnection();

            sqlbuff = new StringBuffer();
            sqlbuff.append("insert into ")
                    .append(tablename).append("(");
            for(String str:map.keySet()){
                sqlbuff.append(str).append(",");
            }
            sqlbuff.replace(sqlbuff.length() - 1, sqlbuff.length(), "");
            sqlbuff.append(") values");
            sqlbuff.append("(");
            for(String str:map.keySet()){
                if("SQL".equals(str)){
                    sqlbuff.append("'").append(map.get(str).replaceAll("'","\"")).append("',");
                }else{
                    sqlbuff.append("'").append(map.get(str)).append("',");
                }

            }

            sqlbuff.append(")");

            sqlbuff.replace(sqlbuff.length() - 2, sqlbuff.length()-1, "");//去掉最后一个,
            Pattern p = Pattern.compile("\\r\\n");
            Matcher m = p.matcher(sqlbuff.toString());
            String strNoBlank = m.replaceAll(" ");
            preparedStatement = conn.prepareStatement(strNoBlank);

            i = preparedStatement.executeUpdate();
            if(i==1) {
                log.debug("insertOK！");
            }else {
                log.error("insertError！i="+i+":"+sqlbuff.toString());
            }
        } catch (SQLException e) {
            log.error("insertError: "+sqlbuff.toString());
            e.printStackTrace();
        }finally {
            DBUtil.close(conn);
            // 释放资源
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return i;
    }

    /**
     * 删除数据
     * @return
     */
    public int delData(Map<String, String> map,String tablename){
        Connection conn = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        StringBuffer sqlbuff = null;
        int i=0;
        try {
            
            conn = DBUtil.getConnection();


            sqlbuff = new StringBuffer();
            sqlbuff.append("delete from ")
                    .append(tablename);
            sqlbuff.append(" where ");
            for(String str:map.keySet()){
                sqlbuff.append(str);
                sqlbuff.append(" = ");
                sqlbuff.append("'").append(map.get(str)).append("'");
                sqlbuff.append(" and ");
            }

            sqlbuff.replace(sqlbuff.length() - 4, sqlbuff.length(), "");//去掉最后一个and
            preparedStatement = conn.prepareStatement(sqlbuff.toString().replace("\r\n"," "));

            i = preparedStatement.executeUpdate();
            if(i==1){
                log.debug("deleteOK！");
            }else {
                log.error("deleteError！kyxt i="+i+":"+sqlbuff.toString());
            }

        } catch (SQLException e) {
            log.error("deleteError: "+sqlbuff.toString());
            e.printStackTrace();
        }finally {
            DBUtil.close(conn);
            // 释放资源
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return i;
    }
    /**
     * 删除数据
     * @return
     */
    public int updateData(Map<String, String> mapUp,Map<String, String> mapSame,String tablename){
        Connection conn = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        StringBuffer sqlbuff = null;
        int i=0;
        try {
            
            conn = DBUtil.getConnection();


            sqlbuff = new StringBuffer();
            sqlbuff.append("update ").append(tablename).append(" set ");
            for(String str:mapUp.keySet()){
                sqlbuff.append(str);
                sqlbuff.append(" = ");
                sqlbuff.append("'").append(mapUp.get(str)).append("'");
                sqlbuff.append(",");
            }
            sqlbuff.replace(sqlbuff.length() - 1, sqlbuff.length(), "");//去掉最后一个,
            sqlbuff.append(" where ");
            for(String str1:mapSame.keySet()){
                sqlbuff.append(str1);
                sqlbuff.append(" = ");
                sqlbuff.append("'").append(mapSame.get(str1)).append("'");
                sqlbuff.append(" and ");
            }
            sqlbuff.replace(sqlbuff.length() - 4, sqlbuff.length(), "");//去掉最后一个and
            preparedStatement = conn.prepareStatement(sqlbuff.toString().replace("\r\n"," "));

            i = preparedStatement.executeUpdate();
            if(i==1) {
                log.debug("updateOK！");
            }else {
                log.error("updateError! i="+i+":"+sqlbuff.toString());
            }
        } catch (SQLException e) {
            log.error("updateError: "+sqlbuff.toString());
            e.printStackTrace();
        }finally {
            DBUtil.close(conn);
            // 释放资源
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return i;
    }
}
