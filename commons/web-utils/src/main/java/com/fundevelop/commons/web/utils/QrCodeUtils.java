package com.fundevelop.commons.web.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * 二维码工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/5/10 17:05
 */
public class QrCodeUtils {
    /**
     * 生成二维码.
     * @param url URL地址
     * @param width 二维码图片宽度
     * @param height 二维码图片高度
     */
    public static BufferedImage create(String url, int width, int height) throws WriterException {
        Hashtable hints = new Hashtable();
        //内容所使用编码
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);

        //生成二维码
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * 将图片写入磁盘.
     * @throws IOException
     */
    public static void writeToFile(BufferedImage image, File file) throws IOException {
        String format = file.getName();
        format = format.substring(format.lastIndexOf(".")+1);

        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    private QrCodeUtils(){}
}
