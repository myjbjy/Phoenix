package com.myjbjy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myjbjy.pojo.Users;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author myj
 */
public interface UsersService extends IService<Users> {

    /**
     * 判断用户是否存在，如果存在则返回用户信息，否则null
     * @param mobile
     * @return
     */
    public Users queryMobileIsExist(String mobile);

    /**
     * 创建用户信息，并且返回用户对象
     * @param mobile
     * @return
     */
    public Users createUsers(String mobile);

    /**
     * 消息的一致性
     * @param mobile
     * @return
     */
    public Users createUsersAndInitResumeMQ(String mobile);

}
