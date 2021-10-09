package com.rmeunier.colormatchapi.config;

import com.rmeunier.colormatchapi.dao.ProductRepository;
import com.rmeunier.colormatchapi.model.Product;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableBatchProcessing
public class DomColorJobBatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RepositoryItemWriter<Product> databaseWriter;

    @Value("${chunk-size}")
    private int chunkSize;

    @Bean
    public Job domColorJob(DomColorJobCompletionNotificationListener listener, Step domColorStep1) {
        return jobBuilderFactory.get("domColorJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(domColorStep1)
                .end()
                .build();
    }

    @Bean
    public DomColorJobCompletionNotificationListener domColorJobExecutionListener() {
        return new DomColorJobCompletionNotificationListener();
    }

    @StepScope
    @Bean
    public RepositoryItemReader<Product> databaseReader() {
        Map<String, Sort.Direction> sorts = new HashMap<>();
        return new RepositoryItemReaderBuilder<Product>()
                .name("productItemDbReader")
                .repository(productRepository)
                .methodName("findAll")
                .pageSize(5)
                .sorts(sorts)
                .name("dominantColor")
                .build();
    }

    @Bean
    public ProductItemProcessor processor() {
        return new ProductItemProcessor();
    }

    @Bean
    public Step domColorStep1(RepositoryItemWriter<Product> databaseWriter) {
        return stepBuilderFactory.get("domColorStep1")
                .<Product, Product> chunk(chunkSize)
                .reader(databaseReader())
                .processor(processor())
                .writer(databaseWriter)
                // Multi-threaded execution
                .taskExecutor(domColorTaskExecutor())
                .build();
    }

    /**
     * Creates the Task Executor to use multi-threaded execution in the database row processing.
     * @return the executor
     */
    @Bean
    public ThreadPoolTaskExecutor domColorTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(10);
        executor.setQueueCapacity(3);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("ProductDomColThread-");
        return executor;
    }
}
