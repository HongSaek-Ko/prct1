package com.toypj1.prct1.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.toypj1.prct1.domain.SignUpForm;
import com.toypj1.prct1.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;




@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class MemberController {
  private final MemberService memberService;

  // 회원가입 화면
  @GetMapping("/signUp")
  public String signUp(SignUpForm signUpForm) {
      return "sign_up";
  }
  
  // 회원가입 요청
  @PostMapping("/signUp")
  public String signUp(@Valid SignUpForm signUpForm, BindingResult bindingResult) {
    // 유효성 검사 실패 시 회원가입창 리렌더링
    if(bindingResult.hasErrors()) {
      return "sign_up";
    }

    // 비밀번호 - 비밀번호 확인 불일치 시 안내문구 출력, 회원가입창 리렌더링
    if (!signUpForm.getPassword().equals(signUpForm.getPasswordCheck())) {
      bindingResult.rejectValue("pwck", "passwordIncorrect", "패스워드가 일치하지 않습니다.");
      return "sign_up";
    }

    try {
      // 둘 다 통과 시 (회원가입 후)홈화면으로 리다이렉트
      log.info("password??: {}", signUpForm.getPassword());
      memberService.create(signUpForm.getMembername(), signUpForm.getEmail(), signUpForm.getPassword());
      return "redirect:/";
      
      // 사용자 ID 혹은 이메일 주소가 이미 있으면(uk임) 회원가입 거부
    } catch (DataIntegrityViolationException e) {
      e.printStackTrace();
      bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");

      // 기타 오류는 해당 메시지 표시
    } catch (Exception e) {
      e.printStackTrace();
      bindingResult.reject("signupFailed", e.getMessage());
      return "sign_up";
    }
    // 회원가입 완료(요청 성공) 시 초기화면으로 리다이렉트
    return "redirect:/";
  }
  
  // 로그인 화면
  @GetMapping("/signIn")
  public String signIn() {
      return "sign_in";
  }
}
