package com.myjbjy.controller;

import com.myjbjy.grace.result.GraceJSONResult;
import com.myjbjy.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author myj
 */
@Slf4j
@RestController
@RequestMapping("resume")
public class ResumeController {

    @Resource
    private ResumeService resumeService;

    /**
     * 初始化用户简历
     * @param userId
     * @return
     */
    @PostMapping("init")
    public GraceJSONResult init(@RequestParam("userId") String userId) {
        resumeService.initResume(userId);
        return GraceJSONResult.ok();
    }
}
