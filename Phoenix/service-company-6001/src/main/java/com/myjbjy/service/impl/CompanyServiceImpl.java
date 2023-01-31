package com.myjbjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.myjbjy.mapper.CompanyMapper;
import com.myjbjy.pojo.Company;
import com.myjbjy.service.CompanyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 企业表 服务实现类
 * </p>
 *
 * @author myj
 * @since 2023-01-31
 */
@Service
public class CompanyServiceImpl implements CompanyService {

    @Resource
    private CompanyMapper companyMapper;

    @Override
    public Company getByFullName(String fullName) {

        return companyMapper.selectOne(
                new QueryWrapper<Company>()
                        .eq("company_name", fullName)
        );
    }
}
