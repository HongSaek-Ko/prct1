package com.toypj1.prct1.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.toypj1.prct1.domain.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
  Question findBySubject(String subject);
  Question findBySubjectAndContent(String sbj, String cont);
  List<Question> findBySubjectLike(String spSbj);
  Page<Question> findAll(Pageable pageable);
}
