package org.mys.example.demo;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>图形码工具类</p>
 *
 * @author 土味儿
 * @version 1.0
 * @Date 2023/6/9
 */
public class ImageCodeUtil {
    /**
     * 尺寸
     */
    enum Size {SMALL, MIDDLE, BIG}

    // ====================== 条形码内部类 ========================

    /**
     * 条形码
     */
    public static class BarCode {
        /**
         * 生成简单条形码（无文字）
         *
         * @param content
         * @param width
         * @param height
         * @return
         */
        public static BufferedImage create(String content, int width, int height) {
            // 定义位图矩阵BitMatrix
            BitMatrix matrix = null;

            try {
                // 定义二维码参数
                Map<EncodeHintType, Object> hints = new HashMap<>(3);
                // 设置编码
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                // 设置容错等级
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
                // 设置边距，默认为5
                hints.put(EncodeHintType.MARGIN, 3);

                // 使用code_128格式进行编码生成条形码
                MultiFormatWriter writer = new MultiFormatWriter();
                matrix = writer.encode(content, BarcodeFormat.CODE_128, width, height, hints);
            } catch (WriterException e) {
                e.printStackTrace();
                //throw new RuntimeException("条形码内容写入失败");
            }

            return MatrixToImageWriter.toBufferedImage(matrix);
        }

        /**
         * 生成条形码（含文字）
         * ****************************************************
         * ----------------------------------------------
         * |                         2023-06-10 10:55   |
         * |                                            |
         * |            商品名称 /超出不显示/              |
         * |                                            |
         * |    | || ||| | || |||| | | | ||| | | ||     |
         * |    | || ||| | || |||| | | | ||| | | ||     |
         * |    | || ||| | || |||| | | | ||| | | ||     |
         * |    | || ||| | || |||| | | | ||| | | ||     |
         * |               13800000000                  |
         * ----------------------------------------------
         * ===================================================
         * 1、日期：顶部右侧
         * 2、商品名称：居中
         * 3、条形码：商品名称下方，且居中
         * 4、条码内容：条形码下方，且居中
         * *****************************************************
         *
         * @param codeImage     条形码图片
         * @param bottomStr     底部文字
         * @param titleStr      标题文字
         * @param topRightStr   右上角文字
         * @param pictureWidth  图片宽度
         * @param pictureHeight 图片高度
         * @param margin        边距
         * @param fontSize      字体大小
         * @return 条形码图片
         */
        private static BufferedImage createWithWords(
                BufferedImage codeImage,
                String bottomStr,
                String titleStr,
                String topRightStr,
                int pictureWidth,
                int pictureHeight,
                int margin,
                int fontSize) {
            BufferedImage picImage = new BufferedImage(pictureWidth, pictureHeight, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = picImage.createGraphics();
            // 抗锯齿
            setGraphics2D(g2d);
            // 设置白色
            setColorWhite(g2d, picImage.getWidth(), picImage.getHeight());

            // 条形码默认居中显示
            int codeStartX = (pictureWidth - codeImage.getWidth()) / 2;
            int codeStartY = (pictureHeight - codeImage.getHeight()) / 2 + 2 * margin;
            // 画条形码到新的面板
            g2d.drawImage(codeImage, codeStartX, codeStartY, codeImage.getWidth(), codeImage.getHeight(), null);

            // 给条码上下各画一条线
            // 设置颜色
            g2d.setColor(Color.LIGHT_GRAY);
            int y1 = 2 * margin + codeImage.getHeight();
            int y2 = 2 * margin + 2 * codeImage.getHeight();
            g2d.drawLine(0, y1, pictureWidth, y1);
            g2d.drawLine(0, y2, pictureWidth, y2);

            // 画文字到新的面板
            g2d.setColor(Color.BLACK);
            // 字体、字型、字号
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, fontSize));
            // 文字与条形码之间的间隔
            int wordAndCodeSpacing = 3;

            // 底部文字（居中）
            if (StringUtils.isNotEmpty(bottomStr)) {
                // 文字长度
                int strWidth = g2d.getFontMetrics().stringWidth(bottomStr);
                // 文字X轴开始坐标
                int strStartX = (pictureWidth - strWidth) / 2;
                if (strStartX < margin) {
                    strStartX = margin;
                }
                // 文字Y轴开始坐标
                int strStartY = codeStartY + codeImage.getHeight() + fontSize + wordAndCodeSpacing;
                // 画文字
                g2d.drawString(bottomStr, strStartX, strStartY);
            }

            // 右上角文字
            if (StringUtils.isNotEmpty(topRightStr)) {
                // 文字长度
                int strWidth = g2d.getFontMetrics().stringWidth(topRightStr);
                // 文字X轴开始坐标
                int strStartX = pictureWidth - strWidth - margin;
                // 文字Y轴开始坐标
                int strStartY = margin + fontSize;
                // 画文字
                g2d.drawString(topRightStr, strStartX, strStartY);
            }

            // 标题文字（居中）
            if (StringUtils.isNotEmpty(titleStr)) {
                // 字体、字型、字号
                int fs = (int) Math.ceil(fontSize * 1.5);
                g2d.setFont(new Font("微软雅黑", Font.PLAIN, fs));
                // 文字长度
                int strWidth = g2d.getFontMetrics().stringWidth(titleStr);
                // 文字X轴开始坐标
                int strStartX = (pictureWidth - strWidth) / 2;
                if (strStartX < margin) {
                    strStartX = margin;
                }
                // 文字Y轴开始坐标
                int strStartY = codeStartY - margin;
                // 画文字
                g2d.drawString(titleStr, strStartX, strStartY);
            }

            g2d.dispose();
            picImage.flush();

            return picImage;
        }

        /**
         * 设置 Graphics2D 属性 （抗锯齿）
         *
         * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
         */
        private static void setGraphics2D(Graphics2D g2d) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
            Stroke s = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
            g2d.setStroke(s);
        }

        /**
         * 设置背景为白色
         *
         * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
         */
        private static void setColorWhite(Graphics2D g2d, int width, int height) {
            g2d.setColor(Color.WHITE);
            // 填充整个屏幕
            g2d.fillRect(0, 0, width, height);
            // 设置笔刷
            g2d.setColor(Color.BLACK);
        }

        /**
         * 创建小号条形码（250x150）
         *
         * @param code
         * @param name
         * @return
         */
        private static BufferedImage createSmallWithWords(@NonNull String code, @NonNull String name) {
            // 条形码底部内容
            String bottomStr = code;
            // 条形码左上角内容
            //String topLeftStr = name.length() < 11 ? name : name.substring(0, 11);
            String topLeftStr = name;
            // 条形码右上角内容
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String topRightStr = dtf.format(LocalDateTime.now());

            // 生成条形码
            BufferedImage barCodeImage = create(bottomStr, 250, 50);

            return createWithWords(
                    barCodeImage,
                    bottomStr,
                    topLeftStr,
                    topRightStr,
                    250,
                    150,
                    10,
                    14);
        }

        /**
         * 创建中号条形码（350x210）
         *
         * @param code
         * @param name
         * @return
         */
        private static BufferedImage createMiddleWithWords(@NonNull String code, @NonNull String name) {
            // 条形码底部内容
            String bottomStr = code;
            // 条形码左上角内容
            //String topLeftStr = name.length() < 11 ? name : name.substring(0, 11);
            String topLeftStr = name;
            // 条形码右上角内容
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String topRightStr = dtf.format(LocalDateTime.now());

            // 生成条形码
            BufferedImage barCodeImage = create(bottomStr, 350, 70);

            return createWithWords(
                    barCodeImage,
                    bottomStr,
                    topLeftStr,
                    topRightStr,
                    350,
                    210,
                    14,
                    20);
        }

        /**
         * 创建大号条形码（500x300）
         *
         * @param code
         * @param name
         * @return
         */
        private static BufferedImage createBigWithWords(@NonNull String code, @NonNull String name) {
            // 条形码底部内容
            String bottomStr = code;
            // 条形码左上角内容
            //String topLeftStr = name.length() < 11 ? name : name.substring(0, 11);
            String topLeftStr = name;
            // 条形码右上角内容
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String topRightStr = dtf.format(LocalDateTime.now());

            // 生成条形码
            BufferedImage barCodeImage = create(bottomStr, 500, 100);

            return createWithWords(
                    barCodeImage,
                    bottomStr,
                    topLeftStr,
                    topRightStr,
                    500,
                    300,
                    20,
                    28);
        }

        /**
         * 生成条形码（带文字）
         *
         * @param code 编码内容
         * @param name 名称
         * @param size 规格：小中大
         * @return
         */
        public static BufferedImage createWithWords(
                @NonNull String code,
                @NonNull String name,
                @NonNull Size size
        ) {
            switch (size) {
                case SMALL:
                    return createSmallWithWords(code, name);
                case MIDDLE:
                    return createMiddleWithWords(code, name);
                case BIG:
                default:
                    return createBigWithWords(code, name);
            }
        }
    }

    // ====================== 二维码内部类 ========================

    /**
     * 二维码
     */
    public static class QrCode {
        /**
         * 生成二维码（正方形）
         *
         * @param content
         * @param sideLength 边长（px）
         * @return
         */
        public static BufferedImage create(String content, int sideLength) {
            BitMatrix matrix = null;
            // 定义二维码参数
            Map<EncodeHintType, Object> hints = new HashMap<>(3);
            // 设置编码
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            // 设置容错等级
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 设置边距，默认为5
            hints.put(EncodeHintType.MARGIN, 3);

            try {
                matrix = new MultiFormatWriter()
                        .encode(content, BarcodeFormat.QR_CODE, sideLength, sideLength, hints);
            } catch (Exception e) {
                e.printStackTrace();
                //throw new RuntimeException("条形码内容写入失败");
            }
            return MatrixToImageWriter.toBufferedImage(matrix);
        }

        public static BufferedImage create(String content, Size size) {
            switch (size) {
                case SMALL:
                    return create(content, 250);
                case MIDDLE:
                    return create(content, 350);
                case BIG:
                default:
                    return create(content, 500);
            }
        }
    }

    // ======================== 存入文件 =========================

    /**
     * 存入文件
     *
     * @param barCodeImage
     * @return 文件名
     */
    public static String toFile(BufferedImage barCodeImage) {
        try {
            String fileName = getFileName();
            File file = new File(getImageDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            boolean isWrite = ImageIO.write(barCodeImage, "png", outputStream);
            if (isWrite) {
                return fileName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 得到图片存放目录
     * 类路径下的image文件夹
     *
     * @return
     */
    private static String getImageDir() {
        String imageDir = "";
        Resource resource = new ClassPathResource("");
        try {
            String _dir = resource.getFile().getAbsolutePath();
            File file = new File(_dir, "image");
            if (!file.exists()) {
                file.mkdirs();
            }
            imageDir = file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageDir;
    }

    /**
     * 生成文件名
     * 当前时间戳 + 两位随机数
     *
     * @return
     */
    private static String getFileName() {
        // 当前时间戳
        long second = Instant.now().getEpochSecond();

        // 两位随机数
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int rand = r.nextInt(10, 99);

        return new StringBuffer().append(second).append(rand).append(".png").toString();
    }

    // ======================= 识别图形码 ========================

    /**
     * 识别图形码（条形码/二维码）
     * ***********************************************
     * ------ 条形码 ----------------------------------
     * 可以识别内容：手机号、纯数字、字母数字、网址(中大号)
     * 识别不了小号(250x150)含文字的网址条形码
     * 存储网址时用二维码，不要使用条形码
     * ***********************************************
     * ------ 二维码 ----------------------------------
     * 比条形码有优势；能识别三种尺寸的网址二维码
     * ***********************************************
     *
     * @param imageCode 图形码文件
     * @return void
     */
    public static String scan(File imageCode) {
        String text = "";
        try {
            BufferedImage image = ImageIO.read(imageCode);
            if (image == null) {
                return text;
            }
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Map<DecodeHintType, Object> hints = new HashMap<>(3);
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            Result result = new MultiFormatReader().decode(bitmap, hints);
            text = result.getText();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("无法识别");
        }
        return text;
    }

    public static String scan(String fileName) {
        return scan(new File(getImageDir(), fileName));
    }

    public static String scan(String fileName, String fileParent) {
        return scan(new File(fileParent, fileName));
    }
}
