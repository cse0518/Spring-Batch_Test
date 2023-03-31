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

    private final JobRepository jobRepository;

    @Bean
    public Job simpleJob(Step step) {
        return new JobBuilder("simpleJob", jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public Step simpleStep(PlatformTransactionManager transactionManager) {
        return new StepBuilder("simpleStep", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .transactionManager(transactionManager)
                .build();
    }
}
