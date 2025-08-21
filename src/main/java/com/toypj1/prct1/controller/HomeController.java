package com.toypj1.prct1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
  
  @GetMapping("/home")
  @ResponseBody
  public String home() {
    return "여기는 질답 사이트에용.";
  }

  // localhost:{포트번호} 외 아무것도 입력 안하면 리다이렉트할 경로 설정
  @GetMapping("/")
  public String root() {
    return "redirect:/question/list";
  }

}
