package com.back.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // "/api"로 시작하지 않는 모든 경로를 index.html로 포워딩 (리액트 라우터에게 넘김)
    @GetMapping(value =  "/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }
}