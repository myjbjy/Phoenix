package com.myjbjy.utils;

import com.myjbjy.exceptions.GraceException;
import com.myjbjy.grace.result.ResponseStatusEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * @author myj
 */
@Component
@Slf4j
@RefreshScope
public class JWTUtils {

    public static final String AT = "@";

    @Value("${jwt.key}")
    public String JWT_KEY;

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

        String userKey = JWT_KEY;

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

    public String checkJWT(String pendingJWT) {

        String userKey = JWT_KEY;

        // 1. 对秘钥进行base64编码
        String base64 = new BASE64Encoder().encode(userKey.getBytes());

        // 2. 对base64生成一个秘钥的对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());

        // 3. 校验jwt
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();       // 构造解析器
        // 解析成功，可以获得Claims，从而去get相关的数据，如果此处抛出异常，则说明解析不通过，也就是token失效或者被篡改
        Jws<Claims> jws = jwtParser.parseClaimsJws(pendingJWT);      // 解析jwt

        return jws.getBody().getSubject();
    }
}
