package org.springframework.batch.sample.config;

import static org.springframework.batch.sample.listener.ScoreWriterJobExecutionListener.SKIP_LIST_KEY;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.sample.dto.ScoreDto;
import org.springframework.batch.sample.fieldset.ScoreDtoFieldSetMapper;
import org.springframework.batch.sample.listener.ScoreWriterJobExecutionListener;
import org.springframework.batch.sample.processor.ScoreExtractProcessor;
import org.springframework.batch.sample.writer.JsonFileWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class BatchConfig extends DefaultBatchConfigurer {

  private final JobBuilderFactory jobBuilderFactory;

  private final StepBuilderFactory stepBuilderFactory;

  private final FlatFileItemReader<ScoreDto> csvFileReader;

  private final ScoreExtractProcessor<ScoreDto, JsonNode> scoreExtractProcessor;

  private final JsonFileWriter<JsonNode> jsonFileWriter;

  @Bean
  public Job jsonWriter() {
    return this.jobBuilderFactory.get("json-writer")
        .start(writeJsonFileStep())
        .listener(new ScoreWriterJobExecutionListener())
        .build();
  }

  public Step writeJsonFileStep() {
    return this.stepBuilderFactory.get("csv-to-json")
        .<ScoreDto, JsonNode>chunk(1000)
        .reader(csvFileReader)
        .processor(scoreExtractProcessor)
        .writer(jsonFileWriter)
        .listener(writeJsonFilePromotionListener())
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<ScoreDto> csvFileReader(
      @Value("#{jobParameters['readFilePath']}") String readFilePath) {
    var itemReader = new FlatFileItemReader<ScoreDto>();
    itemReader.setResource(new FileSystemResource(readFilePath));
    itemReader.setLineMapper(new DefaultLineMapper<>() {{
      setLineTokenizer(new DelimitedLineTokenizer());
      setFieldSetMapper(new ScoreDtoFieldSetMapper());
    }});
    return itemReader;
  }

  @Bean
  public ExecutionContextPromotionListener writeJsonFilePromotionListener() {
    String[] keySet = {SKIP_LIST_KEY};
    ExecutionContextPromotionListener promotionListener = new ExecutionContextPromotionListener();
    promotionListener.setKeys(keySet);
    return promotionListener;
  }
}
