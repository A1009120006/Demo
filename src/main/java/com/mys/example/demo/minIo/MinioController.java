package com.mys.example.demo.minIo;

import com.mys.example.demo.framework.common.Res;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/minio")
public class MinioController {

    @Autowired
    private MinioUtil minioUtil;

    /**
     * 创建桶
     *
     * @param bucketName
     * @return Boolean
     */
    @GetMapping("/createBucket/{bucketName}")
    public Res<Boolean> createBucket(@PathVariable String bucketName) {
        if (minioUtil.existBucket(bucketName)) {
            return Res.error("已存在");
        }
        return minioUtil.makeBucket(bucketName) ? Res.success(true) : Res.error("创建失败");
    }

    /**
     * 删除桶
     *
     * @param bucketName
     * @return Boolean
     */
    @GetMapping("/removeBucketByName/{bucketName}")
    public Res<Boolean> removeBucketByName(@PathVariable String bucketName) {
        if (!minioUtil.existBucket(bucketName)) return Res.error("未查到该桶");
        return minioUtil.removeBucket(bucketName) ? Res.success(true) : Res.error("删除失败");
    }

    /**
     * 单文件上传
     *
     * @param file
     * @param bucketName
     * @return String 文件路径
     */
    @PostMapping("/uploadSingleFileByFileOrBucketName")
    public Res<String> uploadSingleFileByFileOrBucketName(@RequestParam MultipartFile file,
                                                          @RequestParam String bucketName) {
        return (bucketName.equals("") || minioUtil.existBucket(bucketName))
                ? Res.success(minioUtil.upload(file, bucketName))
                : Res.error("该桶不存在");
    }

    /**
     * 批量文件上传
     *
     * @param files
     * @param bucketName
     * @return Map 文件文件路径集合
     */
    @PostMapping("/uploadClusterFileByFileOrBucketName")
    public Res<Map<String, String>> uploadClusterFileByFileOrBucketName(@RequestParam MultipartFile[] files,
                                                                        @RequestParam String bucketName) {
        return (bucketName.equals("") || minioUtil.existBucket(bucketName))
                ? Res.success(minioUtil.upload(files, bucketName))
                : Res.error("该桶不存在");
    }

    /**
     * 单文件下载（因为存在多级目录，通过文件路径）
     *
     * @param filePath
     * @param response
     * @param request
     * @return Boolean
     */
    @PostMapping("/downloadSingleFileByFilePath")
    public void downloadSingleFileByFilePath(@RequestParam String filePath,
                                             HttpServletResponse response, HttpServletRequest request) {
        minioUtil.download(response, request, filePath);
    }

    /**
     * 批量文件下载（因为存在多级目录，通过文件路径）
     *
     * @param filePaths
     * @param response  可为空
     * @param response
     * @param request
     */
    @PostMapping("/downloadClusterFileByFilePaths")
    public void downloadClusterFileByFilePaths(@RequestParam List<String> filePaths, @RequestParam String zipName,
                                               HttpServletResponse response, HttpServletRequest request) {
        minioUtil.download(response, request, filePaths, zipName);
    }

    /**
     * 单文件删除（多级目录，使用文件路径）
     *
     * @param filePath
     * @return Boolean
     */
    @PostMapping("/removeSingleFileByFilePath")
    public Res<Boolean> removeSingleFileByFilePath(@RequestParam String filePath) {
        return minioUtil.removeFile(filePath) ? Res.success(true) : Res.error("删除失败");
    }

    /**
     * 文件批量删除（多级目录，使用文件路径）
     *
     * @param filePaths
     * @return Map
     */
    @PostMapping("/removeClusterFileByFilePaths")
    public Res<Map<String, String>> removeClusterFileByFilePaths(@RequestParam List<String> filePaths) {
        return Res.success(minioUtil.removeFile(filePaths));
    }
}
