package org.csource.quickstart;

import java.io.Serializable;


/**
 * @author SongJian email:1738042258@QQ.COM
 * 
 * @param <T>
 */
@SuppressWarnings("serial")
public class FResult<T> implements Serializable {

    private static int DEFAULT_SUCCESS_CODE = HttpResponseCode.SUCCESS_CODE;
    
    private static int DEFAULT_FAIL_CODE = 500;
    public static FResult<String> DEFAULT_FRESULT = FResult.newFailure(404,"请求处理参数错误");

    /**
     * 接口调用成功，不需要返回对象
     */
    public static <T> FResult<T> newSuccess() {
        FResult<T> result = new FResult<>();
        return result;
    }

    /**
     * 接口调用成功，有返回对象
     */
    public static <T> FResult<T> newSuccess(T object) {
        FResult<T> result = new FResult<>();
        result.setCode(DEFAULT_SUCCESS_CODE);
        result.setData(object);
        return result;
    }

    /**
     * 接口调用失败，有错误码和描述，没有返回对象
     */
    public static <T> FResult<T> newFailure(int code, String error) {
        FResult<T> result = new FResult<>();
        result.setCode(code != DEFAULT_SUCCESS_CODE ? code : -1);
        result.setMsg(error);
        return result;
    }

    /**
     * 接口调用失败，有错误字符串码和描述，没有返回对象
     */
    public static <T> FResult<T> newFailure(String error, String message) {
        FResult<T> result = new FResult<>();
        result.setCode(DEFAULT_FAIL_CODE);
        result.setMsg(error);
        return result;
    }

    /**
     * 接口调用失败，返回异常信息
     */
    public static <T> FResult<T> newException(Exception e) {
        FResult<T> result = new FResult<>();
        result.setCode(DEFAULT_FAIL_CODE);
        result.setMsg(e.getMessage());
        return result;
    }

    private int code = DEFAULT_SUCCESS_CODE;
    private T data;
    private String msg;

    /**
     * 判断返回结果是否成功
     */
    public boolean success() {
        return code == DEFAULT_SUCCESS_CODE;
    }

    /**
     * 判断返回结果是否有结果对象
     */
    public boolean hasObject() {
        return code == DEFAULT_SUCCESS_CODE && data != null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
      return msg;
    }

    public void setMsg(String msg) {
      this.msg = msg;
    }

}
