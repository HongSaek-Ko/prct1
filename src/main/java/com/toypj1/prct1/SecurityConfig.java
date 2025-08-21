package com.toypj1.prct1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.toypj1.prct1.service.MemberSecService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Controller의 메서드 중 @PreAuthorize 어노테이션을 사용하기 위해서 필요함
@Slf4j // 로그
public class SecurityConfig {

  private final MemberSecService memberSecService;
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // 모든 요청을 인증 없이 허용 (= 로그인 안해도 접근 가능)
    http
      .authorizeHttpRequests(authorize -> authorize
      .anyRequest().permitAll()
    )
    // 로그인 관련 security 설정
    .formLogin(form -> form
      .loginPage("/user/signIn") // 사용자 정의 로그인 페이지 경로
      .defaultSuccessUrl("/", true) // 로그인 성공 시 루트 경로("/")로 리다이렉트
    )
    // 로그아웃 관련 설정
    .logout(logout -> logout
      .logoutUrl("/user/signOut") // 로그아웃 URL 지정
      .logoutSuccessUrl("/") // 로그아웃 성공 시 이동할 경로
      .invalidateHttpSession(true) // 세션 무효화
      .deleteCookies("JSESSIONID") // 쿠키 제거
      .permitAll()
    )
    ;
    return http.build();
  }

  // BCrypt 암호화하는 PasswordEncoder Bean 객체 등록
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // s.s.의 인증을 담당하는 AuthenticationManager의 bean 객체 생성
  // 사용자 조회를 MemberSecService가 담당하도록 설정
  // 비밀번호 검증에 사용할 passwordEncoder도 함께 등록해야 함
  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = 
      http.getSharedObject(AuthenticationManagerBuilder.class);

    authenticationManagerBuilder
      .userDetailsService(memberSecService)
      .passwordEncoder(passwordEncoder());

    return authenticationManagerBuilder.build();
  }
}

