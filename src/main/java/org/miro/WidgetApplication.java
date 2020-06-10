package org.miro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WidgetApplication {
    public static void main(String[] args) {
        SpringApplication.run(WidgetApplication.class, args);
    }
}