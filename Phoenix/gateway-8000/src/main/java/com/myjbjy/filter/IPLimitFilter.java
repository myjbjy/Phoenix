package com.myjbjy.filter;

import com.google.gson.Gson;
import com.myjbjy.base.BaseInfoProperties;
import com.myjbjy.grace.result.GraceJSONResult;
import com.myjbjy.grace.result.ResponseStatusEnum;
import com.myjbjy.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
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
public class IPLimitFilter extends BaseInfoProperties implements GlobalFilter, Ordered {

    @Resource
    private ExcludeUrlProperties excludeUrlProperties;

    // 路径匹配的规则器
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Value("${blackIP.continueCounts}")
    private Integer continueCounts;
    @Value("${blackIP.timeInterval}")
    private Integer timeInterval;
    @Value("${blackIP.limitTimes}")
    private Integer limitTimes;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1. 获取当前的请求路径
        String url = exchange.getRequest().getURI().getPath();

        // 2. 获得所有的需要进行ip限流校验的url list
        List<String> ipLimitList = excludeUrlProperties.getIpLimitUrls();

        // 3. 校验并且判断
        if (ipLimitList != null && !ipLimitList.isEmpty()) {
            for (String limitUrl : ipLimitList) {
                if (antPathMatcher.matchStart(limitUrl, url)) {
                    // 如果匹配到，则表明需要进行ip的拦截校验
                    log.info("IPLimitFilter - 拦截到需要进行ip限流校验的方法：URL = " + url);
                    return doLimit(exchange, chain);
                }
            }
        }

        // 4. 默认直接放行
        return chain.filter(exchange);
    }

    public Mono<Void> doLimit(ServerWebExchange exchange,
                              GatewayFilterChain chain) {
        // 根据request获得请求ip
        String ip = IPUtil.getIP(exchange.getRequest());

        /**
         * 需求：
         * 判断ip在20秒内请求的次数是否超过3次
         * 如果超过，则限制访问30秒
         * 等待30秒静默以后，才能够回复访问
         */
        // 正常的ip
        final String ipRedisKey = "gateway-ip:" + ip;
        // 被拦截的黑名单，如果存在，则表示目前被关小黑屋
        final String ipRedisLimitedKey = "gateway-ip:limit:" + ip;

        // 获得当前ip，查询还剩下多少的小黑屋时间
        long limitLeftTimes = redis.ttl(ipRedisLimitedKey);
        if (limitLeftTimes > 0) {
            return renderErrorMsg(exchange,
                    ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        // 在redis中获得ip的累加次数
        long requestCounts = redis.increment(ipRedisKey, 1);
        // 判断如果是第一次进来，也就是0开始的，则初期访问就是1，需要设置间隔的时间，也就是连续请求的间隔时间
        if (requestCounts == 1) {
            redis.expire(ipRedisKey, timeInterval);
        }

        // 如果还能取得请求的次数，说明用户的连续请求落在限定的[timeInterval秒]之内
        // 一旦请求次数超过限定的连续访问[continueCounts]次数，则需要限制当前ip
        if (requestCounts > continueCounts) {
            // 限制ip访问的时间[limitTimes]
            redis.set(ipRedisLimitedKey, ipRedisLimitedKey, limitTimes);
            // 终止请求，返回错误
            return renderErrorMsg(exchange,
                    ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        return chain.filter(exchange);
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
        DataBuffer dataBuffer = response
                .bufferFactory()
                .wrap(resultJson.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
