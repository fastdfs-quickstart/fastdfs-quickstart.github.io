package org.csource.quickstart.processor.imlp;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.csource.quickstart.FResult;
import org.csource.quickstart.HttpResponseCode;
import org.csource.quickstart.annotation.ProcessorOrder;
import org.csource.quickstart.processor.AbstractFileProcessor;
import org.csource.quickstart.processor.FileUploadProcessor;
import org.csource.quickstart.request.UploadRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;


@ProcessorOrder
@Service
public class DefaultUploadFileProcessor extends AbstractFileProcessor implements FileUploadProcessor {

  @Override
  public boolean care(UploadRequest uploadFileRequest) {
    return true;
  }

  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
  public FResult<JSONObject> process(UploadRequest request) throws Exception {
    File temporaryFile = new File(request.getTemporaryFilePath());
    FResult<JSONObject> uploadResponseResult = uploadFileToFastDFS(temporaryFile, request);
    return uploadResponseResult;
  }

  public FResult<JSONObject> uploadFileToFastDFS(final File tempFile, UploadRequest uploadRequest) {
    File theTempFile = tempFile;
    if (theTempFile == null || !theTempFile.isFile() || !theTempFile.exists()) {
        return FResult.newFailure(HttpResponseCode.CLIENT_PARAM_INVALID, "本地临时文件:" + tempFile + "不存在");
    }
    String fileId = uploadFileReturnFileId(tempFile.getAbsolutePath());
    if (StringUtils.isBlank(fileId)) {
        return FResult.newFailure(HttpResponseCode.SERVER_IO_WRITE, "上传文件到FastDSF失败,请检查FastDSF日志");
    }
    return buildResult(uploadRequest.getOriginalFilename(), uploadRequest.getTemporaryFileSize(), uploadRequest.getTemporaryFileMd5(),
      getFullLevel2DomainMappingToStorage(fileId), -1L);
}
  
}
