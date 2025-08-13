package com.toypj1.prct1.controller;


import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.toypj1.prct1.domain.AnswerForm;
import com.toypj1.prct1.domain.Question;
import com.toypj1.prct1.domain.QuestionForm;
import com.toypj1.prct1.service.QuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequiredArgsConstructor // 생성자 자동 생성 (final 속성 포함)
@RequestMapping("/question") // 루트 요청 경로: 질문
public class QuestionController {

  private final QuestionService questionService;

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
  @GetMapping("/question/detail/{id}")
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
  @GetMapping("/regist")
  public String getQuestionForm(QuestionForm questionForm) {
      return "question_form";
  }
  
  // Post 요청: 질문 등록
  /* 
   * sbj, cont 항목의 form 전송 → QuestionForm의 subject, content 속성이 자동으로 binding
   * @Valid: QuestionFor의 @NotEmpty, @Size 등의 유효성 검사 기능이 동작
   * @B.R: @Vaild 어노테이션으로 검증이 수행된 결과를 의미하는 객체. 반드시 @Valid 매개변수 뒤에 있어야 함
  */
  @PostMapping("/regist")
  public String registQuestion(@Valid QuestionForm questionForm, BindingResult bindingResult) {
    // 오류(유효성 검사 실패) 발생 시 질문 등록 폼 (다시)렌더링
    if (bindingResult.hasErrors()) {
      return "question_form";
    }
    // 오류 발생 안했으면 그대로 등록.
    questionService.registQuestion(questionForm.getSubject(), questionForm.getContent());
      return "redirect:/question/list";
  }
  
  
}
