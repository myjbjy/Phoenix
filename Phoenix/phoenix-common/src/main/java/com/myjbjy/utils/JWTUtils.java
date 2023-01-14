package com.myjbjy.utils;

import com.myjbjy.exceptions.GraceException;
import com.myjbjy.grace.result.ResponseStatusEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * @author myj
 */
@Component
@Slf4j
public class JWTUtils {

    public static final String AT = "@";

    @Resource
    private JWTProperties jwtProperties;

    public String createJWTWithPrefix(String body, Long expireTimes, String prefix) {
        if (expireTimes == null) {
            GraceException.display(ResponseStatusEnum.SYSTEM_NO_EXPIRE_ERROR);
        }
        return prefix + AT + createJWT(body, expireTimes);
    }

    public String createJWTWithPrefix(String body, String prefix) {
        return prefix + AT + createJWT(body);
    }

    public String createJWT(String body) {
        return dealJWT(body, null);
    }

    public String createJWT(String body, Long expireTimes) {
        if (expireTimes == null) {
            GraceException.display(ResponseStatusEnum.SYSTEM_NO_EXPIRE_ERROR);
        }
        return dealJWT(body, expireTimes);
    }

    public String dealJWT(String body, Long expireTimes) {

        String userKey = jwtProperties.getKey();

        // 1. 对秘钥进行base64编码
        String base64 = new BASE64Encoder().encode(userKey.getBytes());

        // 2. 对base64生成一个秘钥的对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());

//        log.info("JWTUtils - dealJWT: generatorJWT = " + jwt);
        return expireTimes == null ? generatorJWT(body, secretKey) : generatorJWT(body, expireTimes, secretKey);
    }

    public String generatorJWT(String body, SecretKey secretKey) {
        return Jwts.builder()
                .setSubject(body)
                .signWith(secretKey)
                .compact();
    }

    public String generatorJWT(String body, Long expireTimes, SecretKey secretKey) {
        // 定义过期时间
        Date expireDate = new Date(System.currentTimeMillis() + expireTimes);
        return Jwts.builder()
                .setSubject(body)
                .signWith(secretKey)
                .setExpiration(expireDate)
                .compact();
    }
}
