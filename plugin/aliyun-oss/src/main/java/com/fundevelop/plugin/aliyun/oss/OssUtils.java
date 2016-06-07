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
    /**
     * 上传文件.
     * @param bucketName bucket名称
     * @param key 文件在OSS的访问名称
     * @param file 要上传的文件
     */
    public static void putObject(String bucketName, String key, File file) {
        OSSClient client = null;

        try {
            client = getClient();

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

    /**
     * 上传文件.
     * @param bucketName bucket名称
     * @param key 文件在OSS的访问名称
     * @param input 要上传的文件流
     */
    public static void putObject(String bucketName, String key, InputStream input) {
        OSSClient client = null;

        try {
            client = getClient();

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

    /**
     * 删除单个文件.
     * @param bucketName bucket名称
     * @param key 要删除的文件在OSS上的访问名称
     */
    public static void deleteObject(String bucketName, String key) {
        OSSClient client = null;

        try {
            client = getClient();
            client.deleteObject(bucketName, key);
            logger.debug("从阿里云OSS上删除文件, bucketName:{}, key:{}", bucketName, key);
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    /**
     * 创建Bucket.
     */
    public static void createBucket(String bucketName) {
        OSSClient client = null;

        try {
            client = getClient();

            if (!client.doesBucketExist(bucketName)) {
                client.createBucket(bucketName);
                logger.debug("在阿里云OSS上创建Bucket, bucketName:{}", bucketName);
            }
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    /**
     * 删除Bucket.
     */
    public static void deleteBucket(String bucketName) {
        OSSClient client = null;

        try {
            client = getClient();

            if (client.doesBucketExist(bucketName)) {
                client.deleteBucket(bucketName);
                logger.debug("从阿里云OSS上删除Bucket, bucketName:{}", bucketName);
            }
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    /**
     * 判断Bucket是否存在.
     */
    public static boolean doesBucketExist(String bucketName) {
        OSSClient client = null;

        try {
            client = getClient();
            return client.doesBucketExist(bucketName);
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

        return new OSSClient(endPoint, accessKeyId, accessKeySecret, conf);
    }

    private OssUtils(){}

    private static Logger logger = LoggerFactory.getLogger(OssUtils.class);
}
