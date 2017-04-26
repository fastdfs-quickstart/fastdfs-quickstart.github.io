package org.csource.quickstart.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.csource.quickstart.FResult;
import org.csource.quickstart.HttpResponseCode;
import org.csource.quickstart.client.FastDFSService;
import org.csource.quickstart.processor.FileProcessFactory;
import org.csource.quickstart.processor.FileUploadProcessor;
import org.csource.quickstart.request.UploadRequest;
import org.csource.quickstart.util.FileUtils;
import org.csource.quickstart.util.SpringHelper;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * @author SongJian email:1738042258@QQ.COM
 * 
 * 统一处理模板
 */
@Slf4j
public abstract class GenericFileProcess extends HttpServlet {

  private static final long serialVersionUID = -3876937591302171185L;
  
  /**
   * 提供有序的处理器链
   */
  private FileProcessFactory fileProcessFactory = SpringHelper.getBean(FileProcessFactory.class);
  
  protected String getTemporaryFileFolderPath() {
    return getBean(FastDFSService.class).getLocalTempFilePath();
  }
  
  protected List<FileUploadProcessor> getFileUploadProcessors() throws Exception {
    @SuppressWarnings("unchecked")
    List<FileUploadProcessor> allFileUploadProcessor = (List<FileUploadProcessor>) fileProcessFactory.getObject();
    return allFileUploadProcessor;
  }

  /**
   * 
   * @param request
   * @param response
   * @param uploadRequest
   */
  public FResult<?> processsFacade(HttpServletRequest request, HttpServletResponse response,UploadRequest uploadRequest) {
    // 如果请求参数中没有携带filename参数,则使用UUID生成的文件名作为下载的文件名
    if (StringUtils.isBlank(uploadRequest.getOriginalFilename())) {
      uploadRequest.setOriginalFilename(uploadRequest.getTemporaryFileName());
    }
    try {
      FastDFSService fastDFSService = getBean(FastDFSService.class);
      // 检查临时目录是否存在
      File saveTempFileDirectory = new File(fastDFSService.getLocalTempFilePath());
      if (!saveTempFileDirectory.isDirectory() || !saveTempFileDirectory.exists()) {
        try {
          saveTempFileDirectory.mkdirs();
          log.warn("服务器临时目录{}不存在,已经新建", fastDFSService.getLocalTempFilePath());
        } catch (Exception createTempFileDirException) {
          log.error("服务器临时目录" + fastDFSService.getLocalTempFilePath() + "不存在,当前启动账号没有权限创建目录",
              createTempFileDirException);
          return FResult.newFailure(HttpResponseCode.SERVER_Env_ERROR, "服务器临时目录不存在,当前启动账号没有权限创建目录");
        }
      }
      uploadRequest.setTemporaryFileMd5(FileUtils.md5(uploadRequest.getTemporaryFilePath()));
      // 取出文件处理器列表
      List<FileUploadProcessor> allFileUploadProcessor = getFileUploadProcessors();
      // 声明一个处理结果
      FResult<?> processResult = FResult.DEFAULT_FRESULT;
      //
      if (allFileUploadProcessor.size() > 0) {
        for (FileUploadProcessor fileUploadProcessor : allFileUploadProcessor) {
          if (fileUploadProcessor.care(uploadRequest)) {
            log.debug("processor:"+fileUploadProcessor.getClass().getCanonicalName());
            processResult = fileUploadProcessor.process(uploadRequest);
            break;
          }
        }
      }
      log.debug("upload-servlet-response :" + JSON.toJSONString(processResult));
      return processResult;
    } catch (Exception e) {
      log.error("上传文件发生异常", e);
      return FResult.newFailure(HttpResponseCode.SERVER_ERROR,e.getMessage());
    }
    
  }

  /**
   * 普通的根据文件流
   * 
   * @param uploadRequest 请求对象
   * @param saveTempFileDirectory 临时存储目录
   * @param request
   * @return
   */
  protected abstract FResult<String> uploadToHD(UploadRequest uploadRequest, String saveTempFileDirectory,
      HttpServletRequest request);

  protected FResult<String> transferServletRequestStreamToHD(UploadRequest uploadRequest,
      String saveTempFileDirectory, HttpServletRequest request) {
    String temporaryFileName = getGenericTemporaryFileName(uploadRequest);
    // 设置临时文件名
    if (StringUtils.isBlank(uploadRequest.getOriginalFilename())) {
      uploadRequest.setOriginalFilename(temporaryFileName);
    }
    String uploadTempFilePath = saveTempFileDirectory + File.separator + temporaryFileName;
    uploadRequest.setTemporaryFilePath(uploadTempFilePath);
    log.debug("file temp path : " + uploadTempFilePath);
    long fileBytes = 0;
    try {
      InputStream input = request.getInputStream();
      FileOutputStream fos = new FileOutputStream(uploadTempFilePath);
      int readBytes = 0;
      byte[] buffer = new byte[1024];
      while ((readBytes = input.read(buffer, 0, 1024)) != -1) {
        fileBytes += readBytes;
        fos.write(buffer, 0, readBytes);
      }
      fos.close();
      input.close();
      // 设置文件大小
      uploadRequest.setTemporaryFileSize(fileBytes);
    } catch (Exception storeFileError) {
      log.error("存储客户端上传到临时文件失败", storeFileError);
      return FResult.newFailure(HttpResponseCode.SERVER_IO_ERROR, storeFileError.getMessage());
    }
    return FResult.newSuccess("上传成功");
  }


  protected FResult<String> transferSpringRequestStreamToHD(UploadRequest uploadRequest,
      String saveTempFileDirectory, HttpServletRequest request) {
    MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
    Map<String, MultipartFile> fileMaps = multiRequest.getFileMap();
    if (fileMaps != null && fileMaps.size() == 1) {
      for (Entry<String, MultipartFile> fileEntry : fileMaps.entrySet()) {
        if (fileEntry != null && !fileEntry.getValue().isEmpty()) {
          // 得到本次上传文件
          MultipartFile multiFile = fileEntry.getValue();
          String originalFilename = multiFile.getOriginalFilename();
          uploadRequest.setOriginalExtName(FilenameUtils.getExtension(originalFilename));
          String fileName = UUID.randomUUID().toString() + "." + uploadRequest.getOriginalExtName();
          if (StringUtils.isBlank(uploadRequest.getOriginalFilename())) {
            uploadRequest.setOriginalFilename(fileName);
          } else {
            uploadRequest.setOriginalFilename(originalFilename);
          }
          String fileFullPath = saveTempFileDirectory + File.separator + fileName;
          log.debug("file temp path : " + fileFullPath);
          // 构造临时文件
          File uploadTempFile = new File(fileFullPath);
          try {
            // 临时文件持久化到硬盘
            multiFile.transferTo(uploadTempFile);
            uploadRequest.setTemporaryFilePath(fileFullPath);
            uploadRequest.setTemporaryFileSize(uploadTempFile.length());
          } catch (IllegalStateException | IOException e) {
            log.error("上传文件持久化到服务器失败,计划持久化文件 ", e);
            return FResult.newFailure(HttpResponseCode.SERVER_IO_ERROR, "上传文件持久化到服务器失败");
          }
        }
      }
      return FResult.newSuccess("上传成功");
    } else {
      return FResult.newFailure(HttpResponseCode.CLIENT_PARAM_INVALID, "一次请求仅支持上传一个文件");
    }
  }

  public static <T> T getBean(Class<T> requiredType) {
    return SpringHelper.getBean(requiredType);
  }

  protected String getGenericTemporaryFileName(UploadRequest uploadRequest) {

    if (StringUtils.isBlank(uploadRequest.getTemporaryFileName())) {
      String suffix = uploadRequest.getSuffix();
      String temporaryFileName = null;
      if (!StringUtils.isBlank(suffix)) {
        temporaryFileName = UUID.randomUUID().toString() + "." + suffix;
      } else {
        temporaryFileName = UUID.randomUUID().toString();
      }
      uploadRequest.setTemporaryFileName(temporaryFileName);
    }
    return uploadRequest.getTemporaryFileName();
  }

}