package com.myjbjy.controller;

import com.google.gson.Gson;
import com.myjbjy.base.BaseInfoProperties;
import com.myjbjy.exceptions.GraceException;
import com.myjbjy.grace.result.GraceJSONResult;
import com.myjbjy.grace.result.ResponseStatusEnum;
import com.myjbjy.pojo.Users;
import com.myjbjy.pojo.bo.RegisterLoginBO;
import com.myjbjy.pojo.vo.UsersVO;
import com.myjbjy.service.UsersService;
import com.myjbjy.utils.IPUtil;
import com.myjbjy.utils.JWTUtils;
import com.myjbjy.utils.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("passport")
@Slf4j
public class PassportController extends BaseInfoProperties {

    @Resource
    private SMSUtils smsUtils;

    @Resource
    private JWTUtils jwtUtils;

    @Resource
    private UsersService usersService;

    @PostMapping("getSMSCode")
    public GraceJSONResult getSMSCode(String mobile,
                                      HttpServletRequest request) throws Exception {

        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.error();
        }

        // 获得用户ip
        String userIp = IPUtil.getRequestIp(request);
        // 限制用户只能在60s以内获得一次验证码
        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, mobile);

        String code = (int) ((Math.random() * 9 + 1) * 100000) + "";
        smsUtils.sendSMS(mobile, code);
        log.info("验证码为：{}", code);

        // 把验证码存入到redis，用于后续的注册登录进行校验
        redis.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);

        return GraceJSONResult.ok();
    }

    @PostMapping("login")
    public GraceJSONResult login(@Valid @RequestBody RegisterLoginBO registerLoginBO,
                                 HttpServletRequest request) throws Exception {
        String mobile = registerLoginBO.getMobile();
        String code = registerLoginBO.getSmsCode();

        // 1. 从redis中获得验证码进行校验判断是否匹配
        String redisCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(code)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        // 2. 根据mobile查询数据库，判断用户是否存在
        Users user = usersService.queryMobileIsExist(mobile);
        if (user == null) {
            // 2.1 如果查询的用户为空，则表示没有注册过，则需要注册信息入库
            user = usersService.createUsers(mobile);
        }

        // 3. 保存用户token，分布式会话到redis中
        String jwt = jwtUtils.createJWTWithPrefix(new Gson().toJson(user),
                (long) (60 * 1000),
                TOKEN_USER_PREFIX);

        // 4. 用户登录注册以后，删除redis中的短信验证码
        redis.del(MOBILE_SMSCODE + ":" + mobile);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserToken(jwt);

        // 5. 返回用户的信息给前端
        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("logout")
    public GraceJSONResult logout(@RequestParam String userId) {
        // 后端只需要清除用户的token信息即可，前端也需要清除相关的用户信息
        redis.del(REDIS_USER_TOKEN + ":" + userId);
        return GraceJSONResult.ok();
    }
}
