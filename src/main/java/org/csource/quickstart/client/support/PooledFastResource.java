package org.csource.quickstart.client.support;

import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerServer;


/**
 * 
 * @author SongJian email:1738042258@QQ.COM
 */
public class PooledFastResource {
  
  private TrackerServer trackerServer;
  private StorageServer storageServer;
  private StorageClient1 storageClient1;

  public TrackerServer getTrackerServer() {
      return trackerServer;
  }

  public void setTrackerServer(TrackerServer trackerServer) {
      this.trackerServer = trackerServer;
  }

  public StorageServer getStorageServer() {
      return storageServer;
  }

  public void setStorageServer(StorageServer storageServer) {
      this.storageServer = storageServer;
  }

  public StorageClient1 getStorageClient1() {
      return storageClient1;
  }

  public void setStorageClient1(StorageClient1 storageClient1) {
      this.storageClient1 = storageClient1;
  }

}
