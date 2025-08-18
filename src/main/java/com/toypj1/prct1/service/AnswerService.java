package com.toypj1.prct1.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.toypj1.prct1.DataNotFoundException;
import com.toypj1.prct1.domain.Answer;
import com.toypj1.prct1.domain.Member;
import com.toypj1.prct1.domain.Question;
import com.toypj1.prct1.repository.AnswerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerService {
  private final AnswerRepository answerRepository;

  // 답변 등록
  public Answer registAnswer(Question question, String content, Member author) {
    Answer answer = new Answer();
    answer.setContent(content); // 내용
    answer.setCreateDate(LocalDateTime.now()); // 작성일시
    answer.setQuestion(question); // 원본글(질문)
    answer.setAuthor(author); // 작성자(답변)
    answerRepository.save(answer);
    // 답변 등록, 수정, 추천 후 리다이렉트 시 '답변 등록 위치로 이동'을 위한 답변 객체 리턴
    return answer;
  }

  // 답변 조회
  public Answer getAnswer(Integer id) {
    Optional<Answer> answer = answerRepository.findById(id);
    if(answer.isPresent()) {
      return answer.get();
    } else {
      throw new DataNotFoundException("answer not found");
    }
  }

  // 답변 수정
  public void modifyAnswer(Answer answer, String content) {
    answer.setContent(content);
    answer.setModifyDate(LocalDateTime.now());
    answerRepository.save(answer);
  }
  
  // 답변 삭제
  public void deleteAnswer(Answer answer) {
    answerRepository.delete(answer);
  }

  // 답변 추천
  public void recommend(Answer a, Member m) {
    a.getRecommender().add(m);
    answerRepository.save(a);
  }
}
