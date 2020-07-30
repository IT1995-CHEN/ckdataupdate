package com.ck.dataupdate.enums;

/**
 * @author ChenLei
 * @Title: ResultEnums
 */

public enum ResultEnums {

  /**
   * 成功
   */
  SUCCESS(200, "ok"),
  /**
   * 失败
   */
  ERROR(500, "error");

  private Integer code;
  private String msg;

  ResultEnums(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public Integer getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

}
