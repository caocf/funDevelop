package com.fundevelop.cache.redis;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Redis连接还池过滤器.
 * <p>描述:负责将Redis连接还池</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/9 14:57
 */
@WebFilter
public class RedisRealeseFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
            RedisUtil.realRealese();
        }
    }

    @Override
    public void destroy() {}
}
