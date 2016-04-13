package com.fundevelop.commons.utils;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.commons.lang3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Class工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/3/18 13:18
 */
public class ClassUtils {
    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
     * 如无法找到, 返回Object.class.
     *
     * 如public UserDao extends HibernateDao<User,Long>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be determined
     */
    @SuppressWarnings("rawtypes")
    public static Class getSuperClassGenricType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class) params[index];
    }

    /**
     * 获取方法参数名称.
     */
    public static String[] getMethodParamNames(Class<?> clazz, Method method) {
        insertClassPath(clazz.getResource("/"));

        return getMethodParamNames(clazz, method.getName(), method.getParameterTypes());
    }

    private static void insertFunJar() {
        if (!isInit) {
            synchronized (ClassUtils.class) {
                if (!isInit) {
                    try {
                        ClassLoader loader = Thread.currentThread().getContextClassLoader();

                        // 按文件的形式去查找
                        Enumeration<URL> urls = loader.getResources("com/fundevelop");
                        while (urls.hasMoreElements()) {
                            URL url = urls.nextElement();

                            if (url != null) {
                                String protocol = url.getProtocol();

                                String path = org.apache.commons.lang3.StringUtils.substringBeforeLast(url.getPath(), ".jar")+".jar";
                                url = new URL(path);

                                if ("jar".equals(protocol) && !clazzPaths.contains(url)) {
                                    logger.info("添加类路径：{}", url.getPath());

                                    clazzPaths.add(url);
                                    try {
                                        ClassPool.getDefault().insertClassPath(url.getPath());
                                    } catch (NotFoundException e) {
                                        logger.warn("添加类路径失败:{}", url, e);
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        logger.error("加载快速开发相关资源发生异常", e);
                    }

                    isInit = true;
                }
            }
        }
    }

    /**
     * 添加类路径.
     */
    private static void insertClassPath(URL classPath) {
        insertFunJar();

        if (!clazzPaths.contains(classPath)) {
            logger.info("添加类路径：{}", classPath.getPath());

            clazzPaths.add(classPath);
            try {
                ClassPool.getDefault().insertClassPath(classPath.getPath());
            } catch (NotFoundException e) {
                logger.warn("添加类路径失败:{}", classPath, e);
            }
        }
    }

    /**
     * 获取方法参数名称.
     */
    private static String[] getMethodParamNames(CtMethod cm) {
        CtClass cc = cm.getDeclaringClass();
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

        if (attr == null) {
            throw new RuntimeException(cc.getName());
        }

        String[] paramNames = null;

        try {
            paramNames = new String[cm.getParameterTypes().length];
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        pos = findThisVariablePos(pos, attr);

        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i] = attr.variableName(i + pos);
        }

        return paramNames;
    }

    /**
     * 查找第一个参数位置.
     */
    private static int findThisVariablePos(int startPos, LocalVariableAttribute attr) {
        int pos = startPos;

        for (int i = pos; i < attr.length(); i++) {
            try {
                String name = attr.variableName(i);

                if ("this".equals(name)) {
                    pos = i + 1;
                    break;
                }
            } catch (Exception ex) {
                break;
            }
        }

        return pos;
    }

    /**
     * 获取方法参数名称.
     */
    private static String[] getMethodParamNames(Class<?> clazz, String method, Class<?>... paramTypes) {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = null;
        CtMethod cm = null;
        try {
            cc = pool.get(clazz.getName());

            String[] paramTypeNames = new String[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++)
                paramTypeNames[i] = paramTypes[i].getName();

            cm = cc.getDeclaredMethod(method, pool.get(paramTypeNames));
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        return getMethodParamNames(cm);
    }

    private ClassUtils() {}

    private static boolean isInit = false;
    private final static List<URL> clazzPaths = new ArrayList<>();
    private static Logger logger = LoggerFactory.getLogger(ClassUtils.class);
}
