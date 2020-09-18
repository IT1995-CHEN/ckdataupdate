package com.ck.dataupdate.service;

/**
 * @author ChenLei
 * @date 2020/09/18
 */
public interface RemoteDataUpdateStructureService {


  /**
   * @param oldDatabaseName
   * @param newDatabaseName
   * @return
   */
  String updateRemoteStructure(String oldDatabaseName, String newDatabaseName, String ip,
      String username, String password);


  /**
   * @param oldDatabaseName
   * @param newDatabaseName
   * @return
   */
  String updateRemoteData(String oldDatabaseName, String newDatabaseName, String isCluster,
      String suffixName, String ip, String username, String password);


  /**
   * @param oldDatabaseName
   * @param newDatabaseName
   * @return
   */
  String updateRemoteTableData(String oldDatabaseName, String newDatabaseName, String tableNames,
      String isTruncate, String ip, String username, String password);
}
