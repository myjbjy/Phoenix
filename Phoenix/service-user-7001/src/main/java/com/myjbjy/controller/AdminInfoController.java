package com.myjbjy.controller;

import com.myjbjy.api.intercept.JWTCurrentUserInterceptor;
import com.myjbjy.base.BaseInfoProperties;
import com.myjbjy.pojo.Admin;
import com.myjbjy.pojo.bo.ResetPwdBO;
import com.myjbjy.pojo.bo.UpdateAdminBO;
import com.myjbjy.pojo.vo.AdminInfoVO;
import com.myjbjy.service.AdminService;
import com.myjbjy.grace.result.GraceJSONResult;
import com.myjbjy.pojo.bo.CreateAdminBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
@RequestMapping("admininfo")
@Slf4j
public class AdminInfoController extends BaseInfoProperties {

    @Resource
    private AdminService adminService;

    @PostMapping("create")
    public GraceJSONResult create(@Valid @RequestBody CreateAdminBO createAdminBO) {
        adminService.createAdmin(createAdminBO);
        return GraceJSONResult.ok();
    }

    @PostMapping("list")
    public GraceJSONResult list(String accountName,
                                Integer page,
                                Integer limit) {
        if (page == null) {
            page = 1;
        }
        if (limit == null) {
            limit = 10;
        }

        return GraceJSONResult.ok(adminService.getAdminList(accountName, page, limit));
    }

    @PostMapping("delete")
    public GraceJSONResult delete(String username) {
        adminService.deleteAdmin(username);
        return GraceJSONResult.ok();
    }

    @PostMapping("resetPwd")
    public GraceJSONResult resetPwd(@RequestBody ResetPwdBO resetPwdBO) {

        // resetPwdBO 校验
        // adminService 重置密码

        resetPwdBO.modifyPwd();
        return GraceJSONResult.ok();
    }

    @PostMapping("myInfo")
    public GraceJSONResult myInfo() {
        Admin admin = JWTCurrentUserInterceptor.adminUser.get();

        Admin adminInfo = adminService.getById(admin.getId());

        AdminInfoVO adminInfoVO = new AdminInfoVO();
        BeanUtils.copyProperties(adminInfo, adminInfoVO);

        return GraceJSONResult.ok(adminInfoVO);
    }

    @PostMapping("updateMyInfo")
    public GraceJSONResult updateMyInfo(@RequestBody @Valid UpdateAdminBO adminBO) {
        Admin admin = JWTCurrentUserInterceptor.adminUser.get();

        adminBO.setId(admin.getId());
        adminService.updateAdmin(adminBO);

        return GraceJSONResult.ok();
    }
}
