package com.toypj1.prct1.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.toypj1.prct1.domain.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
  Question findBySubject(String subject);
  Question findBySubjectAndContent(String sbj, String cont);
  List<Question> findBySubjectLike(String spSbj);
  Page<Question> findAll(Pageable pageable);
  Page<Question> findAll(Specification<Question> spec, Pageable pageable);

    // JPQL 쿼리문. 이미 @ManyToOne 등으로 ON 조건이 걸려있으므로 엔티티 관계 기반 JOIN 해야 함
    @Query(
      "SELECT DISTINCT q FROM Question q "
    + "LEFT OUTER JOIN q.author m1 "
    + "LEFT OUTER JOIN q.answerList a "
    + "LEFT OUTER JOIN a.author m2 "
    + "WHERE "
      + "q.subject LIKE %:kw% "
      + "OR q.content LIKE %:kw% "
      + "OR m1.membername LIKE %:kw% "
      + "OR a.content LIKE %:kw% "
      + "OR m2.membername LIKE %:kw%")
            Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}
