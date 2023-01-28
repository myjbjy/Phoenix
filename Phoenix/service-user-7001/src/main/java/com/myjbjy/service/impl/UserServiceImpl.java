package com.myjbjy.service.impl;

import com.myjbjy.base.BaseInfoProperties;
import com.myjbjy.exceptions.GraceException;
import com.myjbjy.grace.result.ResponseStatusEnum;
import com.myjbjy.mapper.UsersMapper;
import com.myjbjy.pojo.Users;
import com.myjbjy.pojo.bo.ModifyUserBO;
import com.myjbjy.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author myj
 */
@Service
public class UserServiceImpl extends BaseInfoProperties implements UserService {

    @Resource
    private UsersMapper usersMapper;

    @Transactional
    @Override
    public void modifyUserInfo(ModifyUserBO userBO) {

        String userId = userBO.getUserId();
        if (StringUtils.isBlank(userId)) {
            GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_ERROR);
        }

        Users pendingUser = new Users();
        pendingUser.setId(userId);
        pendingUser.setUpdatedTime(LocalDateTime.now());

        BeanUtils.copyProperties(userBO, pendingUser);

        usersMapper.updateById(pendingUser);
    }

    @Override
    public Users getById(String uid) {
        return usersMapper.selectById(uid);
    }
}
