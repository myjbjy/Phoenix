package com.myjbjy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author myj
 */
@RestController
@RequestMapping("a")
@Slf4j
public class AuthController {

    @GetMapping("hello")
    public Object hello(){
        return "Hello Auth Service";
    }
}
