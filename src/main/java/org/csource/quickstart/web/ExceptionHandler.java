package org.csource.quickstart.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.csource.quickstart.FResult;
import org.csource.quickstart.HttpResponseCode;
import org.csource.quickstart.util.ServletUtil;
import org.springframework.core.Ordered;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;



/**
 * @author SongJian email:1738042258@QQ.COM
 * 这个类里面你可以拦截自己的逻辑异常 
 */
public class ExceptionHandler implements HandlerExceptionResolver, Ordered{

  @Override
  public int getOrder() {
      return Integer.MIN_VALUE;
  }

  @Override
  public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                       Exception ex) {
      FResult<String> result = null;
      if (ex instanceof HttpRequestMethodNotSupportedException) {
          HttpRequestMethodNotSupportedException newExp = (HttpRequestMethodNotSupportedException) ex;
          result = FResult.newFailure(HttpResponseCode.CLIENT_PARAM_INVALID, newExp.getMessage());
      } else if (ex instanceof MaxUploadSizeExceededException) {
          result = FResult.newFailure(HttpResponseCode.CLIENT_PARAM_INVALID, "上传文件大小超出限制");
      } else if (ex instanceof SizeLimitExceededException) {
          result = FResult.newFailure(HttpResponseCode.CLIENT_PARAM_INVALID, "上传文件大小超出限制");
      } else {
          result = FResult.newFailure(HttpResponseCode.SERVER_ERROR, ex.getMessage());
      }
      ServletUtil.responseOutWithJson(response, result);
      return null;
  }
  
}
