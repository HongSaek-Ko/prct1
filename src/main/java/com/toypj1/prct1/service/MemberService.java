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

  public Member create(String membername, String email, String password) {
    Member member = new Member();
    member.setMembername(membername);
    member.setEmail(email);
    // 패스워드는 BCrypt 해싱함수로 암호화하여 저장
    member.setPassword(passwordEncoder.encode(password));
    memberRepository.save(member);
    return member;
  }

  // 멤버 정보 가져오기
  public Member getMember(String membername) {
    Optional<Member> member = memberRepository.findByMembername(membername);
    // 멤버 존재하면 해당 정보 가져오고, 없으면 예외 던짐
    if(member.isPresent()) {
      return member.get();
    } else {
      throw new DataNotFoundException("member not found");
    }
  }
}
