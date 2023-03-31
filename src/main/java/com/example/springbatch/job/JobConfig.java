package com.example.springbatch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
public class JobConfig {

//  기존 방식 deprecated
//  private final JobBuilderFactory jobBuilderFactory;
//  private final StepBuilderFactory stepBuilderFactory;
    private final JobRepository jobRepository;

    @Bean
    public Job simpleJob(Step step) {
//      기존 방식
//      return jobBuilderFactory.get("simpleJob")
//              .start(simpleStep1())
//              .build();
        return new JobBuilder("simpleJob", jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public Step simpleStep(PlatformTransactionManager transactionManager) {
//      기존 방식
//      stepBuilderFactory.get("simpleStep1")
//              .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
//              .build();
        return new StepBuilder("simpleStep", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .transactionManager(transactionManager)
                .build();
    }
}
