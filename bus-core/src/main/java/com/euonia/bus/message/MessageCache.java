package com.euonia.bus.message;

import com.euonia.bus.annotation.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MessageCache {
    private static volatile MessageCache instance;
    private static final Object lock = new Object();

    public static MessageCache getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new MessageCache();
                }
            }
        }
        return instance;
    }

    private final ConcurrentMap<Class<?>, String> channels = new ConcurrentHashMap<>();

    public String getOrAddChannel(Class<?> messageType) {
        return channels.computeIfAbsent(messageType, key -> {
            var annotation = messageType.getAnnotation(Channel.class);
            return annotation == null ? messageType.getName() : annotation.value();
        });
    }
}
