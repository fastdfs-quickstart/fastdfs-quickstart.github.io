package org.csource.quickstart.client.support;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;


/**
 * @author SongJian email:1738042258@QQ.COM
 */
public class FastPool extends BasePooledObjectFactory<PooledFastResource>{
  
  @Override
  public org.csource.quickstart.client.support.PooledFastResource create() throws Exception {
    TrackerClient tracker = new TrackerClient();
    TrackerServer trackerServer = tracker.getConnection();
    StorageServer storageServer = tracker.getStoreStorage(trackerServer);
    StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);

    PooledFastResource resource = new PooledFastResource();
    resource.setTrackerServer(trackerServer);
    resource.setStorageServer(storageServer);
    resource.setStorageClient1(storageClient1);
    return resource;
  }

  @Override
  public PooledObject<org.csource.quickstart.client.support.PooledFastResource> wrap(
      org.csource.quickstart.client.support.PooledFastResource obj) {
    return new DefaultPooledObject<PooledFastResource>(obj);
  }
  
  @Override
  public void passivateObject(PooledObject<PooledFastResource> p) throws Exception {
      PooledFastResource resource = p.getObject();
      synchronized (resource) {
          resource.getStorageServer().close();
          resource.getTrackerServer().close();
      }
  }
  
}
