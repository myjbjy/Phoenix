package com.myjbjy.controller;

import com.myjbjy.grace.result.GraceJSONResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * @author myj
 */
@RestController
@RequestMapping("file")
public class FileController {

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

        return GraceJSONResult.ok();
    }
}
