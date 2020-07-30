package com.ck.dataupdate.service.impl;


import com.ck.dataupdate.mapper.DataUpdateStructureMapper;
import com.ck.dataupdate.service.DataUpdateStructureService;
import java.util.ArrayList;
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

  public boolean isHaving(String name, List<String> list) {
    for (String sname : list) {
      if (name.equals(sname)) {
        return true;
      }
    }
    return false;
  }


  @Override
  public String updateStructure(String oldDatabaseName, String newDatabaseName) {

    List<String> getDataBaseList = dataUpdateStructureMapper.getDataBase();

    if (getDataBaseList.size() == 0) {
      return "该ck服务器中没有一个库存在";
    } else {
      boolean a = isHaving(oldDatabaseName, getDataBaseList);
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
    List<String> tableNameList = dataUpdateStructureMapper.getTableNames();
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
      tableNameList = dataUpdateStructureMapper.getTableNames();
      logInfo = "非集群表数据同步成功";
    } else {
      tableNameList = dataUpdateStructureMapper.getNotLikeTableNames(suffixName, oldDatabaseName);
      logInfo = "单个节点集群总表数据同步成功";
    }

    if (tableNameList.size() == 0) {
      return "源数据库无表，数据同步成功";
    }
    if (tableNameList.size() == 1) {
      tableSql = "select count(*) as num from " + tableNameList.get(0) + ")";

    }

    for (int i = 0; i < tableNameList.size(); i++) {

      if (i == tableNameList.size() - 1) {
        tableSql += "select count(*) as num from " + tableNameList.get(i) + ")";
        sql += tableSql;

        break;
      }
      tableSql += "select count(*) as num from " + tableNameList.get(i) + " UNION ALL ";
    }
    String maxNum = dataUpdateStructureMapper.getMaxNum(sql);
    int i = 0;
    for (String tableName : tableNameList) {
      String remoteSql = "insert into ";
      remoteSql += newDatabaseName + "." + tableName + " select * from " + oldDatabaseName + "."
          + tableName + " limit " + maxNum;

      i++;
      try {
        dataUpdateStructureMapper.truncateTable(newDatabaseName, tableName);
        dataUpdateStructureMapper.updateRemoteData(remoteSql);
        log.info("第" + i + "张表数据同步成功");
      } catch (Exception e) {
        log.info("第" + i + "张表数据同步失败");
      }
    }
    return logInfo;

  }
}
