package com.fundevelop.framework.erp.audit;

import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.commons.web.utils.WebUtils;
import com.fundevelop.framework.base.listener.SpringContextHolder;
import com.fundevelop.framework.erp.security.shiro.SysUserCheck;
import com.fundevelop.framework.erp.security.shiro.SysUserToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 操作日志记录拦截器.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/7/6 18:43
 */
public class LogInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        startTimeThreadLocal.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long endTime = System.currentTimeMillis();
        long beginTime = startTimeThreadLocal.get();
        long consumeTime = endTime - beginTime;

        ErpOperationLog log = null;
        String opError = "失败";
        ErpLogger methodLogger = null;
        ErpLogger classLogger = null;

        try {
            try {
                if (handler != null && handler instanceof HandlerMethod) {
                    HandlerMethod handlerMethod = (HandlerMethod)handler;
                    //获取注解信息
                    classLogger = handlerMethod.getBean().getClass().getAnnotation(ErpLogger.class);
                    methodLogger = handlerMethod.getMethod().getAnnotation(ErpLogger.class);

                    //从方法中获取日志信息
                    if (methodLogger != null && methodLogger.log() && !methodLogger.globaldisable()) {
                        if (classLogger == null || (classLogger != null && !classLogger.globaldisable())) {
                            opError = methodLogger.opError();

                            log = createLog(request);
                            log.setModleName(methodLogger.model());
                            log.setAction(methodLogger.action());
                            log.setResult(methodLogger.opSucess());
                        }
                    }

                    //从类中获取日志信息
                    if (log == null) {
                        if (classLogger != null && classLogger.log() && !classLogger.globaldisable()) {
                            opError = classLogger.opError();

                            log = createLog(request);
                            log.setModleName(classLogger.model());
                            log.setAction(classLogger.action());
                            log.setResult(classLogger.opSucess());
                        }
                    }

                    if (log == null && classLogger == null && methodLogger == null) {
                        // 没有Logger注解的请求不记录访问日志
//						log = createLog(request);
                    }

                    if (log != null) {
                        if (StringUtils.isBlank(log.getModleName())) {
                            if (classLogger != null) {
                                log.setModleName(classLogger.model());
                            }

                            if (StringUtils.isBlank(log.getModleName())) {
                                log.setModleName(handlerMethod.getBean().getClass().getSimpleName());
                            }
                        }

                        if (StringUtils.isBlank(log.getAction())) {
                            if (classLogger != null) {
                                log.setAction(classLogger.action());
                            }

                            if (StringUtils.isBlank(log.getAction())) {
                                log.setAction(handlerMethod.getMethod().getName());
                            }
                        }
                    }
                } else {
                        // 没有Logger注解的请求不记录访问日志
//						log = createLog(request);
                }
            } catch (Exception exception) {
                //获取ERP日志出现异常时不应该影响业务运行，因此将异常信息写入Log4j，而不抛出异常
                logger.warn("分析ERP操作日志出现异常", exception);
            }

            if (log != null) {
                if (ex != null) {
                    log.setResult(opError);
                    log.setException(BeanUtils.toJson(ex));
                }

                log.setActionData(BeanUtils.toJson(request.getParameterMap()));

                if (methodLogger != null) {
                    log.setActionContent(methodLogger.logger());
                } else if (classLogger != null) {
                    log.setActionContent(classLogger.logger());
                }

                if (StringUtils.isBlank(log.getModleName())) {
                    if (log.getUrl().indexOf("/") != -1) {
                        log.setModleName(log.getUrl().substring(log.getUrl().lastIndexOf("/")+1));
                    } else {
                        log.setModleName(log.getUrl());
                    }
                }

                if (StringUtils.isBlank(log.getAction())) {
                    if (log.getUrl().indexOf("/") != -1) {
                        log.setAction(log.getUrl().substring(0,log.getUrl().lastIndexOf("/")));
                    } else {
                        log.setAction(log.getUrl());
                    }
                }
            }
        } finally {
            if (log != null) {
                log.setTimeConsuming(consumeTime);
                try {
                    saveLog(log);
                } catch (Exception exception) {
                    logger.error("保持ERP操作日志发生异常！", exception);
                }
            }

            if (ex != null) {
                logger.error("ERP请求发生异常！" ,ex);
            }
        }
    }

    private ErpOperationLog createLog(HttpServletRequest request) {
        ErpOperationLog log = new ErpOperationLog();
        log.setIp(WebUtils.getIpAddr(request));
        log.setActionTime(new Date());
        log.setUrl(request.getRequestURI());
        log.setResult("成功");

        if (StringUtils.isNotBlank(request.getContextPath()) && !"/".equals(request.getContextPath())) {
            log.setUrl(log.getUrl().replace(request.getContextPath(), ""));
        }

        Subject subject = SecurityUtils.getSubject();

        if (subject != null && subject.getPrincipal() != null) {
            String userId = (String)subject.getPrincipal();

            if (StringUtils.isNotBlank(userId)) {
                SysUserToken sysUser = sysUserCheck.getByUserId(userId);

                if (sysUser != null) {
                    log.setUserId(userId);
                    log.setUserName(sysUser.getLoginName());
                }
            }
        }

        return log;
    }

    private void saveLog(ErpOperationLog log) {
        init();

        if (loggerService != null) {
            loggerService.save(log);
        }
    }

    private void init() {
        if (!isInit) {
            loggerService = (LoggerService)SpringContextHolder.getBeanNotRequired("erpOperationLogService");
            isInit = true;
        }
    }

    /** ERP请求开始时间. */
    private NamedThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>("ERP-opWatch-StartTime");

    @Autowired
    private SysUserCheck sysUserCheck;

    private boolean isInit = false;
    private LoggerService loggerService;
    private Logger logger = LoggerFactory.getLogger(getClass());
}
