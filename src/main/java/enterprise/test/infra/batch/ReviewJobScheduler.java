package enterprise.test.infra.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job job;

    @Scheduled(cron = "0 */1 * * * ?") // 10분마다 실행
    public void runJob() throws Exception {
        jobLauncher.run(job, new JobParametersBuilder().toJobParameters());
    }
}
