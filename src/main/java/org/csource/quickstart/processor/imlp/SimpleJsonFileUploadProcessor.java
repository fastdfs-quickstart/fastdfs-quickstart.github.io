package org.csource.quickstart.processor.imlp;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.csource.quickstart.FResult;
import org.csource.quickstart.HttpResponseCode;
import org.csource.quickstart.annotation.ProcessorOrder;
import org.csource.quickstart.orm.FileService;
import org.csource.quickstart.orm.UploadFile;
import org.csource.quickstart.processor.AbstractFileProcessor;
import org.csource.quickstart.processor.FileUploadProcessor;
import org.csource.quickstart.request.UploadRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ProcessorOrder(order = 1)
public class SimpleJsonFileUploadProcessor extends AbstractFileProcessor implements FileUploadProcessor {

  @Resource
  private FileService commonFileService;

  @Override
  public boolean care(UploadRequest uploadFileRequest) {
    return uploadFileRequest.getSuffix().equals("json");
  }

  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
  public FResult<JSONObject> process(UploadRequest request) throws Exception {
    Long commonFileId = null;
    try {
      File temporaryFile = new File(request.getTemporaryFilePath());
      if (temporaryFile == null || !temporaryFile.isFile() || !temporaryFile.exists()) {
        return FResult.newFailure(HttpResponseCode.SERVER_IO_READ, "上传失败");
      }
      String currentFileMd5 = request.getTemporaryFileMd5();
      String fileId = uploadFileReturnFileId(request.getTemporaryFilePath(), request.getSuffix());
      if (StringUtils.isBlank(fileId)) {
        return FResult.newFailure(HttpResponseCode.SERVER_IO_WRITE, "上传文件到FastDFS失败,请检查FastDFS日志");
      }
      long nowTimestamp = System.currentTimeMillis();
      // 要保存的对象
      UploadFile commonFile = new UploadFile();
      commonFile.setFilemd5(currentFileMd5);
      // 保存主文件后,返回住表ID
      log.debug("你可以控制是否要持久化这个文件");
      commonFileId = commonFileService.addUploadFile(nowTimestamp, commonFile, temporaryFile, request, fileId);
      if (commonFileId == null || commonFileId == 0) {
        log.error("insert common_file failed,record:" + JSON.toJSONString(commonFile));
        FileUtils.forceDelete(temporaryFile);
        return FResult.newFailure(HttpResponseCode.SERVER_DB_ERROR, "保存上传记录失败");
      }
      return buildResult(request.getOriginalFilename(), request.getTemporaryFileSize(), request.getTemporaryFileMd5(), commonFile.getUrl(),
          commonFile.getId());
    } catch (Exception uploadException) {
      log.error("普通文件上传过程中发生错误", uploadException);
      return FResult.newFailure(HttpResponseCode.SERVER_ERROR, "文件上传过程中发生错误");
    }
  }

}
