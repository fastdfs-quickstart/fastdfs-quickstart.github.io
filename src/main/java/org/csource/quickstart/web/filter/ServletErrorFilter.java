package org.csource.quickstart.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.csource.quickstart.FResult;
import org.csource.quickstart.HttpResponseCode;
import org.csource.quickstart.util.ServletUtil;

import lombok.extern.slf4j.Slf4j;


/**
 * @author SongJian email:1738042258@QQ.COM
 * 拦截Servlet抛出的异常
 */
@Slf4j
public class ServletErrorFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Exception error) {
            HttpServletResponse r = (HttpServletResponse) response;
            log.error("servlet request errpr", error);
            ServletUtil.responseOutWithJson(r, FResult.newFailure(HttpResponseCode.SERVER_ERROR, error.getMessage()));
        }
    }

    @Override
    public void destroy() {
      
    }

}
