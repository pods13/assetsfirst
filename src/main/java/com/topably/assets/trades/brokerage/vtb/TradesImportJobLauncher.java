package com.topably.assets.trades.brokerage.vtb;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TradesImportJobLauncher {

    private final Job job;
    private final JobLauncher jobLauncher;

    public TradesImportJobLauncher(@Qualifier("tradesImportJob") Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }

//    @Scheduled(fixedDelay = 100000L)
    void launchXmlFileToDatabaseJob() throws Exception {
        jobLauncher.run(job, newExecution());
    }

    private JobParameters newExecution() {
        Map<String, JobParameter> parameters = new HashMap<>();

        JobParameter filename = new JobParameter("sample.xls");
        parameters.put("filename", filename);
        JobParameter date = new JobParameter(new Date());
        parameters.put("date", date);

        return new JobParameters(parameters);
    }
}
