package com.toypj1.prct1.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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

  // 작성일시
  private LocalDateTime createDate;

  // 글 작성자 >- 사용자
  @ManyToOne
  private Member author;

  // 수정일시
  private LocalDateTime modifyDate;

  // 추천인
  // Set: 중복을 허용하지 않는 자료형
  @ManyToMany
  Set<Member> recommender;
}
