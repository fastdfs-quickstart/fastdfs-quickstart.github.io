package org.csource.quickstart.orm;

import java.io.File;

import org.csource.quickstart.request.UploadRequest;

/**
 * @author SongJian email:1738042258@QQ.COM
 * 这里只是做个例子,指导大致的方向,具体的自己实现
 */
public interface FileService {
  
  Long addUploadFile(long nowTimestamp, UploadFile commonFile, File theTempFile, UploadRequest uploadRequest, String fdfsFileId);
  
}
