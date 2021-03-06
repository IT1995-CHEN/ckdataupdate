package com.ck.dataupdate.service;


public interface DataUpdateStructureService {

  /**
   * @param oldDatabaseName
   * @param newDatabaseName
   * @return
   */
  String updateStructure(String oldDatabaseName, String newDatabaseName);

  /**
   * @param oldDatabaseName
   * @param newDatabaseName
   * @return
   */
  String updateData(String oldDatabaseName, String newDatabaseName, String isCluster,
      String suffixName);
}
