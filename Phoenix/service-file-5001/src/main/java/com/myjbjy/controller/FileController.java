package com.myjbjy.controller;

import com.myjbjy.config.MinIOConfig;
import com.myjbjy.grace.result.GraceJSONResult;
import com.myjbjy.grace.result.ResponseStatusEnum;
import com.myjbjy.pojo.bo.Base64FileBO;
import com.myjbjy.utils.Base64ToFile;
import com.myjbjy.utils.MinIOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.util.UUID;

/**
 * @author myj
 */
@RestController
@RequestMapping("file")
public class FileController {

    public static final String HOST = "http://192.168.1.6:8000/";

    @Resource
    private MinIOConfig minIOConfig;

    @PostMapping("uploadFace")
    public GraceJSONResult uploadFace(@RequestParam("file") MultipartFile file,
                                       @RequestParam("userId") String userId
                                      ) throws Exception {
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        // 获得文件原始名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        filename = userId + File.separator + filename;
        MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream());

        String imageUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + filename;
        return GraceJSONResult.ok(imageUrl);
    }

    @PostMapping("uploadFace1")
    public GraceJSONResult uploadFace1(@RequestParam("file") MultipartFile file,
                                       @RequestParam("userId") String userId,
                                       HttpServletRequest request) throws Exception {
        // 获得文件原始名称
        String filename = file.getOriginalFilename();

        //        "abc.123.abc.png"
        // 根据文件名中最后一个点的位置向后进行截取
        String suffixName = filename.substring(filename.lastIndexOf("."));

        // 文件新的名称
        String newFileName = userId + suffixName;

        // 设置文件存储的路径，可以存放在指定的路径中，windows用户需要修改为对应的盘符
        String rootPath = "/temp" + File.separator;
        // 图片存储的完全路径
        String filePath = rootPath + File.separator + "face" + File.separator + newFileName;

        File newFile = new File(filePath);
        if (!newFile.getParentFile().exists()) {
            // 如果目标文件所在目录不存在，则创建父目录
            newFile.getParentFile().mkdirs();
        }

        // 将内存中的文件数据写入到磁盘
        file.transferTo(newFile);

        // 生成web可以被访问的url地址
        String userFaceUrl = HOST + "static/face/" + newFileName;

        return GraceJSONResult.ok(userFaceUrl);
    }

    @PostMapping("uploadAdminFace")
    public GraceJSONResult uploadAdminFace(@RequestBody @Valid Base64FileBO base64FileBO) throws Exception {

        String base64 = base64FileBO.getBase64File();

        String suffixName = ".png"; // 后缀
        String uuid = UUID.randomUUID().toString(); // 文件名
        String objectName = uuid + suffixName;  // 对象名

        String rootPath = "/temp" + File.separator;
        String filePath = rootPath
                + File.separator
                + "adminFace"
                + File.separator
                + objectName;

        Base64ToFile.Base64ToFile(base64, filePath);

        MinIOUtils.uploadFile(minIOConfig.getBucketName(), objectName, filePath);

        String imageUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + objectName;

        return GraceJSONResult.ok(imageUrl);
    }

    @PostMapping("uploadLogo")
    public GraceJSONResult uploadLogo(@RequestParam("file") MultipartFile file) throws Exception {

        // 获得文件原始名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        filename = "company/logo/" + filename;
        MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream());

        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);
        return GraceJSONResult.ok(imageUrl);
    }

    @PostMapping("uploadBizLicense")
    public GraceJSONResult uploadBizLicense(@RequestParam("file") MultipartFile file) throws Exception {

        // 获得文件原始名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        filename = "company/bizLicense/" + filename;
        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);
        return GraceJSONResult.ok(imageUrl);
    }
}
