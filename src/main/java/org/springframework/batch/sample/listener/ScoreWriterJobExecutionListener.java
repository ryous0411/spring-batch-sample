package org.springframework.batch.sample.listener;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.sample.dto.ScoreDto;

@Slf4j
public class ScoreWriterJobExecutionListener implements JobExecutionListener {

  public static final String SKIP_LIST_KEY = "skipList";

  @Override
  public void beforeJob(JobExecution jobExecution) {

  }

  @Override
  public void afterJob(JobExecution jobExecution) {

    var skipList = (List<ScoreDto>) jobExecution.getExecutionContext().get(SKIP_LIST_KEY);

    if (!skipList.isEmpty()) {
      skipList.forEach(s -> System.out.println(s.getName()));
    }
  }
}
