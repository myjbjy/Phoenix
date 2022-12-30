package com.myjbjy.api.intercept;

import com.myjbjy.base.BaseInfoProperties;
import com.myjbjy.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author myj
 */
@Slf4j
public class SMSInterceptor extends BaseInfoProperties implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获得用户ip
        String userIp = IPUtil.getRequestIp(request);
        // 获得用于判断的boolean
        if (redis.keyIsExist(MOBILE_SMSCODE + ":" + userIp)){
            log.error("短信发送频率太高了~~！！！");
            return false;
        }
        return true;
    }
}
