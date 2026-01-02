package com.back.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebController {

    // "/api"로 시작하지 않는 모든 경로를 index.html로 포워딩 (리액트 라우터에게 넘김)
    @GetMapping(value =  "/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }

    /**
     * 브라우저의 자동 파비콘 요청에 대해 500 에러 방지용 빈 응답 반환
     */
    @GetMapping("favicon.ico")
    @ResponseBody
    public void returnNoFavicon() {
        // 아무것도 반환하지 않음 (200 OK or Void)
    }
}