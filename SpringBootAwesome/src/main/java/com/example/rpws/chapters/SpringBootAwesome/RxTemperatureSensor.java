package com.example.rpws.chapters.SpringBootAwesome;


import io.reactivex.Observable;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class RxTemperatureSensor {
    private final Random random = new Random(System.currentTimeMillis());

    private final Observable<Temperature> dataStream = Observable.range(0, Integer.MAX_VALUE)
            .concatMap(tick ->
                    Observable.just(tick)
                            .delay(random.nextInt(3000), TimeUnit.MILLISECONDS)
                            .map(delay -> probe() )
            ).publish()
            .refCount();

    public Observable<Temperature> getDataStream() {
        return dataStream;
    }

    private Temperature probe() {
        return new Temperature(16 + random.nextGaussian() * 10);
    }
}
