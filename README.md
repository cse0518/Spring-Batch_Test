# Spring-Batch_Test

- 스프링 배치 프로그램을 실습합니다.

<details>
<summary>Job Configuration</summary>
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

<details>
<summary>Scope, Job Parameter</summary>
<div>

- 파라미터를 받아 여러 Batch 컴포넌트에서 사용 → Job Parameter  
  Job Parameter를 사용하기 위해선 항상 Scope를 선언해야함.
  - `@JobScope`
    - Step 선언문에서 사용 가능
    - jobParameters와 jobExecutionContext 사용 가능
    - ex) `@Value("#{jobParameters[파라미터명]}")`
  - `@StepScope`
    - Tasklet 또는 ItemReader, ItemWriter, ItemProcessor에서 사용 가능
    - jobParameters와 stepExecutionContext 사용 가능
- default proxyMode
  ```java
  @Scope(value = "step", proxyMode = ScopedProxyMode.TARGET_CLASS)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface StepScope {
  
  }
  ```
  - 메소드의 리턴 타입을 구현체의 타입으로 사용해야 함.
    - ItemReader 부분이 Proxy 객체로 생성되어 문제 발생.
      ```java
      @Bean
      @StepScope
      public ItemReader<Member> reader(@Value("#{jobParameters[firstName]}") String firstName) {
          Map<String, Object> paramMap = new HashMap<>();
          paramMap.put("firstName", firstName);
    
          JpaPagingItemReader<Member> reader = new JpaPagingItemReader<>();
          reader.setQueryString("Select m From Member m where m.firstName=:firstName");
          reader.setParameterValues(paramMap);
          reader.setEntityManagerFactory(entityManagerFactory);
          reader.setPageSize(10);
    
          return reader;
      }
      ```
    - 메소드의 리턴 타입을 구현체의 타입으로 지정해서 해결.
      ```java
      @Bean
      @StepScope
      public JpaPagingItemReader<Member> reader(@Value("#{jobParameters[firstName]}") String firstName) {
          Map<String, Object> paramMap = new HashMap<>();
          paramMap.put("firstName", firstName);
    
          JpaPagingItemReader<Member> reader = new JpaPagingItemReader<>();
          reader.setQueryString("Select m From Member m where m.firstName=:firstName");
          reader.setParameterValues(paramMap);
          reader.setEntityManagerFactory(entityManagerFactory);
          reader.setPageSize(10);
    
          return reader;
      }
      ```

</div>
</details>

<details>
<summary>Chunk</summary>
<div>

- `Chunk`
  - 데이터 덩어리로 작업할 때 각 커밋 사이에 처리되는 row 수
  - 한 번에 하나의 Item 단위로 데이터를 읽어서(read) 가공하고(process), Chunk 단위로 트랜잭션을 처리한다(write).
  - 실패할 경우 chunk 단위로 롤백됨. 이전 chunk 범위는 반영.
- `Page size` vs `Chunk size`
  - `Page size`는 한 번에 조회할 Item 수
  - `Chunk size`는 한 번에 처리될 트랜잭션 단위
  - 즉, Page 단위로 끊어서 조회하고 Chunk 단위로 끊어서 처리한다.  
    Page size와 Chunk size가 다를 경우 여러번의 비효율적인 쿼리가 발생하기 때문에 **같게 설정하는 것이 좋다.**

</div>
</details>

<details>
<summary>운영 환경에서 실행 명령</summary>
<div>

```shell
java -jar batch-application.jar --job.name=simpleJob
```

</div>
</details>

## References

- [Jojoldu(이동욱) 님의 블로그 - Spring Batch 가이드](https://jojoldu.tistory.com/category/Spring%20Batch)
- [Spring Batch 5.0 Migration 가이드 (공식 문서)](https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-5.0-Migration-Guide)
