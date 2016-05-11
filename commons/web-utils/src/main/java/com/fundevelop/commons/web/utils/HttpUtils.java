package com.fundevelop.commons.web.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * http工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/18 16:03
 */
public class HttpUtils {
    public static String executePost(String httpUrl, HttpEntity param) {
        return executePost(httpUrl, param, HttpClientBuilder.create().build(), DEFAULT_CHARENCODING, null);
    }

    public static String executePost(String httpUrl, HttpEntity param, Map<String, String> headers) {
        return executePost(httpUrl, param, HttpClientBuilder.create().build(), DEFAULT_CHARENCODING, headers);
    }

    public static String executePost(String httpUrl, HttpEntity param, String chartEncoding) {
        return executePost(httpUrl, param, HttpClientBuilder.create().build(), chartEncoding, null);
    }

    public static String executePost(String httpUrl, HttpEntity param, HttpClient httpClient, String chartEncoding, Map<String, String> headers) {
        String result = "";

        try {
            if (StringUtils.isBlank(chartEncoding)) {
                chartEncoding = DEFAULT_CHARENCODING;
            }

            HttpPost httpPost = new HttpPost(httpUrl);

            if (headers != null && !headers.isEmpty()) {
                for (String key : headers.keySet()) {
                    httpPost.addHeader(key, headers.get(key));
                }
            }

            // 设置请求配置
            setRequestConfig(httpPost);

            // 设置请求体
            if (param != null) {
                httpPost.setEntity(param);
            }

            logger.debug("准备调用远程服务[ POST ], url: {}, body: {}", httpUrl, param);

            HttpResponse httpResponse = httpClient.execute(httpPost);

            // 获取返回数据
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity, chartEncoding);
            if (entity != null) {
                EntityUtils.consume(entity);
            }
        } catch (Exception ex) {
            logger.error("请求失败，request: {}，param：{}", httpUrl, param);
            throw new RuntimeException("发送HTTP Get请求失败", ex);
        }

        return result;
    }

    public static String executeGet(String httpUrl, Map<String, String> param) {
        return executeGet(httpUrl, param, DEFAULT_CHARENCODING);
    }

    public static String executeGet(String httpUrl, Map<String, String> param, String chartEncoding) {
        return executeGet(httpUrl, param, HttpClientBuilder.create().build(), chartEncoding);
    }

    public static String executeGet(String httpUrl, Map<String, String> param, HttpClient httpClient, String chartEncoding) {
        String result = "";

        try {
            if (StringUtils.isBlank(chartEncoding)) {
                chartEncoding = DEFAULT_CHARENCODING;
            }

            List<NameValuePair> params = null;

            if (param != null && !param.isEmpty()) {
                params = new ArrayList<>(param.size());

                for (String paramName : param.keySet()) {
                    params.add(new BasicNameValuePair(paramName, param.get(paramName)));
                }
            }

            HttpGet httpGet = new HttpGet();

            // 设置请求配置
            setRequestConfig(httpGet);

            // 设置请求参数
            String fullUrl = httpUrl;
            if (params != null && !param.isEmpty()) {
                String queryStrings = EntityUtils.toString(new UrlEncodedFormEntity(params, chartEncoding));

                if (httpUrl.indexOf("?") != -1) {
                    fullUrl += "&" + queryStrings;
                } else {
                    fullUrl += "?" + queryStrings;
                }
            }

            httpGet.setURI(new URI(fullUrl));

            logger.debug("准备调用远程服务[ GET ], url: {}", fullUrl);

            HttpResponse httpResponse = httpClient.execute(httpGet);

            // 获取返回数据
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity, chartEncoding);
            if (entity != null) {
                EntityUtils.consume(entity);
            }
        } catch (Exception ex) {
            logger.error("请求失败，request: {}，param：{}", httpUrl, param);
            throw new RuntimeException("发送HTTP Get请求失败", ex);
        }

        return result;
    }

    /**
     * 设置请求配置
     * @param clientRequest http client请求
     */
    private static void setRequestConfig(HttpRequestBase clientRequest) {
        // 超时配置
        int connectionTimeout = Integer.parseInt(PropertyUtil.get("webUtils.http.idleTimeout", "6000"),10);

        if (connectionTimeout > 0) {
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(connectionTimeout)
                    .setConnectTimeout(connectionTimeout)
                    .setConnectionRequestTimeout(connectionTimeout)
                    .build();
            clientRequest.setConfig(defaultRequestConfig);
        }
    }

    private static final String DEFAULT_CHARENCODING = "UTF-8";

    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private HttpUtils(){}
}
