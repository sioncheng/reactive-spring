package com.example.rpws.rxjava;

import io.reactivex.Observable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RxJavaApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(RxJavaApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Observable.zip(Observable.just(1,2,4),
				Observable.just(4,2,1),
				(x, y) -> x * y).subscribe(System.out::println);


	}
}
