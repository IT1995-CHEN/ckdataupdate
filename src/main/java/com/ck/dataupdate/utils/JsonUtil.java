package com.ck.dataupdate.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author
 * @since JDK 1.8
 */
public class JsonUtil {

  private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

  static ObjectMapper objectMapper;

  private static String charset = "UTF-8";

  /**
   * @param content   要转换的JavaBean类型
   * @param valueType 原始json字符串数据
   * @return JavaBean对象
   * @Description 使用泛型方法，把json字符串转换为相应的JavaBean对象。
   * @Description (1)转换为普通JavaBean：readValue(json,Student.class)
   * @Description (2)转换为List:readValue(json,List.class).但是如果我们想把json转换为特定类型的List，比如List<Student>，就不能直接进行转换了。
   * 因为readValue(json,List.class)返回的其实是List<Map>类型，你不能指定readValue()的第二个参数是List<Student>.class，所以不能直接转换。
   * 我们可以把readValue()的第二个参数传递为Student[].class.然后使用Arrays.asList();方法把得到的数组转换为特定类型的List。
   * @Description (3)转换为Map：readValue(json,Map.class) 我们使用泛型，得到的也是泛型
   */
  public static <T> T readValue(String content, Class<T> valueType) {
    if (objectMapper == null) {
      objectMapper = new ObjectMapper();
    }
    try {
      return objectMapper.readValue(content, valueType);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 把JavaBean转换为json字符串 (1)普通对象转换：toJson(Student) (2)List转换：toJson(List) (3)Map转换:toJson(Map)
   * 我们发现不管什么类型，都可以直接传入这个方法
   *
   * @param object JavaBean对象
   * @return json字符串
   */
  public static String toJson(Object object) {
    if (objectMapper == null) {
      objectMapper = new ObjectMapper();
    }
    try {
      return objectMapper.writeValueAsString(object);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * TODO
   *
   * @param valueType 转化类型
   * @Title:
   * @param: * @param content base64加密内容
   * @return: T
   * @description: base64加密内容转json
   * @throws:
   */
  public static <T> T readBase64Value(String content, Class<T> valueType) {
    if (objectMapper == null) {
      objectMapper = new ObjectMapper();
    }
    try {
      return objectMapper.readValue(decodeBase64(content), valueType);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * TODO
   *
   * @Title: base64加密
   * @param: * @param str   加密内容
   * @return: java.lang.String
   * @description:
   * @throws:
   */
  private static String decodeBase64(String str) {
    try {
      return new String(Base64.decodeBase64(str), charset);
    } catch (UnsupportedEncodingException e) {
      logger.error("HttpClientUtil.decodeBase64 error", e);
    }
    return null;
  }


}


