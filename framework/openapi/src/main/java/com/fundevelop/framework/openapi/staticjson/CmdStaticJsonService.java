package com.fundevelop.framework.openapi.staticjson;

import java.net.URL;
import java.util.Map;

/**
 * 静态JSON模式CMD服务获取接口定义类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/3 23:50
 */
public interface CmdStaticJsonService {
    /**
     * 获取内容.
     */
    Map<String, Object> getContent(URL url);
}
