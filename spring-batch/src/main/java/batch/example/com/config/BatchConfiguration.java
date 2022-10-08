package batch.example.com.config;

import batch.example.com.listeners.JobCompletionNotificationListener;
import batch.example.com.model.Person;
import batch.example.com.processors.PersonItemProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfiguration {
    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Person> reader(){
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv")) //target file path
                .delimited() //set separation
                .names(new String[]{"firstName", "lastNames"}) //field names
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>(){{setTargetType(Person.class);}}) //mapping csv to dto
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource) //datasource provided by batch, could be configured in SpringDate
                .build();
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1){
        return jobBuilderFactory.get("importUserJob") //set job name
                .listener(listener)
                .flow(step1) //set steps order
                .end().build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Person> writer){
        return stepBuilderFactory.get("step1")
                .<Person,Person> chunk(10) // batch size
                .reader(reader())
                .processor(processor())
                .writer(writer).build();
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }
}
