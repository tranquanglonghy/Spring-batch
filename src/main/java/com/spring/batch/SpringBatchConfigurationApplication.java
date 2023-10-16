package com.spring.batch;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Date;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
@AllArgsConstructor
public class SpringBatchConfigurationApplication {
	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;

	private final JobLauncher jobLauncher;

	@Bean
	public Step step() {
		return this.stepBuilderFactory.get("step").tasklet((contribution, chunkContext) -> {
			System.out.println("The run time is: " + LocalDateTime.now());
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Job job() {
		return this.jobBuilderFactory.get("job").start(step()).build();
	}

	@Scheduled(cron = "0/5 * * * * *")
	public void run() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		JobParametersBuilder parametersBuilder = new JobParametersBuilder();
		parametersBuilder.addDate("runtime", new Date());
		this.jobLauncher.run(job(), parametersBuilder.toJobParameters());
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchConfigurationApplication.class, args);
	}

}
