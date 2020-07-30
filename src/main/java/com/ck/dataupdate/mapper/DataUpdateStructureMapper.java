package com.ck.dataupdate.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author: chenlei
 * @date: 2020/5/12 18:00
 */
@Mapper
@Repository
public interface DataUpdateStructureMapper {


  @Select("show databases")
  List<String> getDataBase();

  /**
   * @return
   */
  String isDataBase(@Param("dataBaseName") String dataBaseName,
      @Param("tableName") String tableName);


  boolean dropDataBase(@Param("dataBaseName") String dataBaseName);


  boolean createDataBase(@Param("dataBaseName") String dataBaseName);


  List<String> getTableNames();


  String getTableStructure(@Param("dataBaseName") String dataBaseName,
      @Param("tableName") String tableName);


  boolean createTableStructure(@Param("sql") String sql);


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

  List<String> getNotLikeTableNames(@Param("suffixName") String suffixName,
      @Param("dataBaseName") String dataBaseName);
}
