package com.ck.dataupdate.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author ChenLei
 * @date 2020/09/18
 */

@Mapper
@Repository
public interface RemoteDataUpdateStructureMapper {


  List<String> getRemoteDataBase(@Param("ip") String ip,
      @Param("username") String username, @Param("password") String password);

  /**
   * @return
   */
  String isDataBase(@Param("dataBaseName") String dataBaseName,
      @Param("tableName") String tableName);


  boolean dropDataBase(@Param("dataBaseName") String dataBaseName);


  boolean createDataBase(@Param("dataBaseName") String dataBaseName);


  List<String> getRemoteTableNames(@Param("dataBaseName") String dataBaseName,
      @Param("ip") String ip, @Param("username") String username,
      @Param("password") String password);


  String getRemoteTableStructure(@Param("dataBaseName") String dataBaseName,
      @Param("tableName") String tableName, @Param("ip") String ip,
      @Param("username") String username, @Param("password") String password);


  boolean createRemoteTableStructure(@Param("sql") String sql);


  boolean insertData(@Param("sql") String sql);

  //同数据源同步数据

  /**
   * @param sql
   * @return
   */
  String getMaxNum(@Param("sql") String sql);

  /**
   * @param sql
   * @return
   */
  boolean updateRemoteData(@Param("sql") String sql);


  /**
   * @param dataBaseName
   * @param tableName
   * @return
   */
  boolean truncateTable(@Param("dataBaseName") String dataBaseName,
      @Param("tableName") String tableName);

  List<String> getNotLikeRemoteTableNames(@Param("suffixName") String suffixName,
      @Param("dataBaseName") String dataBaseName, @Param("ip") String ip,
      @Param("username") String username, @Param("password") String password);

  List<String> getLikeRemoteTableNames(@Param("suffixName") String suffixName,
      @Param("dataBaseName") String dataBaseName, @Param("ip") String ip,
      @Param("username") String username, @Param("password") String password);

  List<String> getSomeFitTableNames(@Param("dataBaseName") String dataBaseName,
      @Param("tableNames") String tableNames, @Param("ip") String ip,
      @Param("username") String username, @Param("password") String password);
}
