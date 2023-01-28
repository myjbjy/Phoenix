package com.myjbjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myjbjy.api.feign.WorkMicroServiceFeign;
import com.myjbjy.enums.Sex;
import com.myjbjy.enums.ShowWhichName;
import com.myjbjy.exceptions.GraceException;
import com.myjbjy.grace.result.GraceJSONResult;
import com.myjbjy.grace.result.ResponseStatusEnum;
import com.myjbjy.mapper.UsersMapper;
import com.myjbjy.pojo.Users;
import com.myjbjy.service.UsersService;
import com.myjbjy.utils.DesensitizationUtil;
import com.myjbjy.utils.LocalDateUtils;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.tm.api.GlobalTransactionContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

    @Resource
    private UsersMapper usersMapper;

    @Resource
    private WorkMicroServiceFeign workMicroServiceFeign;

    private static final String USER_FACE1 = "http://122.152.205.72:88/group1/M00/00/05/CpoxxF6ZUySASMbOAABBAXhjY0Y649.png";

    @Override
    public Users queryMobileIsExist(String mobile) {

        return usersMapper.selectOne(new QueryWrapper<Users>()
                .eq("mobile", mobile));
    }

    @Resource
    public RabbitTemplate rabbitTemplate;

    @Transactional(rollbackForClassName = {"Users"})
    @Override
    public Users createUsersAndInitResumeMQ(String mobile) {

        // 创建用户
        return createUsers(mobile);
    }

    //    @Transactional
    @GlobalTransactional
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

        // 发起远程调用，初始化用户简历，新增一条空记录
        GraceJSONResult graceJSONResult = workMicroServiceFeign.init(user.getId());
        if (graceJSONResult.getStatus() != 200) {
            // 如果调用状态不是200，则手动回滚全局事务
            String xid = RootContext.getXID();
            if (StringUtils.isNotBlank(xid)) {
                try {
                    GlobalTransactionContext.reload(xid).rollback();
                } catch (TransactionException e) {
                    e.printStackTrace();
                } finally {
                    GraceException.display(ResponseStatusEnum.USER_REGISTER_ERROR);
                }
            }
        }

        // 模拟除零异常
//        int a = 1 / 0;

        return user;
    }
}
