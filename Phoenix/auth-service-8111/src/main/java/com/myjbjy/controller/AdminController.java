package com.myjbjy.controller;

import com.google.gson.Gson;
import com.myjbjy.base.BaseInfoProperties;
import com.myjbjy.grace.result.GraceJSONResult;
import com.myjbjy.grace.result.ResponseStatusEnum;
import com.myjbjy.pojo.Admin;
import com.myjbjy.pojo.bo.AdminBO;
import com.myjbjy.service.AdminService;
import com.myjbjy.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author myj
 */
@RestController
@RequestMapping("admin")
@Slf4j
public class AdminController extends BaseInfoProperties {

    @Resource
    private AdminService adminService;

    @Resource
    private JWTUtils jwtUtils;

    @PostMapping("login")
    public GraceJSONResult login(@Valid @RequestBody AdminBO adminBO){
        // 执行登录判断用户是否存在
        boolean isExist = adminService.adminLogin(adminBO);

        if (!isExist) {
            return GraceJSONResult.errorCustom(
                    ResponseStatusEnum.ADMIN_LOGIN_ERROR);
        }

        // 登录成功之后获得admin信息
        Admin admin = adminService.getAdminInfo(adminBO);
        String adminToken = jwtUtils.createJWTWithPrefix(new Gson().toJson(admin),
                TOKEN_ADMIN_PREFIX);

        return GraceJSONResult.ok(adminToken);
    }

    @PostMapping("logout")
    public GraceJSONResult logout() {
        return GraceJSONResult.ok();
    }

}
