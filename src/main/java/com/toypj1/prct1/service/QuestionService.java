package com.toypj1.prct1.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.toypj1.prct1.DataNotFoundException;
import com.toypj1.prct1.domain.Answer;
import com.toypj1.prct1.domain.Member;
import com.toypj1.prct1.domain.Question;
import com.toypj1.prct1.repository.QuestionRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class QuestionService {
  private final QuestionRepository questionRepository;

  // 질문 목록
  public List<Question> getList() {
    return questionRepository.findAll();
  }

  // 질문 상세보기
  public Question getQuestion(Integer id) {
    log.info("question id: {}", id);
    return questionRepository.findById(id).
      orElseThrow(() -> new DataNotFoundException("질문 없음!"));
  }

  // 질문 등록 (제목, 내용, 작성자) + 작성일시
  public void registQuestion(String sbj, String cont, Member member) {
    Question q = new Question();
    q.setSubject(sbj); // 제목
    q.setContent(cont); // 내용
    q.setAuthor(member); // 작성자
    q.setCreateDate(LocalDateTime.now()); // 작성일시
    questionRepository.save(q);
  }

  // 질문 목록 (페이징처리, 검색어)
  public Page<Question> getList(int page, String kw) {
    List<Sort.Order> sorts = new ArrayList<>();
    // 변수 1에 작성일시 역순으로(= 최신순, 날짜값 높은순)으로 조회(목록)한 정보 바인딩
    sorts.add(Sort.Order.desc("createDate"));

    // 변수 2에 (조회할 페이지 번호, 한 페이지에서 보여줄 게시물 개수, 정렬 조건) 바인딩
    Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

    // 검색어(kw)로 메서드 'search' 시헹(하단 참조), spec에 저장
    // Specification<Question> spec = search(kw);

    // Q.R의 findAll로 검색 결과값, 페이징값 보냄
    // return questionRepository.findAll(spec, pageable);

    return questionRepository.findAllByKeyword(kw, pageable);
  }

  // 질문 수정 ((수정할)질문, 제목, 내용)
  public void modify(Question q, String sbj, String cont) {
    q.setSubject(sbj);
    q.setContent(cont);
    q.setModifyDate(LocalDateTime.now());
    questionRepository.save(q);
  }
  
  // 질문 삭제 ((삭제할)질문)
  public void delete(Question question) {
    questionRepository.delete(question);
  }

  // 질문 추천 ((추천할)질문, 사용자)
  public void recommend(Question question, Member member) {
    // 질문 엔티티의 '추천인' 컬럼에 {사용자} 받아서 추가
    question.getRecommender().add(member);
    questionRepository.save(question);
  }

  // (참고) 검색 (Specification 사용 버전)
  private Specification<Question> search(String kw) {
    return new Specification<>() {
      private static final long serialVersionUID = 1L;

      // javax.persistence.criteria.Predicate를 import
      @Override
      public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
        // 중복 제거
        query.distinct(true);
        Join<Question, Member> u1 = q.join("author", JoinType.LEFT);    // 질문 - 멤버(작성자) LEFT JOIN
        Join<Question, Answer> a = q.join("answerList", JoinType.LEFT); // 질문 - 답변(목록) LEFT JOIN
        Join<Answer, Member> u2 = a.join("author", JoinType.LEFT);      // 답변 - 멤버(작성자) LEFT JOIN
        // WHERE q.subject LIKE '%문자열%' OR q.LIKE '%문자열%' ...
        return
          cb.or(cb.like(q.get("subject"), "%" + kw + "%"),  // 질문 제목
          cb.like(q.get("content"), "%" + kw + "%"),        // 질문 내용
          cb.like(u1.get("membername"), "%" + kw + "%"),    // 질문 작성자
          cb.like(a.get("content"), "%" + kw + "%"),        // 답변 내용
          cb.like(u2.get("membername"), "%" + kw + "%"));   // 답변 작성자
      }
    };
  }
}
