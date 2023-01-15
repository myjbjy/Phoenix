package com.myjbjy.api.intercept;

import com.google.gson.Gson;
import com.myjbjy.base.BaseInfoProperties;
import com.myjbjy.pojo.Admin;
import com.myjbjy.pojo.Users;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author myj
 */
public class JWTCurrentUserInterceptor extends BaseInfoProperties implements HandlerInterceptor {

    public static ThreadLocal<Users> currentUser = new ThreadLocal<>();

    public static ThreadLocal<Admin> adminUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        // 使用ThreadLocal可以在同一个线程内共享数据
        // 比如：Interceptor --> 控制器Controller --> 业务层Service --> 数据层Mapper(dao)
        String appUserJson = request.getHeader(APP_USER_JSON);
        String saasUserJson = request.getHeader(SAAS_USER_JSON);
        String adminUserJson = request.getHeader(ADMIN_USER_JSON);

        if (StringUtils.isNotBlank(appUserJson)
                || StringUtils.isNotBlank(saasUserJson)) {
            currentUser.set(new Gson().fromJson(appUserJson, Users.class));
        }

        if (StringUtils.isNotBlank(adminUserJson)) {
            adminUser.set(new Gson().fromJson(adminUserJson, Admin.class));
        }

        /**
         * false: 请求被拦截
         * true: 放行，请求验证通过
         */
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        currentUser.remove();
        adminUser.remove();
    }
}
