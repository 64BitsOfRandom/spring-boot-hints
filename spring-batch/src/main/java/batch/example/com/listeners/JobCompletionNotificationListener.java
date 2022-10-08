package batch.example.com.listeners;

import batch.example.com.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * This listener allows to customize Job Flow: on every stage methods could be triggered.
 */

@Log4j2
@Component
@RequiredArgsConstructor
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        if (BatchStatus.STARTING.equals(jobExecution.getStatus())) {
            log.info("!!! JOB is being started in short time!!!");
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(BatchStatus.COMPLETED.equals(jobExecution.getStatus())){
            log.info("!!! JOB FINISHED! Time to verify the results");

            jdbcTemplate.query("SELECT first_name, last_name FROM people",
                    (rs, row) -> new Person(
                            rs.getString(1),
                            rs.getString(2))
            ).forEach(person -> log.info("Found <" + person + "> in the database."));
        }
    }
}
