package com.toypj1.prct1.domain;

import lombok.Getter;

// 열거 자료형(enum). 상수 자료형이므로 Getter만 사용 가능
@Getter
public enum ROLE {
  // ADMIN, USER 각각 값 지정
  ADMIN("ROLE_ADMIN"), MEMBER("ROLE_MEMBER");

  ROLE(String value) { this.value = value; }

  private String value;
}
