package com.fundevelop.framework.base.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * 字符编码过滤器.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/23 8:20
 */
@WebFilter
public class CharacterEncodingFilter extends OncePerRequestFilter {
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setForceEncoding(boolean forceEncoding) {
        this.forceEncoding = forceEncoding;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(this.encoding != null && (this.forceEncoding || request.getCharacterEncoding() == null)) {
            request.setCharacterEncoding(this.encoding);

            if(this.forceEncoding) {
                response.setCharacterEncoding(this.encoding);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();

        Enumeration<String> parameterNames = getFilterConfig().getInitParameterNames();

        if (parameterNames != null && parameterNames.hasMoreElements()) {
            for (; parameterNames.hasMoreElements();) {
                String paramName = parameterNames.nextElement();
                System.err.println("filter param name="+paramName+"   ="+getFilterConfig().getInitParameter(paramName));
            }
        }
    }

    private String encoding;
    private boolean forceEncoding = false;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
}
