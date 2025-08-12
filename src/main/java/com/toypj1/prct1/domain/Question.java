package com.toypj1.prct1.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Question {
  // 게시글 ID(PK)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  // 제목, 길이 200(varchar)
  @Column(length = 200)
  private String subject;

  // 내용, 글자수 제한 없음
  @Column(columnDefinition = "TEXT")
  private String content;

  // 댓글(답변), 목록(= 일대다: Answer 테이블의 question 컬럼, '일'쪽 삭제 시 '다'쪽의 해당 컬럼도 삭제)
  @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
  private List<Answer> answerList;

  private LocalDateTime createDate;
}
