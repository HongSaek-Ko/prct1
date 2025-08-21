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
import org.springframework.web.server.ResponseStatusException;



// 컨트롤러, 경로는 /answer로 시작
// 생성자 자동 생성
@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {
  private final QuestionService questionService;
  private final AnswerService answerService;
  private final MemberService memberService;

  // 답변 등록 요청
  // @Valid: (답변 작성 시)유효성 검사
  // 로그인된 경우만 가능
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/regist/{id}")
  public String registAnswer(
    Model model, 
    @PathVariable(value = "id") Integer id, 
    @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
      // 변수1에 질문 정보 바인딩
      Question question = questionService.getQuestion(id);

      // 변수2에 (접속 중인)사용자 정보 바인딩
      Member member = memberService.getMember(principal.getName());

      // 에러 시 답변 등록 템플릿(question_detail) (다시)렌더링
      if(bindingResult.hasErrors()) {
        model.addAttribute("question", question);
        return "question_content";
      }

      // 검사 통과 시 등록
      Answer answer = answerService.registAnswer(question, answerForm.getContent(), member);
      
      // 해당 질문의, 답변을 등록한 위치로 리다이렉트
      return String.format(
        "redirect:/question/detail/%s#answer_%s", 
        answer.getQuestion().getId(), answer.getId()
      );
  }

  // 답변 수정 폼 요청
  // 로그인된 경우만 가능
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/modify/{id}")
  public String answerModify(AnswerForm answerForm, @PathVariable(value = "id") Integer id, Principal principal) {
    // 변수1에 답변 정보 바인딩
    Answer answer = answerService.getAnswer(id);

    // 사용자 == 작성자일 때만 수정 가능
    if(!answer.getAuthor().getMembername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다");
    }

    // 수정 폼에 answer에 담은 내용 담기
    answerForm.setContent(answer.getContent());

    // 답변 수정 폼
    return "answer_form";
  }

  // 답변 수정 요청
  // 로그인된 경우만 가능
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/modify/{id}")
  public String answerModify(
    @Valid AnswerForm answerForm, 
    BindingResult bindingResult, 
    @PathVariable(value = "id") Integer id, 
    Principal principal
  ) 
  {
    // 요청 정보 중 에러 있다면 폼 리렌더링
    if(bindingResult.hasErrors()) {
      return "answer_form";
    }
    // 변수1에 기존 답변 정보 바인딩
    Answer answer = answerService.getAnswer(id);
    
    // 사용자 == 작성자만 수정 가능
    if(!answer.getAuthor().getMembername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
    }
    // 바인딩한 정보를 입력받은 정보로 수정
    answerService.modifyAnswer(answer, answerForm.getContent());

    // 수정 완료 시 해당 질문의 해당 답변으로 리다이렉트
    return String.format("redirect:/question/detail/%s#answer_%s", 
    answer.getQuestion().getId(), answer.getId());
  }
  
  // 답변 삭제 폼 요청
  // 로그인한 경우만 가능
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/delete/{id}")
  public String answerDelete(Principal principal, @PathVariable(value = "id") Integer id) {
    // 변수1에 답변 정보 바인딩
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
  
  // 답변 추천 요청
  // 로그인한 경우만 가능
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/recommend/{id}")
  public String recommendAnswer(Principal p, @PathVariable(value = "id") Integer id) {
    // 변수1에 답변 정보 바인딩
    Answer a = answerService.getAnswer(id);

    // 변수2에 사용자 정보 바인딩
    Member m = memberService.getMember(p.getName());

    // 답변, 사용자 정보로 추천 등록
    answerService.recommend(a, m);

    // 요청 완료 시 해당 질문의 답변 위치로 이동
    return String.format("redirect:/question/detail/%s#answer_%s", 
    a.getQuestion().getId(), a.getId());
  }
  
}
