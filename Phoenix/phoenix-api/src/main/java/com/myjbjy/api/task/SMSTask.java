package com.myjbjy.api.task;

import com.myjbjy.api.retry.RetryComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class SMSTask {

    @Resource
    private RetryComponent retryComponent;

    @Async
    public void sendSMSTask() {
        boolean res = retryComponent.sendSmsWithRetry();
        log.info("异步任务 - 最终运行结果为 res = {}", res);
    }

}
