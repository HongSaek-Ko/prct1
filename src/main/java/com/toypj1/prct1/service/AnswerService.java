package com.toypj1.prct1.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.toypj1.prct1.domain.Answer;
import com.toypj1.prct1.domain.Question;
import com.toypj1.prct1.repository.AnswerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerService {
  private final AnswerRepository answerRepository;

  public void registAnswer(Question question, String content) {
    Answer answer = new Answer();
    answer.setContent(content);
    answer.setCreateDate(LocalDateTime.now());
    answer.setQuestion(question);
    answerRepository.save(answer);
  }
}
