package org.csource.quickstart.processor;

import org.csource.quickstart.FResult;
import org.csource.quickstart.request.UploadRequest;

import com.alibaba.fastjson.JSONObject;

public interface FileUploadProcessor {

  boolean care(UploadRequest uploadFileRequest);

  //执行文件处理模板接口
  FResult<JSONObject> process(UploadRequest request) throws Exception;
  
}
