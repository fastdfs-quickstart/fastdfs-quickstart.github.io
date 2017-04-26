package org.csource.quickstart;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;

public class StreamUploadClient {

  public static void main(String[] args) {
    try {
      String httpFileServer = "http://localhost:8080/upload-by-servlet";
      URL url = new URL(httpFileServer);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setUseCaches(false);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/octet-stream");
      conn.setRequestProperty("Cache-Control", "no-cache");
      conn.setRequestProperty("Charsert", "UTF-8");
      conn.setRequestProperty("charset", "UTF-8");
      JSONObject json = new JSONObject();
      json.put("bizCode", 1); //有时候,同样的文件在不同的bizCode下要做不同的处理
      json.put("expiretime", 5000);// 过期时间
      json.put("suffix", "json"); // 后缀
      json.put("filename", "aaa.json");
      json.put("not_append_filename", true);
      conn.setRequestProperty("UPLOAD-JSON", json.toString());
      conn.connect();
      conn.setConnectTimeout(10000);
      OutputStream out = conn.getOutputStream();
      File file = new File("D:/111.txt");
      DataInputStream in = new DataInputStream(new FileInputStream(file));
      int bytes = 0;
      byte[] buffer = new byte[1024];
      while ((bytes = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytes);
      }
      in.close();
      out.flush();
      out.close();

      /* 取得Response内容 */
      InputStream is = conn.getInputStream();
      int bs = is.available();
      byte[] responseBytes = new byte[bs];
      is.read(responseBytes, 0, bs);
      if (responseBytes.length==0) {
        TimeUnit.SECONDS.sleep(5);
      }
      System.out.println(new String(responseBytes, "UTF-8"));
      is.close();
      conn.disconnect();
    } catch (Exception e) {
      System.out.println("发送文件出现异常！" + e);
      e.printStackTrace();
    }

  }

}
