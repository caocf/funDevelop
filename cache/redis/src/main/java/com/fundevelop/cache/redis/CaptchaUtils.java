package com.fundevelop.cache.redis;

import com.fundevelop.commons.web.utils.PropertyUtil;
import nl.captcha.Captcha;
import nl.captcha.backgrounds.FlatColorBackgroundProducer;
import nl.captcha.servlet.CaptchaServletUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;

/**
 * 图片验证码工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/5/24 18:26
 */
public class CaptchaUtils {
    /**
     * 生成验证码.
     * @param clientCode 客户端唯一标示码
     * @param response 输出流
     */
    public static void writeCaptcha(String clientCode, HttpServletResponse response) {
        if (StringUtils.isBlank(clientCode)) {
            throw new RuntimeException("客户端唯一标示码不能为空");
        }

        Color backgroundColor = Color.ORANGE;

        Captcha captcha = new Captcha.Builder(
                Integer.parseInt(PropertyUtil.get("captcha.img.width", "140"), 10),
                Integer.parseInt(PropertyUtil.get("captcha.img.height", "40"), 10)
        ).addText().gimp().addBorder().addNoise()
                .addBackground(new FlatColorBackgroundProducer(backgroundColor)).build();

        RedisUtil.set(CACHEKEY+clientCode, captcha.getAnswer().toLowerCase(), Integer.parseInt(PropertyUtil.get("captcha.cacheTime", "1800")));
        CaptchaServletUtil.writeImage(response, captcha.getImage());
    }

    /**
     * 获取验证码.
     * @param clientCode 客户端唯一标示码
     */
    public static String getCaptcha(String clientCode) {
        return RedisUtil.get(CACHEKEY+clientCode);
    }

    private CaptchaUtils(){}

    private final static String CACHEKEY = "_captcha:";
}
