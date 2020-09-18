package com.ck.dataupdate.service.impl;

import com.ck.dataupdate.mapper.RemoteDataUpdateStructureMapper;
import com.ck.dataupdate.service.RemoteDataUpdateStructureService;
import com.ck.dataupdate.utils.CommonUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author ChenLei
 * @date 2020/09/18
 */

@Repository
@Slf4j
public class RemoteDataUpdateStructureServiceImpl implements RemoteDataUpdateStructureService {

  @Autowired
  private RemoteDataUpdateStructureMapper remoteDataUpdateStructureMapper;


  @Override
  public String updateRemoteStructure(String oldDatabaseName, String newDatabaseName, String ip,
      String username, String password) {

    List<String> getDataBaseList = remoteDataUpdateStructureMapper
        .getRemoteDataBase(ip, username, password);

    if (getDataBaseList.size() == 0) {
      return "该ck服务器中没有一个库存在";
    } else {
      boolean a = CommonUtils.isHaving(oldDatabaseName, getDataBaseList);
      if (a == false) {
        return "该ck服务器所有库中尚无源头库";
      }
    }
    try {
      remoteDataUpdateStructureMapper.dropDataBase(newDatabaseName);
    } catch (Exception e) {
      log.info("删除的数据库名不存在，即将新建库");
    } finally {
      remoteDataUpdateStructureMapper.createDataBase(newDatabaseName);
    }
    List<String> tableNameList = remoteDataUpdateStructureMapper
        .getRemoteTableNames(oldDatabaseName, ip, username, password);
    if (tableNameList.size() == 0) {
      return "源头库无表，同步数据库成功";
    }
    for (String tableName : tableNameList) {
      String tableSql = remoteDataUpdateStructureMapper
          .getRemoteTableStructure(oldDatabaseName, tableName, ip, username, password);

      tableSql = tableSql.replaceAll(oldDatabaseName, newDatabaseName);

      remoteDataUpdateStructureMapper.createRemoteTableStructure(tableSql);
    }
    return "同步表结构数据成功";
  }


  @Override
  public String updateRemoteData(String oldDatabaseName, String newDatabaseName, String isCluster,
      String suffixName, String ip, String username, String password) {

    String logInfo = new String();
    List<String> tableNameList = new ArrayList<>();
    System.out.println("0".equals(isCluster));

    if ("0".equals(isCluster)) {
      tableNameList = remoteDataUpdateStructureMapper
          .getRemoteTableNames(oldDatabaseName, ip, username, password);
      logInfo = "同步库中所有表数据成功";
    } else if ("1".equals(isCluster)) {
      tableNameList = remoteDataUpdateStructureMapper
          .getNotLikeRemoteTableNames(suffixName, oldDatabaseName, ip, username, password);

      logInfo = "同步库中不为集群后缀的表数据成功";
    } else {
      tableNameList = remoteDataUpdateStructureMapper
          .getLikeRemoteTableNames(suffixName, oldDatabaseName, ip, username, password);
      logInfo = "同步库中拥有集群表后缀的表数据成功";
    }

    if (tableNameList.size() == 0) {
      return "源数据库无表，数据同步成功";
    }

    String maxNum = getTableRemoteMaxNum(oldDatabaseName, tableNameList, ip, username, password);
    System.out.println(maxNum);
    int i = 0;
    for (String tableName : tableNameList) {
      String remoteSql = "insert into ";
      remoteSql += newDatabaseName + "." + tableName + " select * from  remote('" + ip + "',"
          + oldDatabaseName + "."
          + tableName + ",'" + username + "','" + password + "')" + " limit " + maxNum;
      System.out.println(remoteSql);
      i++;
      try {
        if ("2".equals(isCluster)) {
          remoteDataUpdateStructureMapper.updateRemoteData(remoteSql);
          log.info("第" + i + "张表" + tableName + "在" + newDatabaseName + "数据同步成功");
          continue;
        }
        remoteDataUpdateStructureMapper.truncateTable(newDatabaseName, tableName);
        remoteDataUpdateStructureMapper.updateRemoteData(remoteSql);
        log.info("第" + i + "张表" + tableName + "在" + newDatabaseName + "数据同步成功");
      } catch (Exception e) {
        log.info("第" + i + "张表" + tableName + "在" + newDatabaseName + "数据同步出错,需要查询该表count值验证");
      }
    }
    return logInfo;

  }


  public String getTableRemoteMaxNum(String database, List<String> tableNameList, String ip,
      String username, String password) {
    String sql = "select max(num) from (";

    String tableSql = new String();

    if (tableNameList.size() == 0) {
      return "0";
    }
    for (int i = 0; i < tableNameList.size(); i++) {
      if (tableNameList.size() == 1) {
        tableSql =
            "select count(*) as num from remote('" + ip + "'," + database + "." + tableNameList
                .get(0) + ",'" + username + "','" + password + "') ";
        sql = tableSql;
        break;
      }
      if (i == tableNameList.size() - 1) {
        tableSql +=
            "select count(*) as num from remote('" + ip + "'," + database + "." + tableNameList
                .get(i) + ",'" + username + "','" + password + "') )";
        sql += tableSql;
        break;
      }
      tableSql +=
          "select count(*) as num from remote('" + ip + "'," + database + "." + tableNameList.get(i)
              + ",'" + username + "','" + password + "') "
              + " UNION ALL ";
    }
    String maxNum = remoteDataUpdateStructureMapper.getMaxNum(sql);
    return maxNum;
  }


  @Override
  public String updateRemoteTableData(String oldDatabaseName, String newDatabaseName,
      String tableNames,
      String isTruncate, String ip, String username, String password) {
    List<String> fitTableNameList = remoteDataUpdateStructureMapper
        .getSomeFitTableNames(oldDatabaseName, CommonUtils.handleTableNameSql(tableNames), ip,
            username, password);
    String maxNum = getTableRemoteMaxNum(oldDatabaseName, fitTableNameList, ip, username, password);
    int i = 0;
    for (String tableName : fitTableNameList) {
      String remoteSql = "insert into ";
      remoteSql += newDatabaseName + "." + tableName + " select * from remote('" + ip + "',"
          + oldDatabaseName + "."
          + tableName + ",'" + username + "','" + password + "')" + " limit " + maxNum;
      i++;
      try {
        //truncate table命令只能对子表有效。所以如果输入的tableNames中包含总表名称。数据不会被清空
        if ("1".equals("isTruncate")) {
          remoteDataUpdateStructureMapper.truncateTable(newDatabaseName, tableName);
        }
        remoteDataUpdateStructureMapper.updateRemoteData(remoteSql);
        log.info("第" + i + "张表" + tableName + "数据同步成功");
      } catch (Exception e) {
        log.info("第" + i + "张表" + tableName + "数据同步出错,需要查询该表count值验证");
      }
    }
    if (tableNames.contains(",")) {
      String[] paramTableNames = tableNames.split(",");
      List<String> paramTableNamesList = Arrays.asList(paramTableNames);
      for (int m = 0; m < paramTableNamesList.size(); m++) {
        if (!fitTableNameList.contains(paramTableNamesList.get(m))) {
          log.info(paramTableNamesList.get(m) + "在" + newDatabaseName + "数据库中不存在");
        }
      }
    }

    return "同步在新库中存在的表数据结束";
  }
}
