package com.myjbjy.controller;

import com.google.gson.Gson;
import com.myjbjy.api.task.SMSTask;
import com.myjbjy.base.BaseInfoProperties;
import com.myjbjy.grace.result.GraceJSONResult;
import com.myjbjy.grace.result.ResponseStatusEnum;
import com.myjbjy.pojo.Users;
import com.myjbjy.pojo.bo.RegisterLoginBO;
import com.myjbjy.pojo.vo.UsersVO;
import com.myjbjy.service.UsersService;
import com.myjbjy.utils.IPUtil;
import com.myjbjy.utils.JWTUtils;
import com.myjbjy.utils.SMSUtils;
import com.myjbjy.pojo.mq.SMSContentQO;
import com.myjbjy.api.mq.RabbitMQSMSConfig;
import com.myjbjy.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author myj
 */
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

    @Resource
    private SMSTask smsTask;

    @Resource
    private RabbitTemplate rabbitTemplate;

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
//        smsUtils.sendSMS(mobile, code);
//        smsTask.sendSMSTask();

        // 使用消息队列异步解耦发送短信
        SMSContentQO contentQO = new SMSContentQO();
        contentQO.setMobile(mobile);
        contentQO.setContent(code);

        rabbitTemplate.convertAndSend(RabbitMQSMSConfig.SMS_EXCHANGE,
                    RabbitMQSMSConfig.ROUTING_KEY_SMS_SEND_LOGIN,
                    GsonUtils.object2String(contentQO));
        log.info("验证码为：{}", code);

        // 把验证码存入到redis，用于后续的注册登录进行校验
        redis.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);

        return GraceJSONResult.ok();
    }

    @PostMapping("login")
    public GraceJSONResult login(@Valid @RequestBody RegisterLoginBO registerLoginBO) {
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
        return GraceJSONResult.ok();
    }
}
