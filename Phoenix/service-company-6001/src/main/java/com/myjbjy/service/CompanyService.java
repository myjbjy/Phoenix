package com.myjbjy.service;

import com.myjbjy.pojo.Company;

/**
 * <p>
 * 企业表 服务类
 * </p>
 *
 * @author myj
 * @since 2023-01-31
 */
public interface CompanyService {

    public Company getByFullName(String fullName);
}
