package com.myjbjy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myjbjy.pojo.Admin;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 运营管理系统的admin账户表，仅登录，不提供注册 Mapper 接口
 * </p>
 *
 * @author myj
 * @since 2022-09-04
 */
@Repository
public interface AdminMapper extends BaseMapper<Admin> {

}
