package com.myjbjy.service.impl;

import com.myjbjy.mapper.ResumeMapper;
import com.myjbjy.pojo.Resume;
import com.myjbjy.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author myj
 */
@Slf4j
@Service
public class ResumeServiceImpl implements ResumeService {

    @Resource
    private ResumeMapper resumeMapper;

    @Transactional
    @Override
    public void initResume(String userId) {
        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setCreateTime(LocalDateTime.now());
        resume.setUpdatedTime(LocalDateTime.now());

        // 模拟除零异常
//        int a = 1 / 0;

        resumeMapper.insert(resume);
    }
}
