package com.myjbjy.service;

import com.myjbjy.pojo.Admin;
import com.myjbjy.pojo.bo.CreateAdminBO;
import com.myjbjy.utils.PagedGridResult;

/**
 * <p>
 * 运营管理系统的admin账户表，仅登录，不提供注册 服务类
 * </p>
 *
 * @author myj
 * @since 2022-09-04
 */
public interface AdminService {

    /**
     * 创建admin账号
     * @param createAdminBO
     */
    public void createAdmin(CreateAdminBO createAdminBO);

    /**
     * 查询admin列表
     * @param accountName
     * @param page
     * @param limit
     * @return
     */
    public PagedGridResult getAdminList(String accountName,
                                        Integer page,
                                        Integer limit);

    /**
     * 删除admin账号
     * @param username
     */
    public void deleteAdmin(String username);

    /**
     * 查询admin信息
     * @param adminId
     * @return
     */
    public Admin getById(String adminId);
}
