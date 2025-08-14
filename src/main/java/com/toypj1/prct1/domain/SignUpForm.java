package com.toypj1.prct1.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpForm {
  
  // 길이 3 ~ 25 사이
  @Size(min=3, max=25, message = "ID는 3~25자 사이여야 합니다.")
  @NotEmpty(message = "사용자 ID를 작성해주세요.")
  private String membername;

  @NotEmpty(message = "비밀번호를 작성해주세요.")
  private String password;
  
  @NotEmpty(message = "비밀번호 확인을 작성해주세요.")
  private String passwordCheck;
  
  @NotEmpty(message = "이메일을 작성해주세요.")
  private String email;
}
