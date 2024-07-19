package com.mys.example.demo;

import com.mys.example.demo.framework.util.ImageCodeUtil;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * <p>描述...</p>
 *
 * @author 土味儿
 * @version 1.0
 * @Date 2023/6/10
 */
public class ImageCodeTest {
//    public static void main(String[] args) {
//
//        String code = "1234567890abcdefg";
//        String title = "商品名称(超出不显示)";
//
//        String fileName = ImageCodeUtil.toFile(ImageCodeUtil.BarCode.createWithWords(code, title, ImageCodeUtil.Size.SMALL));
//        String scanResult = ImageCodeUtil.scan(fileName);
//
//        System.out.println("scanResult = " + scanResult);
//    }

    public static long secondsBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Duration duration = Duration.between(startDateTime, endDateTime);
        return duration.getSeconds();
    }
    public static void main(String[] args) {
        LocalDateTime dateTime1 = LocalDateTime.now();
        LocalDateTime dateTime2 = dateTime1.plusSeconds(-30); // dateTime2比dateTime1晚30秒

        long secondsDifference = secondsBetween(dateTime1, dateTime2);
        System.out.println("两个时间相差的秒数为: " + secondsDifference);
    }
}
