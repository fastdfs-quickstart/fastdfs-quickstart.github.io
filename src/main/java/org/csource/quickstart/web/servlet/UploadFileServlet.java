package org.csource.quickstart.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csource.quickstart.FResult;
import org.csource.quickstart.HttpResponseCode;
import org.csource.quickstart.exception.ParseUploadParamException;
import org.csource.quickstart.request.UploadRequest;
import org.csource.quickstart.util.ServletUtil;
import org.csource.quickstart.web.GenericFileProcess;
import org.json.JSONException;

import com.alibaba.fastjson.JSON;

/**
 * @author SongJian email:1738042258@QQ.COM
 * 
 * 如果你是个初学者,有任何问题都可以问我
 */
@WebServlet("/upload-by-servlet")
public class UploadFileServlet extends GenericFileProcess {

  private static final long serialVersionUID = 1L;

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    UploadRequest uploadRequest = null;
    try {
      // 公共基础参数校验
      uploadRequest = UploadRequest.parseHttpRequest(request);
    } catch (ParseUploadParamException e) {
      ServletUtil.responseOutWithJson(response, JSON.toJSONString(FResult.newFailure(e.getCode(), e.getMessage())));
      return;
    } catch (JSONException e) {
      ServletUtil.responseOutWithJson(response,
          JSON.toJSONString(FResult.newFailure(HttpResponseCode.CLIENT_PARAM_INVALID, e.getMessage())));
      return;
    }
    // 上传到硬盘
    FResult<String> uploadToHDResult = uploadToHD(uploadRequest, getTemporaryFileFolderPath(), request);
    if (!uploadToHDResult.success()) {
      ServletUtil.responseOutWithJson(response, JSON.toJSONString(uploadToHDResult));
      return;
    }
    ServletUtil.responseOutWithJson(response, processsFacade(request, response, uploadRequest));
  }

  /**
   * HTTP通过流上传文件,非file域上传.SPRINGMVC行不通,只有SERVLET!
   */
  @Override
  protected FResult<String> uploadToHD(UploadRequest uploadRequest, String saveTempFileDirectory, HttpServletRequest request) {
    return super.transferServletRequestStreamToHD(uploadRequest, saveTempFileDirectory, request);
  }

}
