package com.myjbjy.api;

import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @author myj
 */
@Slf4j
@Component
@Aspect
public class SeataTransactionAspect {

    /**
     * 调用service之前，手动加入或者创建全局事务
     * @param joinPoint
     * @throws TransactionException
     */
    @Before("execution(* com.myjbjy.service.impl..*.*(..))")
    public void beginTransaction(JoinPoint joinPoint) throws TransactionException {
        log.info("手动开启全局事务");
        // 手动开启全局事务
        GlobalTransaction gt = GlobalTransactionContext.getCurrentOrCreate();
        gt.begin();
    }

    /**
     * 捕获异常，则手动回滚全局事务
     * @param throwable
     * @throws Throwable
     */
    @AfterThrowing(
            throwing = "throwable",
            pointcut = "execution(* com.myjbjy.service.impl..*.*(..))"
    )
    public void seataRollback(Throwable throwable) throws Throwable {

        log.info("捕获到异常信息，则回滚，异常信息为：" + throwable.getMessage());

        // 从当前线程获得xid
        String xid = RootContext.getXID();
        if (StringUtils.isNotBlank(xid)) {
            GlobalTransactionContext.reload(xid).rollback();
        }
    }
}
