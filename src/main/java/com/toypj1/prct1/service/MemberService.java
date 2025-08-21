package com.toypj1.prct1.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.toypj1.prct1.DataNotFoundException;
import com.toypj1.prct1.domain.Member;
import com.toypj1.prct1.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  // 회원가입 (사용자명, 이메일, 비밀번호)
  public Member create(String membername, String email, String password) {
    Member member = new Member();
    member.setMembername(membername);
    member.setEmail(email);
    // 패스워드는 BCrypt 해싱함수로 암호화하여 저장
    member.setPassword(passwordEncoder.encode(password));
    memberRepository.save(member);
    return member;
  }

  // 멤버 조회 ((조회할)멤버)
  public Member getMember(String membername) {
    return memberRepository.findByMembername(membername)
      .orElseThrow(() -> new DataNotFoundException("사용자 정보 없음!"));
  }
}
