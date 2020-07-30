package com.ck.dataupdate.pojo;

import com.ck.dataupdate.enums.ResultEnums;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Title: CommonResultT
 * @Description: 自定义响应结构
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonResultT<T> implements Serializable {

  /**
   * 响应业务状态
   */
  private Integer status;

  /**
   * 响应消息
   */
  private String msg;

  /**
   * 响应中的数据
   */
  private T data;


  public static <T> CommonResultT<T> build(Integer status, String msg, T data) {
    return new CommonResultT<>(status, msg, data);
  }

  public static <T> CommonResultT<T> ok(T data) {
    return new CommonResultT<>(data);
  }

  public static <T> CommonResultT<T> ok() {
    return new CommonResultT<>(null);
  }

  public static <T> CommonResultT<T> build(Integer status, String msg) {
    return new CommonResultT<>(status, msg, null);
  }

  public CommonResultT(T data) {
    this.status = ResultEnums.SUCCESS.getCode();
    this.msg = ResultEnums.SUCCESS.getMsg();
    this.data = data;
  }

}
