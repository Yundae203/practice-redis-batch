package enterprise.test.infra.batch;

import enterprise.test.infra.redis.RedisService;
import enterprise.test.infra.redis.ReviewCache;
import enterprise.test.product.domain.Product;
import enterprise.test.product.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ReviewJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RedisService<ReviewCache, Long> redisService;
    private final ProductRepository productRepository;

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
                .<KeyCache, Product>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .faultTolerant()
                .retryPolicy(unlimitedRetryPolicy())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @Lazy
    @StepScope
    public ItemReader<KeyCache> reader() {
        return new RedisKeyItemReader(redisService); // Redis에 저장된 해시를 전부 가져오는 ItemReader
    }

    @Bean
    public ItemProcessor<KeyCache, Product> processor() {
        return keyCache -> {

            // JPA로 상품 조회
            Product product = productRepository.findById(keyCache.key());

            // 리뷰 카운트와 점수 조정
            ReviewCache cache = keyCache.cache();
            product.updateCountAndScore(cache.count(), cache.calculatedScore());

            return product;
        };
    }

    @Bean
    public ItemWriter<Product> writer() {
        return items -> {
            for (Product product : items) {
                productRepository.update(product);
            }
        };
    }

    @Bean
    public RetryPolicy unlimitedRetryPolicy() {
        return new SimpleRetryPolicy(Integer.MAX_VALUE);
    }

}