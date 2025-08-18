package com.toypj1.prct1.domain;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Answer {
    // 답변 id (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 답변 내용
    @Column(columnDefinition = "TEXT")
    private String content;

    // 답변 작성일시
    private LocalDateTime createDate;

    // 답변 >- 질문
    @ManyToOne
    private Question question;

    // 답변 작성자 >- 사용자
    @ManyToOne
    private Member author;

    // 수정일시
    private LocalDateTime modifyDate;

    // 추천인
    // Set: 중복을 허용하지 않는 자료형
    @ManyToMany
    Set<Member> recommender;
}
