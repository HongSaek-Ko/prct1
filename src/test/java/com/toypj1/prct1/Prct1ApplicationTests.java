package com.toypj1.prct1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.toypj1.prct1.domain.Question;
import com.toypj1.prct1.repository.QuestionRepository;
import com.toypj1.prct1.service.QuestionService;


@SpringBootTest
class Prct1ApplicationTests {

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private QuestionService questionService;

	@Test
	public void testJpa() {
		// 1-1. 첫 번째 질문
		Question q1 = new Question();
		q1.setSubject("내가 누구냐고?");
		q1.setContent("알 필요 없다.");
		q1.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q1);

		// 1-2. 두 번째 질문
		Question q2 = new Question();
		q2.setSubject("헛 것이 보이나?");
		q2.setContent("애석하군.");
		q2.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q2);

		// 2. 모든 데이터 조회
		List<Question> all_Questions = this.questionRepository.findAll();
		// assertEquals(기대값, 실제값): 기대값과 실제값이 동일하면 통과, 아니면 실패
		// 크기 기대값 2, 크기 실제값 2. 통과.
		assertEquals(2, all_Questions.size());

		// 기대값 인덱스 0번(= 첫 번째 질문), 실제값 "내가 누구냐고?". 통과.
		Question q3 = all_Questions.get(0);
		assertEquals("내가 누구냐고?", q3.getSubject());
	}

	// 3-1. 특정 데이터 조회: ID로 찾기
	@Test
	public void testFindById() {
		Optional<Question> q = this.questionRepository.findById(2);
		if(q.isPresent()) {
			Question q1 = q.get();
			assertEquals("헛 것이 보이나?", q1.getSubject());
		}
	}

	// 3-2. 특정 데이터 조회: 제목으로 찾기
	@Test
	public void testFindBySubject() {
		Question q = this.questionRepository.findBySubject("내가 누구냐고?");
		assertEquals(1, q.getId());
	}

	// 3-3. 특정 데이터 조회: 제목 및 내용으로 찾기
	@Test
	public void testFindBySubjectAndContent() {
		Question q = this.questionRepository.findBySubjectAndContent("내가 누구냐고?", "알 필요 없다.");
		assertEquals(1, q.getId());
	}

	// 4. 특정 데이터 조회: 특정 문자열 포함 여부로 찾기 (~로 시작하는)
	@Test
	public void testFindBySubjectLike() {
		List<Question> q = this.questionRepository.findBySubjectLike("내%");
		Question q1 = q.get(0);
		assertEquals("내가 누구냐고?", q1.getSubject());
	}

	// 5. 데이터 수정
	@Test
	public void testUpdate() {
		Optional<Question> uq = this.questionRepository.findById(1);
		assertTrue(uq.isPresent());
		Question q = uq.get();
		q.setSubject("질문은 그만");
		this.questionRepository.save(q);
	}
	// 나머지는 https://saranghaeo.tistory.com/148 에서 찾아보아요~

	@Test
	void msvData() {
		for (int i = 0; i <= 299; i++) {
			String sbj = String.format("제목:[%03d]", i);
			String cont = "내용";
			questionService.registQuestion(sbj, cont);
		}
	}

}
