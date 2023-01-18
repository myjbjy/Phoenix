package com.myjbjy.controller;

import com.myjbjy.base.BaseInfoProperties;
import com.myjbjy.service.AdminService;
import com.myjbjy.grace.result.GraceJSONResult;
import com.myjbjy.pojo.bo.CreateAdminBO;
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
}
