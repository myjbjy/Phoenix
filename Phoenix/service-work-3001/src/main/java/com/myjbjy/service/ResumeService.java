package com.myjbjy.service;

/**
 * 简历service
 * @author myj
 */
public interface ResumeService {

    /**
     * 用户注册的时候初始化简历
     * @param userId
     */
    public void initResume(String userId);
}
