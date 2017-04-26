package org.csource.quickstart.client;

import java.io.File;
import java.io.IOException;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.quickstart.client.support.FastPool;
import org.csource.quickstart.client.support.PooledFastResource;
import org.csource.quickstart.processor.AbstractFileProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FastDFSService implements InitializingBean {

  // 注入了classpath:filecloud/fdfs_client.conf文件,自动转为一个Resource
  private Resource localConfigurePath;
  // 存储临时文件的路径
  private String localTempFilePath;
  private String accessFileSite;
  // 有的工具类不使用注入该类的形式时,使用这个属性来获取临时目录路径
  private static String saveTempFilePath;

  private int cleanTempSeconds;

  private BasePooledObjectFactory<PooledFastResource> pooledFastResourceBasePooledObjectFactory = new FastPool();

  private GenericObjectPool<PooledFastResource> genericObjectPool =
      new GenericObjectPool<PooledFastResource>(pooledFastResourceBasePooledObjectFactory);

  private GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

  @Override
  public void afterPropertiesSet() throws Exception {
    log.debug("准备加载FastDFS配置文件:{}", localConfigurePath);
    if (localConfigurePath == null) {
      throw new Exception("本地FastDFS配置文件不能为空,需要指定为某个具体的文件资源");
    }
    if (localConfigurePath.getFile() == null) {
      throw new Exception("本地FastDFS配置文件不存在,需要指定为某个具体的文件资源");
    }
    ClientGlobal.init(localConfigurePath.getFile().getAbsolutePath());
    log.debug("已加载FastDFS配置文件");
    File localTempPath = new File(localTempFilePath);
    if (!localTempPath.exists()) {
      localTempPath.mkdirs();
    }
    poolConfig.setMaxTotal(1000);//
    poolConfig.setMaxIdle(100); // 最多多少个空闲的
    poolConfig.setMinIdle(100);
    poolConfig.setMaxWaitMillis(10000);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    genericObjectPool.setConfig(poolConfig);
    AbstractFileProcessor.initFastDFSService(this);
  }

  public int getCleanTempSeconds() {
    return cleanTempSeconds;
  }

  public void setCleanTempSeconds(int cleanTempSeconds) {
    this.cleanTempSeconds = cleanTempSeconds;
  }

  public void setLocalConfigurePath(Resource localConfigurePath) {
    this.localConfigurePath = localConfigurePath;
  }


  public void setTrackerServerPath(String trackerServerPath) {
    this.accessFileSite = trackerServerPath;
  }

  public void setLocalTempFilePath(String localTempFilePath) {
    this.localTempFilePath = localTempFilePath;
    FastDFSService.saveTempFilePath = localTempFilePath;
  }

  public String getLocalTempFilePath() {
    return localTempFilePath;
  }

  public static String getSaveTempFilePath() {
    return FastDFSService.saveTempFilePath;
  }

  public String upload_file1(String filePath, String extension) throws IOException {
    return upload_file1(filePath, extension, null);
  }

  public String upload_file1(String filePath, String extension, NameValuePair[] meta_list) throws IOException {
    String uploadedFileId = null;
    try {
      PooledFastResource resource = genericObjectPool.borrowObject();
      if (resource != null) {
        uploadedFileId = resource.getStorageClient1().upload_file1(filePath, extension, meta_list);
        genericObjectPool.returnObject(resource);
      }
    } catch (Exception e) {
      log.error("上传文件到FastDFS发生异常,文件路径:" + filePath, e);
    }
    return uploadedFileId;
  }

  public String upload_file_bytes(byte[] data, String extension) throws MyException, IOException {
    return upload_file_bytes(data, extension, null);
  }

  public String upload_file_bytes(byte[] data, String extension, NameValuePair[] meta_list) throws MyException, IOException {
    String uploadedFileId = null;
    try {
      PooledFastResource resource = genericObjectPool.borrowObject();
      if (resource != null) {
        uploadedFileId = resource.getStorageClient1().upload_file1(data, extension, meta_list);
        genericObjectPool.returnObject(resource);
      }
    } catch (Exception e) {
      log.error("上传字节到FastDFS发生异常,文件路径:", e);
    }
    return uploadedFileId;
  }

  /*
   * 0 for success, none zero for fail (error code)
   */
  public int delete_file(String group_name, String remote_filename) throws IOException {
    int result = 0;
    PooledFastResource resource = null;
    try {
      resource = genericObjectPool.borrowObject();
      result = resource.getStorageClient1().delete_file(group_name, remote_filename);
    } catch (Exception e) {
      log.error("删除FastDFS文件发生异常,group_name:" + group_name + "remote_filename: " + remote_filename, e);
    } finally {
      if (resource != null) {
        genericObjectPool.returnObject(resource);
      }
    }
    return result;
  }

  // 这个地方应该如果是指向storage的2级域名比较好一些
  public String level2DomainMappingToStorageEndWithSplitor() {
    return accessFileSite;
  }
  

  public void destroy() {

  }

}
