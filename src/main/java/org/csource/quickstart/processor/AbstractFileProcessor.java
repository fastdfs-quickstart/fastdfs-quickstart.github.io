package org.csource.quickstart.processor;

import static org.csource.quickstart.request.Params.PARAM_NOT_APPEND_FILENAME;

import org.apache.commons.io.FilenameUtils;
import org.csource.quickstart.Constants;
import org.csource.quickstart.FResult;
import org.csource.quickstart.client.FastDFSService;
import org.csource.quickstart.request.UploadRequest;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractFileProcessor {
  
  protected static FastDFSService fastDFSService;

  public static void initFastDFSService(FastDFSService fastDFSService_) {
    fastDFSService = fastDFSService_;
  }

  public FastDFSService getFastDFSService() {
    return fastDFSService;
  }

  public String getFullLevel2DomainMappingToStorage(String storageFileId) {
    return fastDFSService.level2DomainMappingToStorageEndWithSplitor() + storageFileId;
  }

  protected String getFullLevel2DomainMappingToStorage(String storageFileId,UploadRequest uploadRequest) {
    String result = getFullLevel2DomainMappingToStorage(storageFileId);
    if (uploadRequest != null) {
      Boolean notAppendFilename =
          uploadRequest.getUploadJSON().optBoolean(PARAM_NOT_APPEND_FILENAME);
      if (notAppendFilename != null && notAppendFilename) {

      } else {
        result = result + "?filename=" + uploadRequest.getOriginalFilename();
      }
    }
    return result;
  }

  protected String uploadFileReturnFileId(String path) {
    String fileId = null;
    int maxRetryTimes = 0;
    boolean uploadSubFileSuccess = false;
    while (!uploadSubFileSuccess && (maxRetryTimes < Constants.getMaxRetryUploadFastDFSTimes())) {
      try {
        fileId = fastDFSService.upload_file1(path, FilenameUtils.getExtension(path));
        uploadSubFileSuccess = true;
      } catch (Exception uploadException) {
        log.error("上传文件到FastDFS失败,本地文件路径" + path + ",请检查FastDFS日志", uploadException);
        maxRetryTimes++;
      }
    }
    return fileId;
  }

  protected String uploadFileReturnFileId(String path, String suffix) {
    String fileId = null;
    int maxRetryTimes = 0;
    boolean uploadSubFileSuccess = false;
    while (!uploadSubFileSuccess && (maxRetryTimes < Constants.getMaxRetryUploadFastDFSTimes())) {
      try {
        fileId = fastDFSService.upload_file1(path, suffix);
        uploadSubFileSuccess = true;
      } catch (Exception uploadException) {
        log.error("上传文件到FastDFS失败,本地文件路径" + path + ",请检查FastDFS日志", uploadException);
        maxRetryTimes++;
      }
    }
    return fileId;
  }

  protected String uploadFileBytesReturnFileId(byte[] bytes, String suffix) {
    String fileId = null;
    int maxRetryTimes = 0;
    boolean uploadSubFileSuccess = false;
    while (!uploadSubFileSuccess && (maxRetryTimes < Constants.getMaxRetryUploadFastDFSTimes())) {
      try {
        fileId = fastDFSService.upload_file_bytes(bytes, suffix);
        uploadSubFileSuccess = true;
      } catch (Exception uploadException) {
        log.error("上传字节到FastDFS失败,请检查FastDFS日志", uploadException);
        maxRetryTimes++;
      }
    }
    return fileId;
  }
  
  protected FResult<JSONObject> buildResult(String originalFilename, Long fileSize, String md5,String url, Long fileid) {
    JSONObject res = new JSONObject();
    res.put("originalFilename", originalFilename);
    res.put("fileSize", fileSize);
    res.put("md5", md5);
    res.put("download", url);
    res.put("fileid", fileid);
    return FResult.newSuccess(res);
  }
  
}