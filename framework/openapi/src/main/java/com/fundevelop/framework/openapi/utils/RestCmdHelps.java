package com.fundevelop.framework.openapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.commons.utils.ClassUtils;
import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.framework.base.listener.SpringContextHolder;
import com.fundevelop.framework.openapi.annoations.RestCmdMapping;
import com.fundevelop.framework.openapi.model.BaseRestRequest;
import com.fundevelop.framework.openapi.model.RestRequest;
import com.fundevelop.framework.openapi.utils.impl.CmdServiceInvokeDefaultImpl;
import com.fundevelop.framework.openapi.utils.impl.CmdServiceInvokeStaticJsonImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OpenAPI 服务辅助类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/18 10:27
 */
@Component
public class RestCmdHelps extends InstantiationAwareBeanPostProcessorAdapter implements ServletContextListener {
    /**
     * 根据请求参数获取对应的命令调用者.
     */
    public static CmdServiceInvoker getInvoker(BaseRestRequest request) throws Exception {
        String cmd = request.getRestRequest().getCmd();
        RestCmdService cmdService = CMD_SERVICE_MAPPING.get(cmd);

        if (cmdService != null) {
            Class[] paramTypes = cmdService.getMethod().getParameterTypes();
            Object[] methodParameters = null;

            if (paramTypes != null && paramTypes.length > 0) {
                methodParameters = new Object[paramTypes.length];

                for (int i=0; i<paramTypes.length; i++) {
                    Class paramType = paramTypes[i];

                    if (BaseRestRequest.class.isAssignableFrom(paramType)) {
                        methodParameters[i] = getBaseRestRequest(paramType, request.getHttpRequest(), request.getRestRequest());
                    } else if (HttpServletRequest.class.isAssignableFrom(paramType)) {
                        methodParameters[i] = request.getHttpRequest();
                    } else if (RestRequest.class.isAssignableFrom(paramType)) {
                        methodParameters[i] = request.getRestRequest();
                    } else {
                        String parameterName = cmdService.getMethodParameterNames()[i];
                        Object paramValue = request.getRestRequest().getParameters().get(parameterName);

                        logger.debug("从请求{}中获取参数属性：{}={}", cmd, parameterName, paramValue);

                        methodParameters[i] = BeanUtils.convertValue(paramType, paramValue);
                    }
                }
            }

            return new CmdServiceInvokeDefaultImpl(SpringContextHolder.getBean(cmdService.getBeanName()), cmdService.getMethod(), methodParameters);
        } else {
            boolean runWidthStaticJson = com.fundevelop.commons.utils.StringUtils.isBooleanTrue(PropertyUtil.get("fun.openapi.switch.runWithStaticJson","false"));

            if (runWidthStaticJson) {
                return new CmdServiceInvokeStaticJsonImpl(request);
            }
        }

        return null;
    }

    /**
     * 创建基础请求.
     */
    private static BaseRestRequest getBaseRestRequest(Class<? extends BaseRestRequest> requestType,
                                                      HttpServletRequest httpRequest,
                                                      RestRequest restRequest) throws IOException {
        Map<String, Object> parameters = restRequest.getParameters();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(parameters);

        BaseRestRequest businessRequest;
        businessRequest = objectMapper.readValue(jsonString, requestType);
        if (businessRequest == null) {
            try {
                businessRequest = requestType.newInstance();
            } catch (Exception e) {
                logger.error("创建请求对象失败: {}", requestType);
            }
        }

        businessRequest.setHttpRequest(httpRequest);
        businessRequest.setRestRequest(restRequest);

        if (restRequest.getFiles()!=null && restRequest.getFiles().length>0) {
            try {
                Method setFilesMethod = businessRequest.getClass().getMethod("setFiles", MultipartFile[].class);
                setFilesMethod.invoke(businessRequest, restRequest.getFiles());
            } catch (NoSuchMethodException ex) {
            } catch (Exception ex) {
                logger.warn("向CMD服务注入上传文件发生异常", ex);
            }
        }

        return businessRequest;
    }

    @Override
    public Class<?> predictBeanType(Class<?> beanClass, String beanName) {
        if (isFinish) return null;

        // 检查类上是否有@RestCmdMapping注解
        RestCmdMapping cmdMapping = beanClass.getAnnotation(RestCmdMapping.class);

        if (cmdMapping != null && !cmdMappingBeans.containsKey(beanName)) {
            cmdMappingBeans.put(beanName, beanClass);
        }

        return null;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        isFinish = true;
        logger.debug("开始扫描OpenAPI提供的cmd服务...");

        for (String beanName : cmdMappingBeans.keySet()) {
            registerCmd(beanName,cmdMappingBeans.get(beanName));
        }

        logger.debug("OpenAPI扫描完成，共发现服务类：{}，发现服务数：{}", cmdMappingBeans.size(), CMD_SERVICE_MAPPING.size());

        cmdMappingBeans.clear();
        cmdMappingBeans = null;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        CMD_SERVICE_MAPPING.clear();
        CMD_SERVICE_MAPPING = null;
    }

    public RestCmdHelps() {
        SpringContextHolder.addListener(this);
    }

    /**
     * 注册登记注解标记的CMD服务.
     */
    private void registerCmd(final String beanName, final Class<?> beanClass) {
        RestCmdMapping cmdClazz = beanClass.getAnnotation(RestCmdMapping.class);
        final String baseCmd = cmdClazz.cmd();
        final String baseName = cmdClazz.name();

        ReflectionUtils.doWithMethods(beanClass, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                RestCmdMapping cmdMethod = method.getAnnotation(RestCmdMapping.class);

                if (cmdMethod != null) {
                    String cmd = cmdMethod.cmd();
                    String name = cmdMethod.name();

                    if (StringUtils.isNotBlank(cmd)) {
                        if (StringUtils.isNotBlank(baseCmd)) {
                            if (baseCmd.endsWith("/") && cmd.startsWith("/")) {
                                cmd = baseCmd + cmd.substring(1);
                            } else if (!baseCmd.endsWith("/") && !cmd.startsWith("/")) {
                                cmd = baseCmd + "/" + cmd;
                            } else {
                                cmd = baseCmd + cmd;
                            }
                        }
                    } else {
                        cmd = baseCmd;
                    }

                    if (StringUtils.isBlank(cmd)) {
                        logger.error("扫描OpenAPI cmd服务发现cmd为空，服务类：{}，服务方法：{}", beanClass.getName(), method.getName());
                        throw new IllegalStateException("OpenAPI服务不允许CMD为空");
                    }

                    if (cmd.endsWith("/")) {
                        cmd = cmd.substring(0,cmd.length()-1);
                    }

                    if (CMD_SERVICE_MAPPING.containsKey(cmd)) {
                        RestCmdService cmdService = CMD_SERVICE_MAPPING.get(cmd);
                        logger.error("扫描OpenAPI cmd服务发现重复的cmd命令:{}，服务类：{}，服务方法：{}，服务类：{}，服务方法：{}",
                        cmd, beanClass.getName(), method.getName(), cmdService.getBeanType().getName(), cmdService.getMethod().getName());
                        throw new IllegalAccessException("扫描OpenAPI cmd服务发现重复的cmd命令："+cmd);
                    }

                    if (StringUtils.isBlank(name)) {
                        name = method.getName();
                    }

                    if (StringUtils.isNotBlank(baseName)) {
                        name = baseName + "->" + name;
                    } else {
                        name = beanName + "->" + name;
                    }

                    RestCmdService cmdService = new RestCmdService();
                    cmdService.setCmd(cmd);
                    cmdService.setName(name);
                    cmdService.setDescription(cmdMethod.description());

                    cmdService.setCache(cmdMethod.cache());
                    cmdService.setRetryTimes(cmdMethod.retryTimes());
                    cmdService.setTimeout(cmdMethod.timeout());


                    cmdService.setBeanName(beanName);
                    cmdService.setBeanType(beanClass);
                    cmdService.setMethod(method);
                    cmdService.setMethodParameterNames(ClassUtils.getMethodParamNames(beanClass, method));

                    logger.info("扫描OpenAPI cmd服务发现服务({})：{}，服务类：{}，服务方法：{}，参数名称：{}", name, cmd, beanClass.getName(), method.getName(), cmdService.getMethodParameterNames());

                    CMD_SERVICE_MAPPING.put(cmd, cmdService);
                }
            }
        });
    }

    /** cmd服务. */
    private static Map<String, RestCmdService> CMD_SERVICE_MAPPING = new ConcurrentHashMap();

    private boolean isFinish = false;
    private Map<String, Class<?>> cmdMappingBeans = new HashMap();

    private static Logger logger = LoggerFactory.getLogger(RestCmdHelps.class);

    private class RestCmdService {
        private String cmd;
        private String name;
        private String description;

        private boolean cache;
        private int retryTimes;
        private int timeout;

        private String beanName;
        private Class beanType;
        private Method method;
        private String[] methodParameterNames;

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isCache() {
            return cache;
        }

        public void setCache(boolean cache) {
            this.cache = cache;
        }

        public int getRetryTimes() {
            return retryTimes;
        }

        public void setRetryTimes(int retryTimes) {
            this.retryTimes = retryTimes;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public String getBeanName() {
            return beanName;
        }

        public void setBeanName(String beanName) {
            this.beanName = beanName;
        }

        public Class getBeanType() {
            return beanType;
        }

        public void setBeanType(Class beanType) {
            this.beanType = beanType;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public String[] getMethodParameterNames() {
            return methodParameterNames;
        }

        public void setMethodParameterNames(String[] methodParameterNames) {
            this.methodParameterNames = methodParameterNames;
        }
    }
}
