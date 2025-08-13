package com.toypj1.prct1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.toypj1.prct1.domain.AnswerForm;
import com.toypj1.prct1.domain.Question;
import com.toypj1.prct1.service.AnswerService;
import com.toypj1.prct1.service.QuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {
  private final QuestionService questionService;
  private final AnswerService answerService;

  // POST 요청: 답변 등록
  // @Valid: (답변 작성 시)유효성 검사
  @PostMapping("/regist/{id}")
  public String registAnswer(
    Model model, 
    @PathVariable(value = "id") Integer id, 
    @Valid AnswerForm answerForm, BindingResult bindingResult) {
      Question question = questionService.getQuestion(id);

      // 에러 시 답변 등록 템플릿(question_detail) (다시)렌더링
      if(bindingResult.hasErrors()) {
        model.addAttribute("question", question);
        return "question_content";
      }

      // 검사 통과 시 등록 진행, 질문 상세보기로 리다리렉트
      answerService.registAnswer(question, answerForm.getContent());
      return String.format("redirect:/question/detail/%s",id);
  }

  
}
