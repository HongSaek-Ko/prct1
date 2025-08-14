package com.toypj1.prct1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.toypj1.prct1.domain.Member;


public interface MemberRepository extends JpaRepository<Member, Long> {
  Optional<Member> findByMembername(String membername);
} 