package com.ck.dataupdate.controller;

import com.ck.dataupdate.pojo.CommonResultT;
import com.ck.dataupdate.service.RemoteDataUpdateStructureService;
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
 * @author ChenLei
 * @date 2020/09/18
 */

@RestController
@Api(tags = {"不同ip数据库间更新表结构同步"})
@RequestMapping("/api/dataUpdateRemoteStructure")
@Slf4j
public class RemoteDataUpdateStructureController {

  @Autowired
  private RemoteDataUpdateStructureService remoteDataUpdateStructureService;


  /**
   * 不同服务器下ck数据库同步表结构
   */
  @ApiOperation(value = "clickhouse数据库表结构同步", notes = "新库和旧库同步")
  @ApiImplicitParams({
      @ApiImplicitParam(value = "源头库名称", name = "oldDataBaseName", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "新库名称", name = "newDataBaseName", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "oldDataBase所在的ip", name = "ip", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "oldDataBase所在的username", name = "username", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "oldDataBase所在的password", name = "password", required = true, paramType = "query", dataType = "String")

  })
  @GetMapping("/remoteTable")
  public CommonResultT dataUpdateStructure(
      String oldDataBaseName, String newDataBaseName, String ip, String username, String password) {
    try {
      return CommonResultT.ok(remoteDataUpdateStructureService
          .updateRemoteStructure(oldDataBaseName, newDataBaseName, ip, username, password));
    } catch (Exception e) {
      e.printStackTrace();
      return CommonResultT.build(500, "同步数据表结构错误");
    }
  }

  /**
   * 不同服务器下ck数据库同步集群和非集群表单台服务器数据
   */
  @ApiOperation(value = "clickhouse数据库表数据同步", notes = "新库和旧库同步")
  @ApiImplicitParams({
      @ApiImplicitParam(value = "源头库名称", name = "oldDataBaseName", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "目标库名称", name = "newDataBaseName", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "是否集群,0为同步库中所有表数据、1为同步库中不为集群后缀的表数据、2为同步库中拥有集群表后缀的表数据;注意0用于非集群库，1和2常用语集群库,执行2前先确认子表数据是否都已经清空;", name = "isCluster", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "当为集群时，集群后缀名，例如_total", name = "suffixName", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "oldDataBase所在的ip", name = "ip", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "oldDataBase所在的username", name = "username", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "oldDataBase所在的password", name = "password", required = true, paramType = "query", dataType = "String")
  })
  @GetMapping("/remoteData")
  public CommonResultT remoteDataUpdateData(
      String oldDataBaseName, String newDataBaseName, String isCluster, String suffixName,
      String ip, String username, String password) {

    try {
      return CommonResultT.ok(remoteDataUpdateStructureService
          .updateRemoteData(oldDataBaseName, newDataBaseName, isCluster, suffixName, ip, username,
              password));
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
      @ApiImplicitParam(value = "oldDataBase所在的ip", name = "ip", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "oldDataBase所在的username", name = "username", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(value = "oldDataBase所在的password", name = "password", required = true, paramType = "query", dataType = "String")

  })
  @GetMapping("/remoteTableData")
  public CommonResultT tableDataUpdateData(
      String oldDataBaseName, String newDataBaseName, String tableNames, String isTruncate,
      String ip, String username, String password) {
    try {
      return CommonResultT.ok(remoteDataUpdateStructureService
          .updateRemoteTableData(oldDataBaseName, newDataBaseName, tableNames, isTruncate, ip,
              username, password));
    } catch (Exception e) {
      e.printStackTrace();
      return CommonResultT.build(500, "表数据同步错误");
    }
  }
}
