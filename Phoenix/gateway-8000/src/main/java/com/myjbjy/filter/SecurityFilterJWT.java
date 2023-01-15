package com.myjbjy.filter;

import com.google.gson.Gson;
import com.myjbjy.base.BaseInfoProperties;
import com.myjbjy.grace.result.GraceJSONResult;
import com.myjbjy.grace.result.ResponseStatusEnum;
import com.myjbjy.utils.JWTUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author myj
 */
@Component
@Slf4j
public class SecurityFilterJWT extends BaseInfoProperties implements GlobalFilter, Ordered {

    public static final String HEADER_USER_TOKEN = "headerUserToken";

    @Resource
    private ExcludeUrlProperties excludeUrlProperties;

    @Resource
    private JWTUtils jwtUtils;

    /**
     * 路径匹配的规则器
     */
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1. 获取当前的请求路径
        String url = exchange.getRequest().getURI().getPath();

        // 2. 获得所有的需要排除校验的url list
        List<String> excludeList = excludeUrlProperties.getUrls();

        // 3. 校验并且排除excludeList
        if (excludeList != null && !excludeList.isEmpty()) {
            for (String excludeUrl : excludeList) {
                if (antPathMatcher.matchStart(excludeUrl, url)) {
                    // 如果匹配到，则直接放行，表示当前的请求url是不需要被拦截校验的
                    return chain.filter(exchange);
                }
            }
        }

        // 判断header中是否有token，对用户请求进行判断拦截
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String userToken = headers.getFirst(HEADER_USER_TOKEN);

        if (StringUtils.isNotBlank(userToken)){
            String[] tokenArr = userToken.split(JWTUtils.AT);
            if (tokenArr.length < 2){
                return renderErrorMsg(exchange,ResponseStatusEnum.UN_LOGIN);
            }
            //获得jwt的令牌与前缀
            String prefix = tokenArr[0];
            String jwt = tokenArr[1];

            // 判断并且处理用户信息
            if (prefix.equalsIgnoreCase(TOKEN_USER_PREFIX)) {
                return dealJWT(jwt, exchange, chain, APP_USER_JSON);
            } else if (prefix.equalsIgnoreCase(TOKEN_SAAS_PREFIX)) {
                return dealJWT(jwt, exchange, chain, SAAS_USER_JSON);
            } else if (prefix.equalsIgnoreCase(TOKEN_ADMIN_PREFIX)) {
                return dealJWT(jwt, exchange, chain, ADMIN_USER_JSON);
            }

//            return dealJWT(jwt, exchange, APP_USER_JSON, chain);
        }

        // 到达此处表示被拦截
        log.info("被拦截了。。。");

        // 不放行，token校验在jwt校验的自身代码逻辑中，到达此处表示都是漏掉的可能没有配置在excludeList
//        GraceException.display(ResponseStatusEnum.UN_LOGIN);
//        return chain.filter(exchange);
        return renderErrorMsg(exchange,ResponseStatusEnum.UN_LOGIN);
    }

    public Mono<Void> dealJWT(String jwt, ServerWebExchange exchange, GatewayFilterChain chain, String headerKey){
        try {
            // 重构header，将用户数据往后传递
            return chain.filter(setNewHeader(exchange,headerKey,jwtUtils.checkJWT(jwt)));
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            return renderErrorMsg(exchange,ResponseStatusEnum.JWT_EXPIRE_ERROR);
        }  catch (Exception e) {
            e.printStackTrace();
            return renderErrorMsg(exchange,ResponseStatusEnum.JWT_SIGNATURE_ERROR);
        }
    }

    public ServerWebExchange setNewHeader(ServerWebExchange exchange,
                                          String headerKey,
                                          String headerValue){
        // 重新构建新的request
        ServerHttpRequest newRequest = exchange.getRequest()
                .mutate()
                .header(headerKey, headerValue)
                .build();

        // 替换原来的request
        return exchange.mutate().request(newRequest).build();
    }

    /**
     * 重新包装并且返回错误信息
     * @param exchange
     * @param statusEnum
     * @return
     */
    public Mono<Void> renderErrorMsg(ServerWebExchange exchange,
                                     ResponseStatusEnum statusEnum) {
        // 1. 获得response
        ServerHttpResponse response = exchange.getResponse();
        // 2. 构建jsonResult
        GraceJSONResult jsonResult = GraceJSONResult.exception(statusEnum);
        // 3. 修改response的code为500
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        // 4. 设定header类型
        if (!response.getHeaders().containsKey("Content-Type")) {
            response.getHeaders().add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
        }
        // 5. 转换json并且向response中写入数据
        String resultJson = new Gson().toJson(jsonResult);
        DataBuffer dataBuffer = response.bufferFactory().wrap(resultJson.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(dataBuffer));
    }

    // 过滤器的顺序，数字越小优先级则越大
    @Override
    public int getOrder() {
        return 0;
    }
}
