package com.myjbjy.api.feign;

import com.myjbjy.grace.result.GraceJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author myj
 */
@FeignClient("work-service")    // 告诉springcloud容器，本接口为调用远程服务的service（声明式客户端远程调用）
public interface WorkMicroServiceFeign {
    @PostMapping("/resume/init")
    public GraceJSONResult init(@RequestParam("userId") String userId);
}
