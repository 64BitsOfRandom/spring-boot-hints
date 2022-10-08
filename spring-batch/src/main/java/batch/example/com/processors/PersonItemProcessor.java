package batch.example.com.processors;

import batch.example.com.model.Person;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;

/**
 * This ItemProcessor intercepts recieved item and process it, then it produces some output.
 * ItemProcessor scope is a stage.
 */

@Log4j2
public class PersonItemProcessor implements ItemProcessor<Person, Person> {
    @Override
    public Person process(Person person) {
        String upperCaseFName = person.getFirstName().toUpperCase();
        String upperCaseLName = person.getLastName().toUpperCase();
        final Person processed = new Person(upperCaseFName,upperCaseLName);
        log.info("Converting ({}) into ({}})", person,processed);
        return processed;
    }
}
