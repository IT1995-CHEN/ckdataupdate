package com.ck.dataupdate.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang.StringUtils;

/**
 * @author ChenLei
 */
public class CommonUtils {

  //  public static String jdbcUrlAndBaseDataSource(String ip, String port, String userName, String passWord,
//      String dataBaseName) {
//    String url = "jdbc:clickhouse://" + ip + ":" + port + "/" + dataBaseName;
//    System.out.println(url);
//    DataSourceAutoConfiguration dataSourceAutoConfiguration = new DataSourceAutoConfiguration();
//    dataSourceAutoConfiguration.setUrl(url);
//    dataSourceAutoConfiguration.setUserName(userName);
//    dataSourceAutoConfiguration.setPassWord(passWord);
//    return url;
//  }

  //表名称拼接处理
  public static String handleTableNameSql(String tableName) {
    String[] split = tableName.split(",");
    String collect = Stream.of(split).collect(Collectors.joining("','", "'", "'"));
    return collect;
  }


  public static boolean isHaving(String name, List<String> list) {
    for (String sname : list) {
      if (name.equals(sname)) {
        return true;
      }
    }
    return false;
  }

}
