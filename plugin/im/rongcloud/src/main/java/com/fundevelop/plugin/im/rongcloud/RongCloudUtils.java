package com.fundevelop.plugin.im.rongcloud;

import com.fundevelop.commons.utils.BeanUtils;
import com.fundevelop.commons.web.utils.PropertyUtil;
import io.rong.ApiHttpClient;
import io.rong.models.ChatroomInfo;
import io.rong.models.FormatType;
import io.rong.models.SdkHttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 融云IM工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/1 23:57
 */
public class RongCloudUtils {
    /**
     * 获取 Token.
     * @param userId 用户 Id，最大长度 64 字节。是用户在 App 中的唯一标识码，必须保证在同一个 App 内不重复，重复的用户 Id 将被当作是同一用户。（必传）
     * @param userName 用户名称，最大长度 128 字节。用来在 Push 推送时显示用户的名称。（必传）
     * @param userAvatarUrl 用户头像 URI，最大长度 1024 字节。用来在 Push 推送时显示用户的头像。（必传）
     */
    public static String getToken(String userId, String userName, String userAvatarUrl) {
        String token = null;
        String appKey = PropertyUtil.get("im.rongcloud.appKey");
        String appSecret = PropertyUtil.get("im.rongcloud.appSecret");

        if (StringUtils.isBlank(appKey) || StringUtils.isBlank(appSecret)) {
            throw new RuntimeException("请在application.properties中配置融云的appKey及appSecret");
        }

        try {
            SdkHttpResult result = ApiHttpClient.getToken(appKey, appSecret, userId, userName, userAvatarUrl, FormatType.json.json);
            logger.debug("向融云请求获取Token，融云响应结果为：{}", result);

            if (StringUtils.isNotBlank(result.getResult())) {
                Map<String, Object> resultMap = BeanUtils.toBean(result.getResult(), Map.class);

                if (resultMap != null && Integer.valueOf(200).equals(resultMap.get("code"))) {
                    if (userId.equals(resultMap.get("userId"))) {
                        token = resultMap.get("token").toString();
                    } else {
                        throw new RuntimeException("向融云请求获取Token请求时，融云返回结果中的用户ID与请求的用户ID不一致");
                    }
                } else {
                    throw new RuntimeException("向融云请求获取Token请求时，融云返回结果为失败");
                }
            } else {
                throw new RuntimeException("向融云请求获取Token请求时，融云无返回结果");
            }
        } catch (Exception e) {
            logger.error("向融云请求获取Token发生异常，用户ID：{}", userId, e);
            throw new RuntimeException("向融云请求获取Token发生异常", e);
        }

        return token;
    }

    /**
     * 刷新用户信息.
     * @param userId 用户 Id，最大长度 64 字节。是用户在 App 中的唯一标识码，必须保证在同一个 App 内不重复，重复的用户 Id 将被当作是同一用户。（必传）
     * @param userName 用户名称，最大长度 128 字节。用来在 Push 推送时，显示用户的名称，刷新用户名称后 5 分钟内生效。（可选，提供即刷新，不提供忽略）
     * @param userAvatarUrl 用户头像 URI，最大长度 1024 字节。用来在 Push 推送时显示。（可选，提供即刷新，不提供忽略）
     */
    public static void refreshUser(String userId, String userName, String userAvatarUrl) {
        String appKey = PropertyUtil.get("im.rongcloud.appKey");
        String appSecret = PropertyUtil.get("im.rongcloud.appSecret");

        if (StringUtils.isBlank(appKey) || StringUtils.isBlank(appSecret)) {
            throw new RuntimeException("请在application.properties中配置融云的appKey及appSecret");
        }

        try {
            SdkHttpResult result = ApiHttpClient.refreshUser(appKey, appSecret, userId, userName, userAvatarUrl, FormatType.json.json);
            logger.debug("向融云请求刷新用户信息，融云响应结果为：{}", result);

            if (StringUtils.isNotBlank(result.getResult())) {
                Map<String, Object> resultMap = BeanUtils.toBean(result.getResult(), Map.class);

                if (resultMap == null || !Integer.valueOf(200).equals(resultMap.get("code"))) {
                    throw new RuntimeException("向融云请求刷新用户信息时，融云返回结果为失败");
                }
            } else {
                throw new RuntimeException("向融云请求刷新用户信息时，融云无返回结果");
            }
        } catch (Exception e) {
            logger.error("向融云请求刷新用户信息发生异常", userId, e);
            throw new RuntimeException("向融云请求刷新用户信息发生异常", e);
        }
    }

    /**
     * 创建聊天室.
     * @param roomId 要创建聊天室ID
     * @param roomName 要创建聊天室名称
     */
    public static void createChatroom(String roomId, String roomName) {
        String appKey = PropertyUtil.get("im.rongcloud.appKey");
        String appSecret = PropertyUtil.get("im.rongcloud.appSecret");

        if (StringUtils.isBlank(appKey) || StringUtils.isBlank(appSecret)) {
            throw new RuntimeException("请在application.properties中配置融云的appKey及appSecret");
        }

        try {
            List<ChatroomInfo> chats = new ArrayList<>(1);
            chats.add(new ChatroomInfo(roomId, roomName));
            SdkHttpResult result = ApiHttpClient.createChatroom(appKey, appSecret, chats, FormatType.json);

            logger.debug("向融云请求创建聊天室，融云响应结果为：{}", result);

            if (StringUtils.isNotBlank(result.getResult())) {
                Map<String, Object> resultMap = BeanUtils.toBean(result.getResult(), Map.class);

                if (resultMap == null || !Integer.valueOf(200).equals(resultMap.get("code"))) {
                    throw new RuntimeException("向融云请求创建聊天室时，融云返回结果为失败");
                }
            } else {
                throw new RuntimeException("向融云请求创建聊天室时，融云无返回结果");
            }
        } catch (Exception e) {
            logger.error("向融云请求创建聊天室发生异常，聊天室ID：{}，名称：{}", roomId, roomName, e);
            throw new RuntimeException("向融云请求创建聊天室发生异常", e);
        }
    }

    /**
     * 查询聊天室.
     * @param roomId 要查询的聊天室ID
     */
    public static ChatroomInfo queryChatroom(String roomId) {
        String appKey = PropertyUtil.get("im.rongcloud.appKey");
        String appSecret = PropertyUtil.get("im.rongcloud.appSecret");

        if (StringUtils.isBlank(appKey) || StringUtils.isBlank(appSecret)) {
            throw new RuntimeException("请在application.properties中配置融云的appKey及appSecret");
        }

        try {
            List<String> chatIds = new ArrayList<>(1);
            chatIds.add(roomId);
            SdkHttpResult result = ApiHttpClient.queryChatroom(appKey, appSecret, chatIds, FormatType.json);

            logger.debug("向融云查询聊天室，融云响应结果为：{}", result);

            if (StringUtils.isNotBlank(result.getResult())) {
                Map<String, Object> resultMap = BeanUtils.toBean(result.getResult(), Map.class);

                if (resultMap == null || !Integer.valueOf(200).equals(resultMap.get("code"))) {
                    throw new RuntimeException("向融云查询聊天室时，融云返回结果为失败");
                }

                List<Map<String, Object>> chatRooms = (List<Map<String, Object>>)resultMap.get("chatRooms");

                if (chatRooms != null && !chatRooms.isEmpty()) {
                    Map<String, Object> chatRoom = chatRooms.get(0);

                    if (chatRoom != null) {
                        return new ChatroomInfo((String)chatRoom.get("chrmId"), (String)chatRoom.get("name"));
                    }
                }
            } else {
                throw new RuntimeException("向融云查询聊天室时，融云无返回结果");
            }
        } catch (Exception e) {
            logger.error("向融云查询聊天室发生异常，聊天室ID：{}", roomId, e);
            throw new RuntimeException("向融云查询聊天室发生异常", e);
        }

        return null;
    }

    /**
     * 销毁聊天室.
     * @param roomId 要销毁的聊天室 Id。（必传）
     */
    public static void destroyChatroom(String roomId) {
        String appKey = PropertyUtil.get("im.rongcloud.appKey");
        String appSecret = PropertyUtil.get("im.rongcloud.appSecret");

        if (StringUtils.isBlank(appKey) || StringUtils.isBlank(appSecret)) {
            throw new RuntimeException("请在application.properties中配置融云的appKey及appSecret");
        }

        try {
            List<String> chatIds = new ArrayList<>(1);
            chatIds.add(roomId);
            SdkHttpResult result = ApiHttpClient.destroyChatroom(appKey, appSecret, chatIds, FormatType.json);

            logger.debug("向融云请求销毁聊天室，融云响应结果为：{}", result);

            if (StringUtils.isNotBlank(result.getResult())) {
                Map<String, Object> resultMap = BeanUtils.toBean(result.getResult(), Map.class);

                if (resultMap == null || !Integer.valueOf(200).equals(resultMap.get("code"))) {
                    throw new RuntimeException("向融云请求销毁聊天室时，融云返回结果为失败");
                }
            } else {
                throw new RuntimeException("向融云请求销毁聊天室时，融云无返回结果");
            }
        } catch (Exception e) {
            logger.error("向融云请求销毁聊天室发生异常，聊天室ID：{}", roomId, e);
            throw new RuntimeException("向融云请求销毁聊天室发生异常", e);
        }
    }

    private RongCloudUtils() {}

    private static Logger logger = LoggerFactory.getLogger(RongCloudUtils.class);
}
