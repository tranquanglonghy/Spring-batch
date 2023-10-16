package com.spring.batch.decider;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import java.time.LocalDateTime;

public class DeliveryDecider implements JobExecutionDecider {
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        String result = LocalDateTime.now().getHour() > 9 ? "PRESENT" : "NOT PRESENT";
        System.out.println("the Decider is: " + result);
        return new FlowExecutionStatus(result);
    }
}
