# Spring-Batch_Test

- 스프링 배치 프로그램을 실습합니다.

<details>
<summary>기본 Job Configuration</summary>
<div>

- Spring Batch 5.0 이전 (기존 버전)
  ```java
  import lombok.RequiredArgsConstructor;
  import org.springframework.batch.core.Job;
  import org.springframework.batch.core.Step;
  import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
  import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
  import org.springframework.batch.repeat.RepeatStatus;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  
  @RequiredArgsConstructor
  @Configuration
  public class JobConfig {
  
      private final JobBuilderFactory jobBuilderFactory;
      private final StepBuilderFactory stepBuilderFactory;
  
      @Bean
      public Job simpleJob(Step step) {
          return jobBuilderFactory.get("simpleJob")
                  .start(simpleStep1())
                  .build();
      }
  
      @Bean
      public Step simpleStep1() {
          return stepBuilderFactory.get("simpleStep1")
                  .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                  .build();
      }
  }
  ```

- Spring Batch 5.0 이후
  ```java
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
  ```

</div>
</details>

## References

- [Jojoldu(이동욱) 님의 블로그 - Spring Batch 가이드](https://jojoldu.tistory.com/category/Spring%20Batch)
- [Spring Batch 5.0 Migration 가이드 (공식 문서)](https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-5.0-Migration-Guide)
