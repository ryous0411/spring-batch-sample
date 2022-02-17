package org.springframework.batch.sample.fieldset;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.sample.dto.ScoreDto;
import org.springframework.validation.BindException;

public class ScoreDtoFieldSetMapper implements FieldSetMapper<ScoreDto> {

  @Override
  public ScoreDto mapFieldSet(FieldSet fieldSet) throws BindException {
    return new ScoreDto(
        fieldSet.readString(0),
        fieldSet.readInt(1)
    );
  }
}
