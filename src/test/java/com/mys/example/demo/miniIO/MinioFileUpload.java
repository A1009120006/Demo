package com.mys.example.demo.miniIO;

import io.minio.*;
import io.minio.errors.*;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class MinioFileUpload {
    public static void main(String[] args) {
        try{
            MinioClient minioClient = MinioClient
                    .builder().endpoint("http://10.20.11.146:9000")
                    .credentials("liuchuxing","leoc@123").build();
            /*
            存储桶的命名定义规则为：
            1.存储桶名称必须介于 3 到 63 个字符之间
            2.存储桶名称只能由小写字母、数字、句点 (.) 和连字符 (-) 组成
            3.存储桶名称必须以字母或数字开头和结尾
            4.存储桶名称不得采用 IP 地址格式
            */
//            String uploadFile = "D:\\tmp\\test.mp4";
//            String uploadFile = "D:\\tmp\\test.pptx";
            String uploadFile = "D:\\tmp\\test.txt";
            String downloadFilePath = "D:\\tmp\\download\\";

            String bucketName = "srm-default";

//            boolean found = minioClient
//                    .bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
//            if(!found){
//                //如果不存在就创建
//                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
//            }


            //上传文件
            String uploadFileName = uploadFile.substring(uploadFile.lastIndexOf("\\") + 1);
            String minIoFileNameTemplate = uploadFileName.substring(0, uploadFileName.lastIndexOf("."))
                    + "_%s" + uploadFileName.substring(uploadFileName.lastIndexOf("."));
            Date startUpload = new Date();
            for(int i = 1; i <= 1000; i++) {
                String minIoFileName = String.format(minIoFileNameTemplate, i);
                minioClient.uploadObject(UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(minIoFileName)
                        .filename(uploadFile)
                        .build());
            }
            Date endUpload = new Date();
            System.out.println("上传文件耗时："+(endUpload.getTime()-startUpload.getTime())+"毫秒");

            try {
                //下载文件
                Date startDownload = new Date();
                for(int i = 1; i <= 1000; i++) {
                    String minIoFileName = String.format(minIoFileNameTemplate, i);
                    minioClient.downloadObject(DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(minIoFileName)
                            .filename(downloadFilePath + minIoFileName)
                            .build());
                }
                Date endDownload = new Date();
                System.out.println("下载文件耗时："+(endDownload.getTime()-startDownload.getTime())+"毫秒");

                //删除下载文件
                for(int i = 1; i <= 1000; i++) {
                    String minIoFileName = String.format(minIoFileNameTemplate, i);
                    File file = new File(downloadFilePath + minIoFileName);
                    file.delete();
                }
            } catch (ErrorResponseException e) {
                throw new RuntimeException(e);
            } catch (InsufficientDataException e) {
                throw new RuntimeException(e);
            } catch (InternalException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (InvalidResponseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (ServerException e) {
                throw new RuntimeException(e);
            } catch (XmlParserException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } finally {
                //删除服务器文件
                for(int i = 1; i <= 100; i++) {
                    String minIoFileName = String.format(minIoFileNameTemplate, i);
                    minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(minIoFileName).build());
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

