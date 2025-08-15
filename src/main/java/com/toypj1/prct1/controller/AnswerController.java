package com.toypj1.prct1.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.toypj1.prct1.domain.Answer;
import com.toypj1.prct1.domain.AnswerForm;
import com.toypj1.prct1.domain.Member;
import com.toypj1.prct1.domain.Question;
import com.toypj1.prct1.service.AnswerService;
import com.toypj1.prct1.service.MemberService;
import com.toypj1.prct1.service.QuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestBody;




@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {
  private final QuestionService questionService;
  private final AnswerService answerService;
  private final MemberService memberService;

  // POST 요청: 답변 등록
  // @Valid: (답변 작성 시)유효성 검사
  // Principal: S.S에서 제공하는 '사용자 정보' 객체.
  @PostMapping("/regist/{id}")
  @PreAuthorize("isAuthenticated()") // '로그인이 필요한 메서드임' -> 로그아웃 상태에서 호출 시 로그인 페이지로 이동
  public String registAnswer(
    Model model, 
    @PathVariable(value = "id") Integer id, 
    @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
      Question question = questionService.getQuestion(id);
      Member member = memberService.getMember(principal.getName());

      // 에러 시 답변 등록 템플릿(question_detail) (다시)렌더링
      if(bindingResult.hasErrors()) {
        model.addAttribute("question", question);
        return "question_content";
      }

      // 검사 통과 시 등록 진행, 질문 상세보기로 리다리렉트
      answerService.registAnswer(question, answerForm.getContent(), member);
      return String.format("redirect:/question/detail/%s",id);
  }

  // Get 요청: 답변 수정 폼
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/modify/{id}")
  public String answerModify(AnswerForm answerForm, @PathVariable(value = "id") Integer id, Principal principal) {
    // answer에 PK로 찾아서 담기
    Answer answer = answerService.getAnswer(id);

    // 사용자 == 작성자만 수정 가능
    if(!answer.getAuthor().getMembername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다");
    }

    // 수정 폼에 answer에 담은 내용 담기
    answerForm.setContent(answer.getContent());

    // 답변 수정 폼
    return "answer_form";
  }

  // Post 요청: 답변 수정
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/modify/{id}")
  public String answerModify(
    @Valid AnswerForm answerForm, 
    BindingResult bindingResult, 
    @PathVariable(value = "id") Integer id, 
    Principal principal
  ) 
  {
    // 에러 있으면 리렌더링
    if(bindingResult.hasErrors()) {
      return "answer_form";
    }
    // answer에 id로 찾아서 담음
    Answer answer = answerService.getAnswer(id);
    // 사용자 == 작성자만 수정 가능
    if(!answer.getAuthor().getMembername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
    }
    // answer에 담아둔 거로 content 수정
    answerService.modifyAnswer(answer, answerForm.getContent());
    // 수정 완료 시 해당 질문으로 리다이렉트
    return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
  }
  
  // Get: 답변 삭제 폼
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/delete/{id}")
  public String answerDelete(Principal principal, @PathVariable(value = "id") Integer id) {
    // id로 답변 찾아서 answer에 담기
    Answer answer = answerService.getAnswer(id);

    // 사용자 == 작성자만 삭제 가능
    if(!answer.getAuthor().getMembername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
    }
    
    // 답변 삭제 처리
    answerService.deleteAnswer(answer);
    
    // 삭제 후 해당 게시글(질문)으로 리다이렉트
    return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
  }
  
  
  
}
