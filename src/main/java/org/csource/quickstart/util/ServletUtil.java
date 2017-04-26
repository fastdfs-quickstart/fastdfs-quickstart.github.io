package org.csource.quickstart.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

public class ServletUtil {

  public static void responseOutWithJson(HttpServletResponse response, Object responseObject) {
    response.setCharacterEncoding("UTF-8");
    response.setHeader("Content-type", "application/json; charset=UTF-8");
    OutputStream out = null;
    try {
        out = response.getOutputStream();
        out.write(JSON.toJSONString(responseObject).getBytes("UTF-8"));
        out.flush();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
  
}
