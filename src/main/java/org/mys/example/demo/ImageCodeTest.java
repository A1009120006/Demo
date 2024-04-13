package org.mys.example.demo;

/**
 * <p>描述...</p>
 *
 * @author 土味儿
 * @version 1.0
 * @Date 2023/6/10
 */
public class ImageCodeTest {
    public static void main(String[] args) {

        String code = "1234567890abcdefg";
        String title = "商品名称(超出不显示)";

        String fileName = ImageCodeUtil.toFile(ImageCodeUtil.BarCode.createWithWords(code, title, ImageCodeUtil.Size.SMALL));
        String scanResult = ImageCodeUtil.scan(fileName);

        System.out.println("scanResult = " + scanResult);
    }

}
