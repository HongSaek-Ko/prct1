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
import org.springframework.web.bind.annotation.RequestBody;




@Controller
@RequiredArgsConstructor // 생성자 자동 생성 (final 속성 포함)
@RequestMapping("/question") // 루트 요청 경로: 질문
public class QuestionController {

  private final QuestionService questionService;
  private final MemberService memberService;

  // Get 요청: 질문 목록
  /* 
    1. Model: Class - template 간 연결고리 역할; 
    Model 객체에 값을 '담아두면'(addAttribute), 
    template에서 그 값을 사용할 수 있음 
    
    2. Page: 페이지 파라미터(URL) 전달. 기본값 0.
  */
  @GetMapping("/list")
  public String getQuestionList(
    Model model, 
    @RequestParam(value = "page", defaultValue = "0") int page
  ) {
    Page<Question> questionList = questionService.getList(page);
    model.addAttribute("questionList", questionList);
    return "question_list";
  }

  // Get 요청: 질문 내용
  // {경로변수}의 변수명은 일치해야 함
  @GetMapping("/detail/{id}")
  public String getDetail(
    Model model, 
    @PathVariable(value = "id") Integer id, 
    AnswerForm answerForm
  ) {
    Question question = questionService.getQuestion(id);
    model.addAttribute("question", question);
    return "question_content";
  }

  // Get 요청: 질문 등록(폼)
  // th:object에 의해 QuestionForm 객체를 불러옴
  @PreAuthorize("isAuthenticated()") // '로그인이 필요한 메서드임' -> 로그아웃 상태에서 호출 시 로그인 페이지로 이동
  @GetMapping("/regist")
  public String getQuestionForm(QuestionForm questionForm) {
      return "question_form";
  }
  
  // Post 요청: 질문 등록
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
    // 오류 발생 안했으면 그대로 등록.
    Member member = memberService.getMember(principal.getName());
    questionService.registQuestion(questionForm.getSubject(), questionForm.getContent(), member);
      return "redirect:/question/list";
  }
  
  // Get 요청: 질문 수정 폼
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/modify/{id}")
  public String queStringModify(QuestionForm questionForm, @PathVariable(value = "id") Integer id, Principal principal) {
    // 1. id로 질문 가져오고...
    Question question = questionService.getQuestion(id);
    // * 작성자와 접속한 사용자가 동일한 경우에만 수정 가능. 다를 경우 아래 예외 던짐
    if(!question.getAuthor().getMembername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
    }
    // 2. 1.에서 가져온 데이터로 제목, 내용 설정
    questionForm.setSubject(question.getSubject());
    questionForm.setContent(question.getContent());
    return "question_form";
  }

  // Post 요청: 질문 수정
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/modify/{id}")
  public String questionModify(
    @Valid QuestionForm questionForm, 
    BindingResult bindingResult, 
    Principal principal, 
    @PathVariable("id") Integer id
  ) 
  {
    // 유효성 검사 실패 시 폼 재요청
    if(bindingResult.hasErrors()) {
      return "question_form";
    }
    // id로 질문 찾고 question에 저장
    Question question = questionService.getQuestion(id);

    // 사용자와 작성자가 동일할 때만 수정 가능
    if(!question.getAuthor().getMembername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
    }

    // 수정: question 객체를, 제목 및 내용을 수정
    questionService.modify(question, questionForm.getSubject(), questionForm.getContent());

    // 수정 완료 시 질문 상세 화면 리다이렉트
    return String.format("redirect:/question/detail/%s", id);
  }
  
  // Get 요청: 질문 삭제
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/delete/{id}")
  public String questionDelete(Principal principal, @PathVariable(value = "id") Integer id) {
    // id로 질문 찾고 question에 저장
    Question question = questionService.getQuestion(id);

    // 사용자 == 작성자만 수정 가능
    if(!question.getAuthor().getMembername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
    }
    
    // question 객체 삭제
    questionService.delete(question);

    // 요청 처리 후 루트 경로로 리다이렉트
    return "redirect:/";
  }

  // Get: 질문 추천
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/recommend/{id}")
  public String recommendQuestion(Principal p, @PathVariable(value = "id") Integer id) {
    // 글 id로 질문 가져오기
    Question q = questionService.getQuestion(id);
    // principal 객체의 name으로 사용자 정보 가져오기
    Member m = memberService.getMember(p.getName());
    // (추천할)글, (추천한)사용자 정보 넘기기
    questionService.recommend(q, m);
    // 요청 완료 시 해당 글로 리다이렉트
    return String.format("redirect:/question/detail/%s", id);
  }
  
}
