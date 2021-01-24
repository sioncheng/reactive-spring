package com.rs.reactor;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ReactorApplication implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Flux.range(1,2)
                .repeat()
                .collectList()
                .block();

    }

    public static void main(String[] args) {
        SpringApplication.run(ReactorApplication.class, args);
    }
}
