package org.samasama.batchdemo.config;

import lombok.RequiredArgsConstructor;
import org.samasama.batchdemo.subject.Subject;
import org.samasama.batchdemo.subject.SubjectProcessor;
import org.samasama.batchdemo.subject.SubjectRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    private final SubjectRepository subjectRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private static LineMapper<Subject> lineMapper() {
        DefaultLineMapper<Subject> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "age");
        BeanWrapperFieldSetMapper<Subject> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Subject.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public FlatFileItemReader<Subject> itemReader() {
        FlatFileItemReader<Subject> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/subjects.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }


    @Bean
    Step importStep() {
        return new StepBuilder("csvImport", jobRepository)
                .<Subject, Subject>chunk(10, platformTransactionManager)
                .reader(itemReader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job runJob() {
        return new JobBuilder("importSubject", jobRepository)
                .start(importStep())
                .build();
    }

    @Bean
    public SubjectProcessor processor() {
        return new SubjectProcessor();
    }

    @Bean
    public RepositoryItemWriter<Subject> writer() {
        RepositoryItemWriter<Subject> writer = new RepositoryItemWriter<>();
        writer.setRepository(subjectRepository);
        writer.setMethodName("save");
        return writer;
    }
}
