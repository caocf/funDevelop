package com.fundevelop.plugin.sns.weixin;

import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.commons.utils.MD5Util;
import com.fundevelop.commons.web.utils.HttpUtils;
import com.fundevelop.commons.web.utils.PropertyUtil;
import com.fundevelop.plugin.sns.weixin.model.AccessToken;
import com.fundevelop.plugin.sns.weixin.model.CgiAccessToken;
import com.fundevelop.plugin.sns.weixin.model.Ticket;
import com.fundevelop.plugin.sns.weixin.model.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.*;

/**
 * 微信工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/18 15:48
 */
public class WeixinUtils {
    /**
     * 创建签名SHA1.
     */
    public static String createSHA1Sign(SortedMap parameters) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            sb.append(k + "=" + v + "&");
        }

        String params = sb.substring(0, sb.lastIndexOf("&"));
        String signature = getSha1(params);

        logger.debug("创建签名SHA1，签名字符串：{}，signature：{}", params, signature);

        return signature;
    }

    public static String getNonceStr() {
        Random random = new Random();
        return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "GBK");
    }

    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    public static String getSha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }

        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };

        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes());

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取jsapi_ticket.
     */
    public static Ticket getTicket(String accessToken) throws IOException {
        String uri = PropertyUtil.get("weixin.host.cgi.getticket", "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={ACCESS_TOKEN}&type=jsapi");
        Map<String, String> params = new HashMap<>(1);
        params.put("ACCESS_TOKEN", accessToken);

        uri = com.fundevelop.commons.utils.StringUtils.replaceConstant(uri, params);
        String jsonResp = HttpUtils.executeGet(uri, null);
        Map<String, Object> respMap = BeanUtils.toBean(jsonResp, Map.class);

        if (respMap != null && !respMap.isEmpty()) {
            if (respMap.get("errcode") != null && ((int)respMap.get("errcode"))!=0) {
                logger.error("从微信服务端获取公众号jsapi_ticket失败，微信返回结果：{}", respMap);
                throw new RuntimeException("从微信服务端获取公众号jsapi_ticket失败：" + jsonResp);
            }

            logger.debug("从微信服务端获取公众号jsapi_ticket:{}，ACCESS_TOKEN：{}", respMap, accessToken);

            return BeanUtils.toBean(jsonResp, Ticket.class);
        } else {
            throw new RuntimeException("从微信服务端获取公众号jsapi_ticket失败");
        }
    }

    /**
     * 获取公众号access token.
     */
    public static CgiAccessToken getAccessToken() throws IOException {
        String appId = PropertyUtil.get("weixin.mp.AppID");
        String appSecret = PropertyUtil.get("weixin.mp.AppSecret");

        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret)) {
            throw new RuntimeException("请在application.properties配置文件中配置weixin.mp.AppID及weixin.mp.AppSecret两个属性");
        }

        return getAccessToken(appId, appSecret);
    }

    /**
     * 获取公众号access token.
     */
    public static CgiAccessToken getAccessToken(String appID, String appSecret) throws IOException {
        String uri = PropertyUtil.get("weixin.host.cgi.access_token", "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={APPID}&secret={SECRET}");
        Map<String, String> params = new HashMap<>(2);
        params.put("APPID", appID);
        params.put("SECRET", appSecret);

        uri = com.fundevelop.commons.utils.StringUtils.replaceConstant(uri, params);

        String jsonResp = HttpUtils.executeGet(uri, null);
        Map<String, Object> respMap = BeanUtils.toBean(jsonResp, Map.class);

        if (respMap != null && !respMap.isEmpty()) {
            if (respMap.get("errcode") != null) {
                logger.error("从微信服务端获取公众号access_token失败，微信返回结果：{}", respMap);
                throw new RuntimeException("从微信服务端获取公众号access_token失败：" + jsonResp);
            }

            logger.debug("从微信服务端获取公众号access_token:{}，appID：{}，appSecret：{}", respMap, appID, appSecret);

            return BeanUtils.toBean(jsonResp, CgiAccessToken.class);
        } else {
            throw new RuntimeException("从微信服务端获取公众号access_token失败");
        }
    }

    /**
     * （登录）通过code获取access_token.
     * @throws IOException
     */
    public static AccessToken getAccessToken(String code) throws IOException {
        String appId = PropertyUtil.get("weixin.AppID");
        String appSecret = PropertyUtil.get("weixin.AppSecret");

        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret)) {
            throw new RuntimeException("请在application.properties配置文件中配置weixin.AppID及weixin.AppSecret两个属性");
        }

        return getAccessToken(code, appId, appSecret);
    }

    /**
     * （登录）通过code获取access_token.
     * @throws IOException
     */
    public static AccessToken getAccessToken(String code, String appID, String appSecret) throws IOException {
        String uri = PropertyUtil.get("weixin.host.access_token", "https://api.weixin.qq.com/sns/oauth2/access_token?appid={APPID}&secret={SECRET}&code={CODE}&grant_type=authorization_code");
        Map<String, String> params = new HashMap<>(3);
        params.put("APPID", appID);
        params.put("SECRET", appSecret);
        params.put("CODE", code);

        uri = com.fundevelop.commons.utils.StringUtils.replaceConstant(uri, params);

        String jsonResp = HttpUtils.executeGet(uri, null);
        Map<String, Object> respMap = BeanUtils.toBean(jsonResp, Map.class);

        if (respMap != null && !respMap.isEmpty()) {
            if (respMap.get("errcode") != null) {
                logger.error("从微信服务端获取access_token失败，微信返回结果：{}", respMap);
                throw new RuntimeException("从微信服务端获取access_token失败：" + jsonResp);
            }

            logger.debug("从微信服务端获取access_token:{}，code：{}，appID：{}，appSecret：{}", respMap, code, appID, appSecret);

            return BeanUtils.toBean(jsonResp, AccessToken.class);
        } else {
            throw new RuntimeException("从微信服务端获取access_token失败");
        }
    }

    /**
     * （登录）刷新access_token有效期.
     */
    public static AccessToken refreshToken(String refreshToken) throws IOException {
        String appId = PropertyUtil.get("weixin.AppID");

        if (StringUtils.isBlank(appId)) {
            throw new RuntimeException("请在application.properties配置文件中配置weixin.AppID属性");
        }

        return refreshToken(refreshToken, appId);
    }

    /**
     * （登录）刷新access_token有效期.
     */
    public static AccessToken refreshToken(String refreshToken, String appId) throws IOException {
        String uri = PropertyUtil.get("weixin.host.refresh_token", "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=${APPID}&grant_type=refresh_token&refresh_token=${REFRESH_TOKEN}");
        Map<String, String> params = new HashMap<>(3);
        params.put("APPID", appId);
        params.put("REFRESH_TOKEN", refreshToken);

        uri = com.fundevelop.commons.utils.StringUtils.replaceConstant(uri, params);

        String jsonResp = HttpUtils.executeGet(uri, null);
        Map<String, Object> respMap = BeanUtils.toBean(jsonResp, Map.class);

        if (respMap != null && !respMap.isEmpty()) {
            if (respMap.get("errcode") != null) {
                logger.error("从微信服务端refresh_token失败，微信返回结果：{}", respMap);
                throw new RuntimeException("从微信服务端refresh_token失败：" + jsonResp);
            }

            logger.debug("从微信服务端refresh_token:{}，code：{}，appID：{}", respMap, refreshToken, appId);

            return BeanUtils.toBean(jsonResp, AccessToken.class);
        } else {
            throw new RuntimeException("从微信服务端refresh_token失败");
        }
    }

    /**
     * 获取用户个人信息（UnionID机制）.
     * @throws IOException
     */
    public static UserInfo getUserInfo(String accessToken) throws IOException {
        String appId = PropertyUtil.get("weixin.AppID");

        if (StringUtils.isBlank(appId)) {
            throw new RuntimeException("请在application.properties配置文件中配置weixin.AppID属性");
        }

        return getUserInfo(accessToken, appId);
    }

    /**
     * 获取用户个人信息（UnionID机制）.
     * @throws IOException
     */
    public static UserInfo getUserInfo(String accessToken, String openId) throws IOException {
        String uri = PropertyUtil.get("weixin.host.userinfo", "https://api.weixin.qq.com/sns/userinfo?access_token={ACCESS_TOKEN}&openid={OPENID}");
        Map<String, String> params = new HashMap<>();
        params.put("ACCESS_TOKEN", accessToken);
        params.put("OPENID", openId);

        uri = com.fundevelop.commons.utils.StringUtils.replaceConstant(uri, params);

        String jsonResp = HttpUtils.executeGet(uri, null);
        Map<String, Object> respMap = BeanUtils.toBean(jsonResp, Map.class);

        if (respMap != null && !respMap.isEmpty()) {
            if (respMap.get("errcode") != null) {
                logger.error("从微信服务端获取用户个人信息（UnionID机制）失败，微信返回结果：{}", respMap);
                throw new RuntimeException("从微信服务端获取用户个人信息（UnionID机制）失败：" + jsonResp);
            }

            logger.debug("从微信服务端获取用户个人信息（UnionID机制）:{}，accessToken：{}，appID：{}", respMap, accessToken, openId);

            UserInfo userInfo = BeanUtils.toBean(jsonResp, UserInfo.class);
            userInfo.setPrivilege(BeanUtils.toJson(respMap.get("privilege")));

            return  userInfo;
        } else {
            throw new RuntimeException("从微信服务端获取用户个人信息（UnionID机制）失败");
        }
    }

    private static Logger logger = LoggerFactory.getLogger(WeixinUtils.class);

    private WeixinUtils(){}
}
