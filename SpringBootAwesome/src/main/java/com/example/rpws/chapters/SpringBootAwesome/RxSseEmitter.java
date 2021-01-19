package com.example.rpws.chapters.SpringBootAwesome;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.function.Consumer;


public class RxSseEmitter extends SseEmitter {

    private final Observer<Temperature> observer;

    private static final Logger logger = LogManager.getLogger(RxSseEmitter.class);


    public RxSseEmitter() {
        super(10 * 60 * 1000L);

        this.observer = new Observer<Temperature>() {
            private volatile boolean stop = false;

            public void onSubscribe(Disposable subscription) {
                logger.info("onSubscribe {}", subscription);

                RxSseEmitter.this.onCompletion(()->{
                    stop = true;
                    logger.info("onCompletion");
                });

                RxSseEmitter.this.onTimeout(()->{
                    stop = true;
                    logger.info("onTimeout");
                });

                RxSseEmitter.this.onError(t->{
                    stop = true;
                    logger.error("onError {}", t.getMessage());
                });
            }

            @Override
            public void onNext(Temperature temperature) {
                if (stop) {
                    return;
                }
                try {
                    send(temperature);
                } catch (IOException ignore) {}
            }

            @Override
            public void onError(Throwable throwable) {
                stop = true;
                logger.error("onError", throwable);
            }

            @Override
            public void onComplete() {
                stop = true;
                logger.info("onComplete");
            }
        };

    }

    public Observer<Temperature> getObserver() {
        return observer;
    }
}
