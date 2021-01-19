package com.example.rpws.chapters.SpringBootAwesome;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

@RestController
public class TemperatureController {

    @Autowired
    private RxTemperatureSensor rxTemperatureSensor;

    private static final Logger logger = LogManager.getLogger(TemperatureController.class);

    private final Set<SseEmitter> clients = new CopyOnWriteArraySet<>();

    @GetMapping("/temperature-stream")
    public SseEmitter events(HttpServletRequest request) {
        final SseEmitter emitter = new SseEmitter();
        clients.add(emitter);

        Runnable removeEmitter = new Runnable() {
            @Override
            public void run() {
                clients.remove(emitter);
                logger.info("remove emitter {}", emitter);
            }
        };
        emitter.onTimeout(removeEmitter);
        emitter.onError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                removeEmitter.run();
            }
        });
        emitter.onCompletion(removeEmitter);

        return emitter;
    }

    @Async
    @EventListener
    public void handleMessage(Temperature temperature) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        clients.forEach(sseEmitter -> {
            try {
                sseEmitter.send(temperature, MediaType.APPLICATION_JSON);
            } catch (Exception ex) {
                deadEmitters.add(sseEmitter);
            }
        });
        clients.removeAll(deadEmitters);
    }

    @GetMapping("/temperature-rx-stream")
    public SseEmitter rxEvents(HttpServletRequest request) {
            RxSseEmitter emitter = new RxSseEmitter();
            rxTemperatureSensor.getDataStream().subscribe(emitter.getObserver());

            return emitter;
    }
}
