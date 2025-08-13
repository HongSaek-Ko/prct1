package com.toypj1.prct1.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerForm {
  @NotEmpty(message = "내용을 작성해주세요.")
  private String content;
}
