package com.ck.dataupdate.controller;


import com.ck.dataupdate.pojo.CommonResultT;
import com.ck.dataupdate.service.DataUpdateStructureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 数据库更新表结构同步
 * @author: chenlei
 * @date: 2020/5/12 17:02
 */
@RestController
@Api(tags = {"数据库更新表结构同步"})
@RequestMapping("/api/dataUpdateStructure")
@Slf4j

///api/dataUpdateStructure/table
public class DataUpdateStructureController {

  @Autowired
  private DataUpdateStructureService dataUpdateStructureService;

  /**
   * 同步表结构
   */
  @ApiOperation(value = "clickhouse数据库表结构同步", notes = "新库和旧库同步")
  @ApiImplicitParams({
      @ApiImplicitParam(value = "源头库名称", name = "oldDataBaseName", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "新库名称", name = "newDataBaseName", required = true, paramType = "query", dataType = "String"),
  })
  @GetMapping("/table")
  public CommonResultT dataUpdateStructure(
      String oldDataBaseName, String newDataBaseName) {

    try {
      return CommonResultT.ok(dataUpdateStructureService
          .updateStructure(oldDataBaseName, newDataBaseName));
    } catch (Exception e) {
      e.printStackTrace();
      return CommonResultT.build(500, "同步数据表结构错误");
    }
  }

  /**
   * 同步集群和非集群表单台服务器数据
   */
  @ApiOperation(value = "clickhouse数据库表数据同步", notes = "新库和旧库同步")
  @ApiImplicitParams({
      @ApiImplicitParam(value = "源头库名称", name = "oldDataBaseName", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "目标库名称", name = "newDataBaseName", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "是否集群,0为同步库中所有表数据、1为同步库中不为集群后缀的表数据、2为同步库中拥有集群表后缀的表数据;注意0用于非集群库，1和2常用语集群库,执行2前先确认子表数据是否都已经清空;", name = "isCluster", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "当为集群时，集群后缀名，例如_total", name = "suffixName", required = true, paramType = "query", dataType = "String")
  })
  @GetMapping("/data")
  public CommonResultT dataUpdateData(
      String oldDataBaseName, String newDataBaseName, String isCluster, String suffixName) {

    try {
      return CommonResultT.ok(dataUpdateStructureService
          .updateData(oldDataBaseName, newDataBaseName, isCluster, suffixName));
    } catch (Exception e) {
      e.printStackTrace();
      return CommonResultT.build(500, "同步数据表结构错误");
    }
  }

  /**
   * 同步在新库中存在的表数据
   */
  @ApiOperation(value = "同步单、多表数据", notes = "表数据同步")
  @ApiImplicitParams({
      @ApiImplicitParam(value = "源头库名称", name = "oldDataBaseName", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "目标库名称", name = "newDataBaseName", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "固定表名称", name = "tableNames", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "是否需要先清空表。只对子表有效。1、为需要清空。注意：如果涉及到总表需要先去库中先清空", name = "isTruncate", required = true, paramType = "query", dataType = "String"),

  })
  @GetMapping("/tableData")
  public CommonResultT tableDataUpdateData(
      String oldDataBaseName, String newDataBaseName, String tableNames,String isTruncate) {

    try {
      return CommonResultT.ok(dataUpdateStructureService
          .updateTableData(oldDataBaseName, newDataBaseName, tableNames,isTruncate));
    } catch (Exception e) {
      e.printStackTrace();
      return CommonResultT.build(500, "表数据同步错误");
    }
  }
}

