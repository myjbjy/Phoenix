package com.myjbjy.api.retry;

import com.myjbjy.exceptions.GraceException;
import com.myjbjy.grace.result.ResponseStatusEnum;
import com.myjbjy.utils.SMSUtilsRetry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author myj
 */
@Component
@Slf4j
public class RetryComponent {

    @Retryable(
//            value = {
//                    Exception.class
////                IllegalArgumentException.class,
////                ArrayIndexOutOfBoundsException.class
//            },     // 指定重试的异常，不是这个则不重试
            exclude = NullPointerException.class,   // 指定不去重试的异常，抛出的这个异常不会进行重试
            maxAttempts = 5,         // 重试的总次数
            backoff = @Backoff(
                    delay = 1000L,
                    multiplier = 2
            )   // 重试间隔为1秒，后续的重试为次数的2，1/2/4/8
    )
    public boolean sendSmsWithRetry() {
        log.info("当前时间 Time={}", LocalDateTime.now());
        return SMSUtilsRetry.sendSMS();
    }

    // 达到最大重试次数，或者抛出一个没有被设置（进行重试）的异常
    // 可以作为方法的最终兜底处理，如果不处理，也行随意
    @Recover
    public boolean recover() {
        GraceException.display(ResponseStatusEnum.SYSTEM_SMS_FALLBACK_ERROR);
        return false;
    }

}
