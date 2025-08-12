package com.toypj1.prct1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// TODO: '질문 목록과 템플릿' 부터 시작
@Controller
public class HomeController {
  
  @GetMapping("/home")
  @ResponseBody
  public String home() {
    return "hello world!";
  }
}
