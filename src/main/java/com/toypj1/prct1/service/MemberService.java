package com.toypj1.prct1.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.toypj1.prct1.domain.Member;
import com.toypj1.prct1.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public Member create(String membername, String email, String password) {
    Member member = new Member();
    member.setMembername(membername);
    member.setEmail(email);
    // 패스워드는 BCrypt 해싱함수로 암호화하여 저장
    member.setPassword(passwordEncoder.encode(password));
    memberRepository.save(member);
    return member;
  }
}
