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
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job simpleJob() {
        return new JobBuilder("simpleJob", jobRepository)
                .start(step1())   // step1 실행
                    .on("FAILED") // contribution.exitStatus가 FAILED 일 경우
                    .to(step3())  // step3으로
                    .on("*")      // step3의 결과와 관계 없이
                    .end()        // Flow 종료
                .from(step1())     // step1로부터
                    .on("*")       // contribution.exitStatus가 FAILED 이외의 모든 경우
                    .to(step2())   // step2로
                    .next(step3()) // step2 종료 후 step3
                    .on("*")       // step3의 결과와 관계 없이
                    .end()         // Flow 종료
                .end() // Job 종료
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
//              .tasklet((contribution, chunkContext) -> { // 실패인 경우
//                  contribution.setExitStatus(ExitStatus.FAILED);
//                  return RepeatStatus.FINISHED;
//              })
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Step step3() {
        return new StepBuilder("step3", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .transactionManager(transactionManager)
                .build();
    }
}
