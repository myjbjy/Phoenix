package com.myjbjy.controller;

import com.myjbjy.grace.result.GraceJSONResult;
import com.myjbjy.pojo.Company;
import com.myjbjy.pojo.vo.CompanySimpleVO;
import com.myjbjy.service.CompanyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author myj
 */
@RestController
@RequestMapping("company")
public class CompanyController {

    @Resource
    private CompanyService companyService;

    @PostMapping("getByFullName")
    public GraceJSONResult getByFullName(String fullName) {

        if (StringUtils.isBlank(fullName)) {
            return GraceJSONResult.error();
        }

        Company company = companyService.getByFullName(fullName);
        if (company == null) {
            return GraceJSONResult.ok(null);
        }

        CompanySimpleVO companySimpleVO = new CompanySimpleVO();
        BeanUtils.copyProperties(company, companySimpleVO);

        return GraceJSONResult.ok(companySimpleVO);
    }
}
