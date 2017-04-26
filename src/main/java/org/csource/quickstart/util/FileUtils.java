package org.csource.quickstart.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

  public static boolean osLinux() {
    String osName = System.getProperty("os.name");
    if (osName.startsWith("Linux")) {
      return true;
    }
    return false;
  }

  /**
   * 获取文件的md5 信息
   *
   * @return
   */
  public static String md5(String path) {
    String result = "";
    if (StringUtils.isBlank(path)) {
      log.error("dsMountPath/filefullname is null!");
      return result;
    }
    BufferedReader br = null;
    Process process = null;
    String File_MD5 = null;
    String osName = System.getProperty("os.name");
    try {
      if (osName.startsWith("Linux")) {
        process = Runtime.getRuntime().exec("md5sum " + path);
      } else if (osName.startsWith("Mac")) {
        File_MD5 = "([^=]*)=[\\s*]*([^=]*)";
        process = Runtime.getRuntime().exec("md5 " + path);
      } else {
        File f = new File(path);
        InputStream ins = new FileInputStream(f);
        try {
          return DigestUtils.md5Hex(ins);
        } finally {
          IOUtils.closeQuietly(ins);
        }
      }
      br = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line = null;
      List<String> tmpList = new ArrayList<String>();
      while ((line = br.readLine()) != null) {
        tmpList.add(line);
      }

      StringBuffer sbinfo = new StringBuffer();
      for (String info : tmpList) {
        sbinfo.append(info);
        sbinfo.append("\n");
      }
      // 解析md5值
      Pattern p = Pattern.compile(File_MD5, Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(sbinfo.toString());
      while (m.find()) {
        try {
          if (osName.startsWith("Linux")) {
            result = m.group(1).trim();
          } else if (osName.startsWith("Mac")) {
            result = m.group(2).trim();
          }
          break;
        } catch (Exception e) {
          log.error(e.getMessage(), new Throwable(e));
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), new Throwable(e));
    } finally {
      if (process != null) {
        process.destroy();
      }
      if (br != null) {
        try {
          br.close();
        } catch (Exception e) {
          log.error(e.getMessage(), new Throwable(e));
        }
      }
    }
    return result;
  }

}
