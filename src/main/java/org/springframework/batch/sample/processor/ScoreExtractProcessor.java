package org.springframework.batch.sample.processor;

import static org.springframework.batch.sample.listener.ScoreWriterJobExecutionListener.SKIP_LIST_KEY;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.sample.dto.ScoreDto;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class ScoreExtractProcessor<I extends ScoreDto, O extends JsonNode> implements
    ItemProcessor<I, O> {

  private final ObjectMapper objectMapper;

  private List<I> skipList = new ArrayList<>();

  public ScoreExtractProcessor(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public O process(I item) throws Exception {

    if (item.getScore() == 0) {
      skipList.add(item);
      return null;
    }

    var objectNode = objectMapper.createObjectNode();
    objectNode.put("name", item.getName());
    objectNode.put("score", item.getScore());

    var type = objectMapper.getTypeFactory().constructType(new TypeReference<O>() {
    });
    return objectMapper.treeToValue(objectNode, type);
  }

  @AfterStep
  public void afterStep(StepExecution stepExecution) {
    stepExecution.getExecutionContext().put(SKIP_LIST_KEY, skipList);
  }
}
