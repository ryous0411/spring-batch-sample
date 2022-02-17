package org.springframework.batch.sample.writer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileWriter;
import java.util.List;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class JsonFileWriter<T extends JsonNode> implements ItemWriter<T> {

  private final ObjectMapper objectMapper;

  @Value("#{jobParameters['writeFilePath']}")
  private String writeFilePath;

  @Autowired
  public JsonFileWriter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void write(List<? extends T> items) throws Exception {

    try {
      var jsonStr = objectMapper.writeValueAsString(items);
      var fileWriter = new FileWriter(writeFilePath);

      fileWriter.write(jsonStr);
      fileWriter.close();

    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
