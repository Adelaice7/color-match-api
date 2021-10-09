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
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableBatchProcessing
public class ImportJobBatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ProductRepository productRepository;

    @Value("${chunk-size}")
    private int chunkSize;

    @Bean
    public Job importProductJob(ImportJobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importProductJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public ImportJobCompletionNotificationListener importJobExecutionListener() {
        return new ImportJobCompletionNotificationListener();
    }

    /**
     * Responsible for reading and parsing the Products from a CSV file of given filePath.
     * filePath is received from JobParameters upon starting the job in ProductService.
     * @param filePath the CSV file's path
     * @return
     */
    @StepScope
    @Bean
    public FlatFileItemReader fileReader(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder().name("productItemReader")
                .resource(new FileSystemResource(filePath))
                // Skip header line of file
                .linesToSkip(1)
                .delimited()
                .names(new String[] {"id", "title", "gender_id", "composition", "sleeve", "photo", "url"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper() {{
                    setTargetType(Product.class);
                }})
                .build();
    }

    /**
     * Creates the writer that writes the parsed Products into the database using the Repository.
     * @return
     */
    @Bean(name = "databaseWriter")
    public RepositoryItemWriter<Product> databaseWriter() {
        return new RepositoryItemWriterBuilder<Product>()
                .repository(productRepository).build();
    }

    /**
     * This is the step for the import job.
     * It sets the chunk size received from the application.properties file, sets the reader and writer,
     * as well as the taskExecutor for multi-threaded execution.
     * @param writer
     * @return
     */
    @Bean
    public Step step1(RepositoryItemWriter<Product> writer) {
        return stepBuilderFactory.get("step1")
                .<Product, Product> chunk(chunkSize)
                .reader(fileReader(null))
                .writer(writer)
                // Multi-threaded execution
                .taskExecutor(taskExecutor())
                .build();
    }

    /**
     * Creates the Task Executor to use multi-threaded execution in the file processing.
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(10);
        executor.setQueueCapacity(3);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("ProductThread-");
        return executor;
    }
}
