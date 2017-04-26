package org.csource.quickstart.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csource.quickstart.FResult;
import org.csource.quickstart.HttpResponseCode;
import org.csource.quickstart.client.FastDFSService;
import org.csource.quickstart.exception.ParseUploadParamException;
import org.csource.quickstart.request.UploadRequest;
import org.csource.quickstart.web.GenericFileProcess;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author SongJian email:1738042258@QQ.COM
 * 
 */
@Controller
public class FileUploadController extends GenericFileProcess {

  private static final long serialVersionUID = 1L;

  @Resource
  private FastDFSService fastDFSService;

  @RequestMapping(value = "/hello", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public FResult<String> hello(){
    return FResult.newSuccess("hehe");
  }
  
  /**
   * 表单上传应用的入口
   * 
   * @param public_access_auth 默认需要进行验证,开发时需要指定为false
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/form-upload-file", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public FResult<?> form_upload(HttpServletRequest request, HttpServletResponse response) {
    UploadRequest uploadRequest = null;
    try {
      uploadRequest = UploadRequest.parseFormRequest(request);
    } catch (ParseUploadParamException e) {
      return FResult.newFailure(e.getCode(), e.getMessage());
    } catch (Exception e) {
      return FResult.newFailure(HttpResponseCode.SERVER_ERROR,e.getMessage());
    }
    // 上传到硬盘
    FResult<String> uploadToHDResult = uploadToHD(uploadRequest, getTemporaryFileFolderPath(), request);
    if (!uploadToHDResult.success()) {
      return uploadToHDResult;
    }
    return processsFacade(request, response, uploadRequest);
  }

  @Override
  protected org.csource.quickstart.FResult<String> uploadToHD(UploadRequest uploadRequest, String saveTempFileDirectory,
      HttpServletRequest request) {
    return super.transferSpringRequestStreamToHD(uploadRequest, saveTempFileDirectory, request);
  }

}
