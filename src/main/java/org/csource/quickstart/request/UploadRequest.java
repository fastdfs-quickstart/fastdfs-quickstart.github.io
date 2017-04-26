package org.csource.quickstart.request;

import static org.csource.quickstart.request.Params.HEADER_UPLOAD_JSON;
import static org.csource.quickstart.request.Params.PARAM_FILENAME;
import static org.csource.quickstart.request.Params.PARAM_SUFFIX;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.csource.quickstart.Constants;
import org.csource.quickstart.HttpResponseCode;
import org.csource.quickstart.exception.ParseUploadParamException;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author SongJian email:1738042258@QQ.COM
 * 
 *  吧请求参数和内容都放在这个对象里面了,在后面的操作中,都可以从这个对象中获取各种信息
 */
@Slf4j
public class UploadRequest {

  private HttpServletRequest request; // HTTP请求
  // 使用org.json.JSONObject是防止该传递int参数的地方,客户端传递了""这种问题,增强鲁棒性
  private JSONObject uploadJSON; // 上传请求的参数
  private String originalFilename; // 文件上传上来时的文件名
  private String originalExtName; // 文件上传上来时的扩展名
  private String temporaryFileName;// 临时文件名
  private String temporaryFilePath; // 本地上传文件 缓存在web服务器中的临时文件
  private long temporaryFileSize; // 临时文件大小
  private String temporaryFileMd5;// 临时文件的MD5值在AbstractFileProcessor中统一处理
  private boolean streamMode;// 在已非Form形式上传文件时.标记为true
  
  
  public String getSuffix() {
    return uploadJSON.optString(PARAM_SUFFIX);
  }
  
  public static UploadRequest parseHttpRequest(HttpServletRequest request) throws ParseUploadParamException, JSONException {
    UploadRequest currentRequest = new UploadRequest();
      currentRequest.setStreamMode(true);// 流式
      String jsonreq = request.getHeader(HEADER_UPLOAD_JSON);
      if (StringUtils.isBlank(jsonreq)) {
          throw new ParseUploadParamException(HttpResponseCode.PARAM_DISPLAY, "UPLOAD-JSON参数内容为空");
      }
      // 协议大小超过限额
      if (jsonreq.length() > Constants.getProtocolMaxSize()) {
          throw new ParseUploadParamException(HttpResponseCode.PROTOCOL_STACK_OUT_OF_RANGE, "UPLOAD-JSON参数内容超过协议栈大小");
      }
      try {
          String deString = URLDecoder.decode(jsonreq, "UTF-8");
          jsonreq = deString;
      } catch (UnsupportedEncodingException decodeException) {
          throw new ParseUploadParamException(HttpResponseCode.PARAM_VALUE_ERROR, "UPLOAD-JSON参数无效");
      }

      JSONObject uploadJSON = null;
      try {
          uploadJSON = new JSONObject(jsonreq);
      } catch (Exception e) {
          throw new ParseUploadParamException(HttpResponseCode.CLIENT_PARAM_INVALID, "参数内容无效");
      }
      log.debug("upload-json-params:" + uploadJSON.toString());
      // 检查文件后缀.那必须得有
      String suffix = uploadJSON.optString(PARAM_SUFFIX);
      // 文件扩展名
      if (StringUtils.isNotBlank(suffix)) {
          // 扩展名检查
          if (Constants.limitSuffix() && !ArrayUtils.contains(Constants.getSupportFileSuffix(), suffix)) {
              throw new ParseUploadParamException(HttpResponseCode.PARAM_VALUE_ERROR, "不支持的扩展名:" + suffix);
          }
      } else {
          uploadJSON.put(PARAM_SUFFIX, "");
      }
      currentRequest.setUploadJSON(uploadJSON);
      currentRequest.setRequest(request);
      currentRequest.setOriginalFilename(uploadJSON.optString(PARAM_FILENAME));
      return currentRequest;
  }

  /**
   * 
   * @param request
   * @return
   * @throws ParseUploadParamException
   * @throws JSONException
   */
  public static UploadRequest parseFormRequest(HttpServletRequest request) throws ParseUploadParamException, JSONException {
      UploadRequest fileRequest = new UploadRequest();
      String jsonReq = request.getParameter(HEADER_UPLOAD_JSON);
      if (StringUtils.isBlank(jsonReq)) {
          throw new ParseUploadParamException(HttpResponseCode.PARAM_DISPLAY, "UPLOAD-JSON参数内容为空");
      }
      // 协议大小超过限额
      if (jsonReq.length() > Constants.getProtocolMaxSize()) {
          throw new ParseUploadParamException(HttpResponseCode.PROTOCOL_STACK_OUT_OF_RANGE, "UPLOAD-JSON参数内容超过协议栈大小");
      }
      try {
          String deString = URLDecoder.decode(jsonReq, "UTF-8");
          jsonReq = deString;
      } catch (UnsupportedEncodingException decodeException) {
          throw new ParseUploadParamException(HttpResponseCode.PARAM_VALUE_ERROR, "UPLOAD-JSON参数无效");
      }
      JSONObject uploadJSON = null;
      try {
          uploadJSON = new JSONObject(jsonReq);
      } catch (Exception e) {
          throw new ParseUploadParamException(HttpResponseCode.CLIENT_PARAM_INVALID, "参数内容无效");
      }
      log.debug("upload-json-params:" + uploadJSON.toString());
      // 检查文件后缀.那必须得有
      String suffix = uploadJSON.optString(PARAM_SUFFIX);
      // 文件扩展名
      if (StringUtils.isNoneBlank(suffix)) {
          // 扩展名检查
          if (Constants.limitSuffix() && !ArrayUtils.contains(Constants.getSupportFileSuffix(), suffix)) {
              throw new ParseUploadParamException(HttpResponseCode.PARAM_VALUE_ERROR, "不支持的扩展名:" + suffix);
          }
      } else {
          uploadJSON.put(PARAM_SUFFIX, "");
      }
      fileRequest.setRequest(request);// set HttpServletRequest
      fileRequest.setUploadJSON(uploadJSON);// 设置uploadJSON
      fileRequest.setOriginalFilename(uploadJSON.optString(PARAM_FILENAME));
      return fileRequest;
  }


  public HttpServletRequest getRequest() {
    return request;
  }


  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }


  public JSONObject getUploadJSON() {
    return uploadJSON;
  }


  public void setUploadJSON(JSONObject uploadJSON) {
    this.uploadJSON = uploadJSON;
  }


  public String getOriginalFilename() {
    return originalFilename;
  }


  public void setOriginalFilename(String originalFilename) {
    this.originalFilename = originalFilename;
  }


  public String getOriginalExtName() {
    return originalExtName;
  }


  public void setOriginalExtName(String originalExtName) {
    this.originalExtName = originalExtName;
  }


  public String getTemporaryFileName() {
    return temporaryFileName;
  }


  public void setTemporaryFileName(String temporaryFileName) {
    this.temporaryFileName = temporaryFileName;
  }


  public String getTemporaryFilePath() {
    return temporaryFilePath;
  }


  public void setTemporaryFilePath(String temporaryFilePath) {
    this.temporaryFilePath = temporaryFilePath;
  }


  public long getTemporaryFileSize() {
    return temporaryFileSize;
  }


  public void setTemporaryFileSize(long temporaryFileSize) {
    this.temporaryFileSize = temporaryFileSize;
  }


  public String getTemporaryFileMd5() {
    return temporaryFileMd5;
  }


  public void setTemporaryFileMd5(String temporaryFileMd5) {
    this.temporaryFileMd5 = temporaryFileMd5;
  }


  public boolean isStreamMode() {
    return streamMode;
  }


  public void setStreamMode(boolean streamMode) {
    this.streamMode = streamMode;
  }
  
  
  
}
