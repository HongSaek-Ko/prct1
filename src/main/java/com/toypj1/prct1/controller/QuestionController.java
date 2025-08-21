package com.toypj1.prct1.controller;


import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.toypj1.prct1.domain.AnswerForm;
import com.toypj1.prct1.domain.Member;
import com.toypj1.prct1.domain.Question;
import com.toypj1.prct1.domain.QuestionForm;
import com.toypj1.prct1.service.MemberService;
import com.toypj1.prct1.service.QuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;


// 컨트롤러, 요청 경로 "/question"으로 시작
// 생성자 자동 생성
@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {

  private final QuestionService questionService;
  private final MemberService memberService;

  // 질문 목록 요청
  /* 
    1. Model: Class - template 간 연결고리 역할; 
    Model 객체에 값을 '담아두면'(addAttribute), 
    template에서 그 값을 사용할 수 있음 
    
    2. Page: 페이지 파라미터(URL) 전달. 기본값 0.

    3. kw: 검색값 전달. 기본값 ""(빈 문자열)
  */
  @GetMapping("/list")
  public String getQuestionList(
    Model model, 
    @RequestParam(value = "page", defaultValue = "0") int page,
    @RequestParam(value = "kw", defaultValue = "") String kw)
  {
    // 변수1에 질문(목록) 요청
    Page<Question> questionList = questionService.getList(page, kw);
    model.addAttribute("questionList", questionList);
    model.addAttribute("kw", kw);
    return "question_list";
  }

  // 질문 및 답변 내용 요청
  @GetMapping("/detail/{id}")
  public String getDetail(Model model, @PathVariable(value = "id") Integer id, AnswerForm answerForm) {
    // 변수1에 질문 정보 바인딩
    Question question = questionService.getQuestion(id);
    // 모델 객체에 질문 정보 추가
    model.addAttribute("question", question);
    return "question_content";
  }

  // 질문 등록 폼 요청, 로그인 상태만 가능
  // 객체명이 th:object와 서로 일치해야 함
  @PreAuthorize("isAuthenticated()") // '로그인이 필요한 메서드임' -> 로그아웃 상태에서 호출 시 로그인 페이지로 이동
  @GetMapping("/regist")
  public String getQuestionForm(QuestionForm questionForm) {
      return "question_form";
  }
  
  // 질문 등록 요청, 로그인 상태만 가능
  /* 
   * sbj, cont 항목의 form 전송 → QuestionForm의 subject, content 속성이 자동으로 binding
   * @Valid: QuestionFor의 @NotEmpty, @Size 등의 유효성 검사 기능이 동작
   * B.R: @Vaild 어노테이션으로 검증이 수행된 결과를 의미하는 객체. 반드시 @Valid 매개변수 뒤에 있어야 함
  */
  @PreAuthorize("isAuthenticated()") // '로그인이 필요한 메서드임' -> 로그아웃 상태에서 호출 시 로그인 페이지로 이동
  @PostMapping("/regist")
  public String registQuestion(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
    // 오류(유효성 검사 실패) 발생 시 질문 등록 폼 (다시)렌더링 (true/false)
    if (bindingResult.hasErrors()) {
      return "question_form";
    }

    // 변수 1에 사용자 정보 바인딩
    Member member = memberService.getMember(principal.getName());
    
    // 질문 및 사용자(작성자) 정보 등록
    questionService.registQuestion(questionForm.getSubject(), questionForm.getContent(), member);
      return "redirect:/question/list";
  }
  
  // 질문 수정 폼 요청, 로그인 상태만 가능
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/modify/{id}")
  public String queStringModify(QuestionForm questionForm, @PathVariable(value = "id") Integer id, Principal principal) {

    // 변수 1에 질문 정보 바인딩
    Question question = questionService.getQuestion(id);

    // (사용자 == 작성자)인 경우에만 수정 가능
    if(!question.getAuthor().getMembername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
    }
    // 바인딩한 정보로 수정 폼 설정
    questionForm.setSubject(question.getSubject());
    questionForm.setContent(question.getContent());
    return "question_form";
  }

  // 질문 수정 요청, 로그인한 경우만 가능
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/modify/{id}")
  public String questionModify(
    @Valid QuestionForm questionForm, BindingResult bindingResult, 
    Principal principal, @PathVariable("id") Integer id) {
    // 유효성 검사 실패 시 폼 재요청
    if(bindingResult.hasErrors()) {
      return "question_form";
    }
    // 변수 1에 질문 정보 바인딩
    Question question = questionService.getQuestion(id);

    // (사용자 == 작성자)인 경우에만 수정 가능
    if(!question.getAuthor().getMembername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
    }

    // 변수를 입력받은 정보로 수정
    questionService.modify(question, questionForm.getSubject(), questionForm.getContent());

    // 요청 완료 시 질문 상세 화면 리다이렉트
    return String.format("redirect:/question/detail/%s", id);
  }
  
  // 질문 삭제 요청, 로그인한 상태만 가능
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/delete/{id}")
  public String questionDelete(Principal principal, @PathVariable(value = "id") Integer id) {
    // 변수 1에 질문 정보 바인딩
    Question question = questionService.getQuestion(id);

    // 사용자 == 작성자만 수정 가능
    if(!question.getAuthor().getMembername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
    }
    
    // 해당 정보 삭제
    questionService.delete(question);

    // 요청 처리 후 루트 경로로 리다이렉트
    return "redirect:/";
  }

  // 질문 추천 요청
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/recommend/{id}")
  public String recommendQuestion(Principal p, @PathVariable(value = "id") Integer id) {
    // 변수 1에 질문 정보 바인딩
    Question q = questionService.getQuestion(id);

    // 변수 2에 사용자 정보 바인딩
    Member m = memberService.getMember(p.getName());

    // (추천할)질문 정보, (추천한)사용자 정보 넘기기
    questionService.recommend(q, m);

    // 요청 완료 시 해당 글로 리다이렉트
    return String.format("redirect:/question/detail/%s", id);
  }
  
}
