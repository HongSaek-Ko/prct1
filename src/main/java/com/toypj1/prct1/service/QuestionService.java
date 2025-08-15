package com.toypj1.prct1.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.toypj1.prct1.DataNotFoundException;
import com.toypj1.prct1.domain.Member;
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
  public void registQuestion(String sbj, String cont, Member member) {
    Question q = new Question();
    q.setSubject(sbj); // 제목
    q.setContent(cont); // 내용
    q.setAuthor(member); // 작성자
    q.setCreateDate(LocalDateTime.now()); // 작성일시
    questionRepository.save(q);
  }

  // 페이징처리
  public Page<Question> getList(int page) {
    // 작성일시 역순으로(= 최신순, 날짜값 높은순)으로 조회
    List<Sort.Order> sorts = new ArrayList<>();
    sorts.add(Sort.Order.desc("createDate"));

    // 조회할 페이지 번호, 한 페이지에서 보여줄 게시물 개수, 정렬 조건
    Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
    return questionRepository.findAll(pageable);
  }

  // 질문 수정
  public void modify(Question q, String sbj, String cont) {
    q.setSubject(sbj);
    q.setContent(cont);
    q.setModifyDate(LocalDateTime.now());
    questionRepository.save(q);
  }
  
  // 질문 삭제
  public void delete(Question question) {
    questionRepository.delete(question);
  }
}
