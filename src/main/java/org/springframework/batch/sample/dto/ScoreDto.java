package org.springframework.batch.sample.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ScoreDto {

  private final String name;

  private final Integer score;
}
