package com.myjbjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myjbjy.enums.Sex;
import com.myjbjy.enums.ShowWhichName;
import com.myjbjy.mapper.UsersMapper;
import com.myjbjy.pojo.Users;
import com.myjbjy.service.UsersService;
import com.myjbjy.utils.DesensitizationUtil;
import com.myjbjy.utils.LocalDateUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author myj
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    @Autowired
    private UsersMapper usersMapper;

    private static final String USER_FACE1 = "http://122.152.205.72:88/group1/M00/00/05/CpoxxF6ZUySASMbOAABBAXhjY0Y649.png";

    @Override
    public Users queryMobileIsExist(String mobile) {

        return usersMapper.selectOne(new QueryWrapper<Users>()
                .eq("mobile", mobile));
    }

    @Autowired
    public RabbitTemplate rabbitTemplate;

    @Transactional(rollbackForClassName = {"Users"})
    @Override
    public Users createUsersAndInitResumeMQ(String mobile) {

        // 创建用户
        return createUsers(mobile);
    }

    @Transactional
//    @GlobalTransactional
    @Override
    public Users createUsers(String mobile) {

        Users user = new Users();

        user.setMobile(mobile);
        user.setNickname("用户" + DesensitizationUtil.commonDisplay(mobile));
        user.setRealName("用户" + DesensitizationUtil.commonDisplay(mobile));
        user.setShowWhichName(ShowWhichName.nickname.type);

        user.setSex(Sex.secret.type);
        user.setFace(USER_FACE1);
        user.setEmail("");

        LocalDate birthday = LocalDateUtils
                .parseLocalDate("1980-01-01",
                        LocalDateUtils.DATE_PATTERN);
        user.setBirthday(birthday);

        user.setCountry("中国");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");
        user.setDescription("这家伙很懒，什么都没留下~");

        // 我参加工作的日期，默认使用注册当天的日期
        user.setStartWorkDate(LocalDate.now());
        user.setPosition("底层码农");
        user.setHrInWhichCompanyId("");

        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        usersMapper.insert(user);

        return user;
    }
}
