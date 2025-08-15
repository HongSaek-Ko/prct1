package com.toypj1.prct1.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.toypj1.prct1.domain.Member;
import com.toypj1.prct1.domain.ROLE;
import com.toypj1.prct1.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// s.s.가 제공하는 UserDetailService를 구현
@RequiredArgsConstructor
@Service
@Slf4j
public class MemberSecService implements UserDetailsService{
  
  private final MemberRepository memberRepository;

  // UserDetails loadUserByUsername 메서드를 반드시 오버라이드 해야 함
  // - 사용자명으로 비밀번호를 조회하여 리턴하는 메서드
  @Override
  public UserDetails loadUserByUsername(String membername) throws UsernameNotFoundException {
    // membername으로 사용자 조회하여 _member에 담음
    Optional<Member> _member = this.memberRepository.findByMembername(membername);
    
    // 조회 결과 없으면 아래 오류 출력
    if(_member.isEmpty()) {
      log.info("User_not_found: {}", membername);
      throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
    }
    // _member의 데이터를 m에 담음
    Member m = _member.get();
    log.info("signInfo: {}", m);

    // 권한 목록
    List<GrantedAuthority> authorities = new ArrayList<>();

    // 사용자명이 "admin"이면 권한 목록 중 해당 권한(ADMIN) 부여
    if("admin".equals(membername)) {
      authorities.add(new SimpleGrantedAuthority(ROLE.ADMIN.getValue()));
      // 그 외 나머지는 권한 MEMBER 부여
    } else{ authorities.add(new SimpleGrantedAuthority(ROLE.MEMBER.getValue()));
    }
    // 객체 User(유저명, 비밀번호, 권한) 리턴
    return new User(m.getMembername(), m.getPassword(), authorities);
  }
}
