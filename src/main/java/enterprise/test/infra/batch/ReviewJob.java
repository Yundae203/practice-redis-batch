package enterprise.test.config;

import enterprise.test.product.infra.jpa.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ReviewJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job job() {
        return new JobBuilder("review-update-job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return new StepBuilder("review-update-step", jobRepository)
                .<ProductEntity, ProductEntity>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    private ItemReader<? extends ProductEntity> reader() {
        return null;
    }

    private ItemProcessor<? super ProductEntity,? extends ProductEntity> processor() {
        return null;
    }

    private ItemWriter<? super ProductEntity> writer() {
        return null;
    }
}
