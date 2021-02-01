package com.rs.reactor;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@SpringBootApplication
public class ReactorApplication implements ApplicationRunner {


    public static void main(String[] args) {
        SpringApplication.run(ReactorApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        //oomFlux();
        //mono();
        //customPublisher()
        //customConsumer();
        //processor();
        //flatMap();
        //reduceAndScan();
        combining();
    }


    private void oomFlux() {
        Flux.range(1,2)
                .repeat()
                .collectList()
                .block();
    }

    private void mono() {
        Mono.just(1).subscribe(System.out::println);
        Mono.justOrEmpty(null).subscribe(System.out::println);
        Mono.fromCallable(()->{return "hello mono";}).subscribe(System.out::println);
        Mono.error(new Exception("oops")).subscribe(System.out::println, t -> t.printStackTrace());
        Mono.defer(()->{
            if ((System.currentTimeMillis() % 10 ) > 5) {
                return Mono.just(10);
            } else {
                return Mono.justOrEmpty(null);
            }
        }).subscribe(System.out::println);
    }

    private void customPublisher() {
        Flux.from(new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> subscriber) {
                subscriber.onNext("a");
                subscriber.onNext("b");
                boolean ok = (LocalDateTime.now().getSecond() % 10) > 5;
                if (ok) {
                    subscriber.onNext("c");
                    subscriber.onComplete();
                } else {
                    subscriber.onError(new Exception("oops"));
                }
            }
        }) .subscribe(data -> System.out.println(data),
                err -> System.out.println(err),
                () -> System.out.println("onComplete"));
    }

    private void customConsumer() {
        Flux.range(1, 3)
                .subscribe(new Subscriber<Integer>() {
                    private Subscription subscription;
                    private int c = 0;
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.subscription = subscription;
                        request();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println(integer);
                        request();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                    }

                    private void request() {
                        boolean ok = (LocalDateTime.now().getSecond() % 10) > 5;
                        if (c++ < 2 || ok) {
                            this.subscription.request(1);
                        } else {
                            this.subscription.cancel();
                        }
                    }
                });
    }

    private void processor() {
        Flux.just(1,2,3)
                .concatWith(Flux.error(new Exception("oops")))
                .doOnEach(System.out::println)
                .subscribe(System.out::println);

        Flux.range(1,3)
                .materialize()
                .doOnEach(System.out::println)
                .dematerialize()
                .subscribe(System.out::println);
    }

    private void flatMap() {
        Flux.just("hello", "hi", "what", "how")
                .collectMultimap(x -> x.substring(0, 1), v -> v)
                .flux()
                .flatMap(x -> Flux.fromIterable(x.values()))
                .flatMap(x -> Flux.fromIterable(x))
                .subscribe(System.out::println);
    }

    private void reduceAndScan() {
        Flux.range(1, 5)
                .reduce(0, (a, b) -> a + b)
                .subscribe(System.out::println);

        Flux.range(1, 5)
                .scan(0, (a, b) -> a + b)
                .subscribe(System.out::println);
    }

    private void combining() {
        Flux.concat(Flux.range(2, 4),
                Flux.range(1,3)
        ).subscribe(System.out::println);
    }
}
