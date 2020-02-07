package com.bv.actionmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJpaRepositories
public class Application {

    public static void main(final String[] arguments) {
        SpringApplication.run(Application.class, arguments);
    }

}
