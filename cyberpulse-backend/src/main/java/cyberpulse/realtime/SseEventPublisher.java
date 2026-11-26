package cyberpulse.realtime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages active Server-Sent Events (SSE) subscriptions for real-time SOC dashboard updates.
 */
@Component
@Slf4j
public class SseEventPublisher {

    private static final long EMITTER_TIMEOUT_MS = 30 * 60 * 1000L;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MS);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(ex -> emitters.remove(emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException ex) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    public void publish(String eventName, Object payload) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name(eventName)
                                .data(payload, MediaType.APPLICATION_JSON)
                );
            } catch (Exception ex) {
                emitters.remove(emitter);
                log.debug("Removed disconnected SSE client: {}", ex.getMessage());
            }
        }
    }
}
