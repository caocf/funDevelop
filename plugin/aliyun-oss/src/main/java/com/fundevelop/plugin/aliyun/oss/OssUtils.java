package com.fundevelop.plugin.aliyun.oss;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectResult;
import com.fundevelop.commons.web.utils.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 阿里云OSS工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/10 19:12
 */
public class OssUtils {
    public static void putObject(String bucketName, String key, File file) {
        OSSClient client = null;

        try {
            getClient();

            PutObjectResult result = client.putObject(bucketName, key, file);
            logger.debug("使用阿里云OSS上传文件，bucketName：{}，key：{}，file：{}，result：{}", bucketName, key, file.getPath(), result);

            if (result != null && result.getCallbackResponseBody() != null) {
                try {
                    result.getCallbackResponseBody().close();
                } catch (IOException e) {
                    logger.warn("关闭阿里云OSS返回流失败");
                }
            }
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    public static void putObject(String bucketName, String key, InputStream input) {
        OSSClient client = null;

        try {
            getClient();

            PutObjectResult result = client.putObject(bucketName, key, input);
            logger.debug("使用阿里云OSS上传文件，bucketName：{}，key：{}，result：{}", bucketName, key, result);

            if (result != null && result.getCallbackResponseBody() != null) {
                try {
                    result.getCallbackResponseBody().close();
                } catch (IOException e) {
                    logger.warn("关闭阿里云OSS返回流失败");
                }
            }
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    private static OSSClient getClient() {
        String endPoint = PropertyUtil.get("aliyun.oss.endpoint");

        if (StringUtils.isBlank(endPoint)) {
            throw new RuntimeException("请在application.properties中配置阿里云OSS Endpoint地址（aliyun.oss.endpoint）");
        }

        String accessKeyId = PropertyUtil.get("aliyun.oss.accessKeyId");

        if (StringUtils.isBlank(accessKeyId)) {
            throw new RuntimeException("请在application.properties中配置阿里云OSS accessKeyId（aliyun.oss.accessKeyId）");
        }

        String accessKeySecret = PropertyUtil.get("aliyun.oss.accessKeySecret");

        if (StringUtils.isBlank(accessKeySecret)) {
            throw new RuntimeException("请在application.properties中配置阿里云OSS accessKeySecret（aliyun.oss.accessKeySecret）");
        }

        ClientConfiguration conf = new ClientConfiguration();
        conf.setMaxConnections(Integer.parseInt(PropertyUtil.get("aliyun.oss.maxConnections", "100"), 10));
        conf.setConnectionTimeout(Integer.parseInt(PropertyUtil.get("aliyun.oss.connectionTimeout", "5000"), 10));

        return new OSSClient(endPoint, accessKeyId, accessKeySecret);
    }

    private OssUtils(){}

    private static Logger logger = LoggerFactory.getLogger(OssUtils.class);
}
