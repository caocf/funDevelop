package com.fundevelop.framework.openapi.staticjson.impl;

import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.framework.openapi.staticjson.CmdStaticJsonService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

/**
 * 静态JSON模式CMD服务获取接口文件协议实现类..
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/3 23:53
 */
@Component("staticJson.file")
public class FileProtocolImpl implements CmdStaticJsonService {
    @Override
    public Object getContent(URL url) {
        String content = null;

        try {
            content = FileUtils.readFileToString(new File(url.getFile()));

            if (StringUtils.isNotBlank(content)) {
                return BeanUtils.toBean(content, Map.class);
            }
        } catch(Exception e){
            logger.warn("读取静态JSON文件时发生异常，文件地址：{}", url.getPath(), e);
            throw new RuntimeException("无法获取静态JSON文件", e);
        }

        return null;
    }

    private Logger logger = LoggerFactory.getLogger(getClass());
}
