package wt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;


@Profile({"!unittest","web", "consumer"})// Dont run during unit tests
@SpringBootApplication
public class EhOtelApplication {

    public static final Logger LOGGER = LoggerFactory.getLogger(EhOtelApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EhOtelApplication.class, args);
    }

}
