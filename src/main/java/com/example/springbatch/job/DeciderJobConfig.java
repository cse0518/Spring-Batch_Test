package com.example.springbatch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Random;

@RequiredArgsConstructor
@Configuration
public class DeciderJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job deciderJob() {
        return new JobBuilder("deciderJob", jobRepository)
                .start(startStep())
                .next(decider()) // 홀수, 짝수 구분
                .from(decider())
                    .on("ODD")     // decider 상태가 ODD라면
                    .to(oddStep()) // oddStep으로
                .from(decider())
                    .on("EVEN")     // decider의 상태가 EVEN이라면
                    .to(evenStep()) // evenStep으로
                .end() // builder 종료
                .build();
    }

    @Bean
    public Step startStep() {
        return new StepBuilder("start", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Step evenStep() {
        return new StepBuilder("even", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Step oddStep() {
        return new StepBuilder("odd", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .transactionManager(transactionManager)
                .build();
    }

    private JobExecutionDecider decider() {
        return new OddDecider();
    }

    public static class OddDecider implements JobExecutionDecider {

        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            Random random = new Random();
            int randomNumber = random.nextInt(50) + 1;

            if (randomNumber % 2 == 0) {
                return new FlowExecutionStatus("EVEN");
            } else {
                return new FlowExecutionStatus("ODD");
            }
        }
    }
}
