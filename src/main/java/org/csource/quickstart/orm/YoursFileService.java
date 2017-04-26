package org.csource.quickstart.orm;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.csource.quickstart.processor.AbstractFileProcessor;
import org.csource.quickstart.request.UploadRequest;
import org.springframework.stereotype.Service;

@Service
public class YoursFileService extends AbstractFileProcessor implements FileService{

  @Override
  public Long addUploadFile(long nowTimestamp, UploadFile uploadFile, File theTempFile, UploadRequest uploadRequest, String fdfsFileId) {
    uploadFile.setCreatetime(nowTimestamp);
    uploadFile.setSize(theTempFile.length());
    uploadFile.setFilemd5(uploadRequest.getTemporaryFileMd5());
    if (StringUtils.isNotBlank(fdfsFileId)) {
      uploadFile.setUrl(getFullLevel2DomainMappingToStorage(fdfsFileId,uploadRequest));
    }
    uploadFile.setId(2014L);
    return uploadFile.getId();
  }

}
