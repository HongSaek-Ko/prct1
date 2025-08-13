package com.toypj1.prct1.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.toypj1.prct1.DataNotFoundException;
import com.toypj1.prct1.domain.Question;
import com.toypj1.prct1.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QuestionService {
  private final QuestionRepository questionRepository;

  // 질문 목록
  public List<Question> getList() {
    return questionRepository.findAll();
  }

  // 질문 상세보기
  public Question getQuestion(Integer id) {
    Optional<Question> question = questionRepository.findById(id);
    if(question.isPresent()) {
      return question.get();
    } else { // 질문글 id가 없으면 저기로 던지고 아래 문자열 출력
      throw new DataNotFoundException("질문이 없는데요?");
    }
  }

  // 질문 등록
  public void registQuestion(String sbj, String cont) {
    Question q = new Question();
    q.setSubject(sbj);
    q.setContent(cont);
    q.setCreateDate(LocalDateTime.now());
    questionRepository.save(q);
  }

  // 페이징처리. (조회할 페이지 번호, 한 페이지에 보여줄 게시물 개수)
  public Page<Question> getList(int page) {
    Pageable pageable = PageRequest.of(page, 10);
    return questionRepository.findAll(pageable);
  }
}
