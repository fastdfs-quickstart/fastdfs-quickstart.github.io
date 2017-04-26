package org.csource.quickstart.crontab;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.csource.quickstart.client.FastDFSService;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;


/**
 * @author SongJian email:1738042258@QQ.COM
 * 定时清理垃圾文件的线程
 */
@Component
@Slf4j
public class CleanLocalTrashFileWorker {
  
  @Resource
  private FastDFSService fastDFSService;

  ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(2);

  @PostConstruct
  public void init() {
      scheduledThreadPool.scheduleAtFixedRate(new CleanLocalTrashFileWorkerExecutor(), 1, 30, TimeUnit.MINUTES);
  }

  class CleanLocalTrashFileWorkerExecutor implements Runnable {

      private Set<String> ignore(){
          return Sets.newHashSet(".","..");
      }

      @Override
      public void run() {
        if(org.csource.quickstart.util.FileUtils.osLinux()){
          Process process = null;
          BufferedReader br = null;
          String command = null;
          String[] commands = null;
          try {
              command = "ls " + fastDFSService.getLocalTempFilePath() + "  -all --time-style='+%Y-%m-%d %H:%M:%S' --sort=time -r";
              commands = new String[]{"/bin/sh", "-c", command};
              ProcessBuilder builder = new ProcessBuilder(commands);
              builder.redirectErrorStream(true);
              process = builder.start();
              br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
              String line = null;

              Date now = new Date();
              boolean startDelete = false;
              while ((line = br.readLine()) != null) {
                  if (line.equalsIgnoreCase("ERROR")) {
                      break;
                  }
                  String[] data = line.split("\\s");
                  String fileName = data[data.length - 1];
                  if (!ignore().contains(fileName)) {
                      try {
                          File targetFile = new File(fastDFSService.getLocalTempFilePath() + File.separator + fileName);
                          if (startDelete){
                              delete(targetFile);
                          }else if(data.length > 2){
                              String fileTime = data[data.length - 3] + " " + data[data.length - 2];
                              Date lineTime = DateUtils.parseDate(fileTime, "yyyy-MM-dd HH:mm:ss");
                              if ((now.getTime() - lineTime.getTime()) / 1000 > fastDFSService.getCleanTempSeconds()) {
                                  startDelete = true;
                              }
                              delete(targetFile);
                          }
                      } catch (ParseException e) {
                        log.error("解析输出内容,格式化时间信息错误" + e == null ? "" : e.getMessage(), e);
                      }
                  }
              }
          } catch (Exception e) {
              log.error("清空垃圾助手发生错误,已经被捕获." + e == null ? "" : e.getMessage(), e);
          } finally {
              if (process != null) {
                  process.destroy();
              }
              closeIO(br);
          }
        }
      }

      private void delete(File file){
          try {
              if (file.exists()) {
                  FileUtils.forceDelete(file);
              }
          } catch (IOException io) {
            log.error("清除文件发生异常", io);
          }
      }

      private final void closeIO(Closeable c) {
          if (c != null) {
              try {
                  c.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
  }
  
}
