package com.spring.batch;

import com.spring.batch.decider.DeliveryDecider;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfigurationApplication {

	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;

	@Bean
	public JobExecutionDecider decider(){
		return new DeliveryDecider();
	}

	@Bean
	public Step packageItemStep(){
		return this.stepBuilderFactory.get("packageItemStep")
				.tasklet((stepContribution, chunkContext) -> {
					String cost = chunkContext.getStepContext().getJobParameters().get("cost").toString();
					String date = chunkContext.getStepContext().getJobParameters().get("run.date").toString();
					System.out.println("The flycam has been packaged on " + date + " with cost " + cost);
					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public Step storePackageStep(){
		return this.stepBuilderFactory.get("storePackageStep")
				.tasklet((stepContribution, chunkContext) -> {
					System.out.println("Storing the package while the customer address is located.");
					return RepeatStatus.FINISHED;
				}).build();
	}
	@Bean
	public Step leaveAtDoorStep(){
		return this.stepBuilderFactory.get("leaveAtDoorStep")
				.tasklet((stepContribution, chunkContext) -> {
					System.out.println("Leaving the package at the door.");
					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public Step driveToAddressStep(){
		boolean GOST_LOST = false;
		return this.stepBuilderFactory.get("driveToAddressStep")
				.tasklet((stepContribution, chunkContext) -> {
					if (GOST_LOST) {
						throw new RuntimeException("Gost lost driving to the address");
					}
					System.out.println("Success fully arrived to Address");
					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public Step givePackageToCustomerStep(){
		return this.stepBuilderFactory.get("givePackageToCustomerStep")
				.tasklet((stepContribution, chunkContext) -> {
					System.out.println("Given package to customer.");
					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public Job reDeliverPackageJob(){
		return jobBuilderFactory.get("reDeliverPackageJob")
				.start(packageItemStep())
				.next(driveToAddressStep())
				.on("FAILED").to(storePackageStep())
				.from(driveToAddressStep())
				.on("*").to(decider())
				.on("PRESENT").to(givePackageToCustomerStep())
				.from(decider())
				.on("*").to(leaveAtDoorStep())
				.end()
				.build();
	}
	public static void main(String[] args) {
		SpringApplication.run(SpringBatchConfigurationApplication.class, args);
	}

}
