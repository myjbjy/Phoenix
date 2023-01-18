package com.myjbjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myjbjy.mapper.AdminMapper;
import com.myjbjy.pojo.Admin;
import com.myjbjy.pojo.bo.AdminBO;
import com.myjbjy.service.AdminService;
import com.myjbjy.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 运营管理系统的admin账户表，仅登录，不提供注册 服务实现类
 * </p>
 *
 * @author myj
 * @since 2022-09-04
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Override
    public boolean adminLogin(AdminBO adminBO) {

        // 根据用户名获得盐slat
        Admin admin = getSelfAdmin(adminBO.getUsername());

        // 判断admin是否为空来返回true或false
        if (admin == null) {
            return false;
        } else {
            String slat = admin.getSlat();
            String md5Str = MD5Utils.encrypt(adminBO.getPassword(), slat);
            return md5Str.equalsIgnoreCase(admin.getPassword());
        }
    }

    @Override
    public Admin getAdminInfo(AdminBO adminBO) {
        return this.getSelfAdmin(adminBO.getUsername());
    }

    private Admin getSelfAdmin(String username) {
        return adminMapper.selectOne(
                new QueryWrapper<Admin>()
                        .eq("username", username)
        );
    }
}
