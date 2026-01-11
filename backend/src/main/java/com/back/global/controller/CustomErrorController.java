package com.back.global.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {

    // Spring Boot가 처리하지 못한 에러나 404가 발생하면 "/error"로 들어옵니다.
    // 이때 index.html로 포워딩해주면 React 라우터가 에러 페이지를 렌더링합니다.
    @GetMapping("/error")
    public String handleError() {
        return "forward:/index.html";
    }
}