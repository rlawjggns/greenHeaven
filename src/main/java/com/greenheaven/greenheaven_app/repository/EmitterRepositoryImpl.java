package com.greenheaven.greenheaven_app.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository {
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitterMap.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String emitterId, Object event) {
        eventCache.put(emitterId, event);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithEmail(String receiverEmail) {
        return emitterMap.entrySet().stream()
                .filter(e -> e.getKey().startsWith(receiverEmail))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventCacheStartWithEmail(String receiverEmail) {
        return eventCache.entrySet().stream()
                .filter(e -> e.getKey().startsWith(receiverEmail))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteById(String emitterId) {
        emitterMap.remove(emitterId);
    }

    @Override
    public void deleteAllEmitterStartWithEmail(String receiverEmail) {
        emitterMap.forEach(
                (key, emitter) -> {
                    if (key.startsWith(receiverEmail)) {
                        emitterMap.remove(key);
                    }
                });
    }

    @Override
    public void deleteAllEventCacheStartWithEmail(String receiverEmail) {
        eventCache.forEach(
                (key, event) -> {
                    if (key.startsWith(receiverEmail)) {
                        eventCache.remove(key);
                    }
                }
        );
    }
}
