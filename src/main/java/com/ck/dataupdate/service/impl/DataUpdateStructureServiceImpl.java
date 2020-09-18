package com.ck.dataupdate.service.impl;


import com.ck.dataupdate.mapper.DataUpdateStructureMapper;
import com.ck.dataupdate.service.DataUpdateStructureService;
import com.ck.dataupdate.utils.CommonUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author ChenLei
 */
@Repository
@Slf4j
public class DataUpdateStructureServiceImpl implements DataUpdateStructureService {

  @Autowired
  private DataUpdateStructureMapper dataUpdateStructureMapper;




  @Override
  public String updateStructure(String oldDatabaseName, String newDatabaseName) {

    List<String> getDataBaseList = dataUpdateStructureMapper.getDataBase();

    if (getDataBaseList.size() == 0) {
      return "该ck服务器中没有一个库存在";
    } else {
      boolean a = CommonUtils.isHaving(oldDatabaseName, getDataBaseList);
      if (a == false) {
        return "该ck服务器所有库中尚无源头库";
      }
    }
    try {
      dataUpdateStructureMapper.dropDataBase(newDatabaseName);
    } catch (Exception e) {
      log.info("删除的数据库名不存在，即将新建库");
    } finally {
      dataUpdateStructureMapper.createDataBase(newDatabaseName);
    }
    List<String> tableNameList = dataUpdateStructureMapper.getTableNames(oldDatabaseName);
    if (tableNameList.size() == 0) {
      return "源头库无表，同步数据库成功";
    }
    for (String tableName : tableNameList) {
      String tableSql = dataUpdateStructureMapper.getTableStructure(oldDatabaseName, tableName);

      tableSql = tableSql.replaceAll(oldDatabaseName, newDatabaseName);

      dataUpdateStructureMapper.createTableStructure(tableSql);
    }
    return "同步表结构数据成功";
  }

  @Override
  public String updateData(String oldDatabaseName, String newDatabaseName, String isCluster,
      String suffixName) {
    String sql = "select max(num) from (";
    String tableSql = new String();
    String logInfo = new String();
    List<String> tableNameList = new ArrayList<>();
    System.out.println("0".equals(isCluster));

    if ("0".equals(isCluster)) {
      tableNameList = dataUpdateStructureMapper.getTableNames(oldDatabaseName);
      logInfo = "同步库中所有表数据成功";
    } else if ("1".equals(isCluster)) {
      tableNameList = dataUpdateStructureMapper.getNotLikeTableNames(suffixName, oldDatabaseName);
      logInfo = "同步库中不为集群后缀的表数据成功";
    } else {
      tableNameList = dataUpdateStructureMapper.getLikeTableNames(suffixName, oldDatabaseName);
      logInfo = "同步库中拥有集群表后缀的表数据成功";
    }

    if (tableNameList.size() == 0) {
      return "源数据库无表，数据同步成功";
    }
    System.out.println(tableNameList.size());
    if (tableNameList.size() == 1) {
      sql =
          "select count(*) as num from " + oldDatabaseName + "." + tableNameList.get(0) ;

    }else {
      for (int i = 0; i < tableNameList.size(); i++) {

        if (i == tableNameList.size() - 1) {
          tableSql +=
              "select count(*) as num from " + oldDatabaseName + "." + tableNameList.get(i) + ")";
          sql += tableSql;

          break;
        }
        tableSql += "select count(*) as num from " + oldDatabaseName + "." + tableNameList.get(i)
            + " UNION ALL ";
      }
    }



    System.out.println(sql);
    String maxNum = dataUpdateStructureMapper.getMaxNum(sql);
    System.out.println(maxNum);
    int i = 0;
    for (String tableName : tableNameList) {
      String remoteSql = "insert into ";
      remoteSql += newDatabaseName + "." + tableName + " select * from " + oldDatabaseName + "."
          + tableName + " limit " + maxNum;
      System.out.println(remoteSql);
      i++;
      try {
        if ("2".equals(isCluster)) {
          dataUpdateStructureMapper.updateRemoteData(remoteSql);
          log.info("第" + i + "张表" + tableName + "在" + newDatabaseName + "数据同步成功");
          continue;
        }
        dataUpdateStructureMapper.truncateTable(newDatabaseName, tableName);
        dataUpdateStructureMapper.updateRemoteData(remoteSql);
        log.info("第" + i + "张表" + tableName + "在" + newDatabaseName + "数据同步成功");
      } catch (Exception e) {
        log.info("第" + i + "张表" + tableName + "在" + newDatabaseName + "数据同步出错,需要查询该表count值验证");
      }
    }
    return logInfo;

  }

  public String getTableMaxNum(String database, List<String> tableNameList) {
    String sql = "select max(num) from (";

    String tableSql = new String();

    if (tableNameList.size() == 0) {
      return "0";
    }
    for (int i = 0; i < tableNameList.size(); i++) {
      if (tableNameList.size() == 1) {
        tableSql = "select count(*) as num from " + database + "." + tableNameList.get(0) + ")";
        sql = tableSql;
        break;
      }
      if (i == tableNameList.size() - 1) {
        tableSql +=
            "select count(*) as num from " + database + "." + tableNameList.get(i) + ")";
        sql += tableSql;
        break;
      }
      tableSql += "select count(*) as num from " + database + "." + tableNameList.get(i)
          + " UNION ALL ";
    }
    String maxNum = dataUpdateStructureMapper.getMaxNum(sql);
    return maxNum;
  }

  @Override
  public String updateTableData(String oldDatabaseName, String newDatabaseName, String tableNames,
      String isTruncate) {
    List<String> fitTableNameList = dataUpdateStructureMapper
        .getSomeFitTableNames(oldDatabaseName, CommonUtils.handleTableNameSql(tableNames));
    String maxNum = getTableMaxNum(oldDatabaseName, fitTableNameList);
    int i = 0;
    for (String tableName : fitTableNameList) {
      String remoteSql = "insert into ";
      remoteSql += newDatabaseName + "." + tableName + " select * from " + oldDatabaseName + "."
          + tableName + " limit " + maxNum;
      i++;
      try {
        //truncate table命令只能对子表有效。所以如果输入的tableNames中包含总表名称。数据不会被清空
        if ("1".equals(isTruncate)) {
          dataUpdateStructureMapper.truncateTable(newDatabaseName, tableName);
        }
        dataUpdateStructureMapper.updateRemoteData(remoteSql);
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
