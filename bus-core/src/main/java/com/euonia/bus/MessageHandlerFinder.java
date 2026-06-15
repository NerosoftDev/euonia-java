package com.euonia.bus;

import com.euonia.bus.annotation.Subscribe;
import com.euonia.bus.message.MessageCache;
import com.euonia.reflection.ClassScanner;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageHandlerFinder {

    public static List<HandlerRegistration> find(String... packages) {
        List<HandlerRegistration> registrations = new ArrayList<>();
        for (var pkg : packages) {
            var classes = ClassScanner.scan(pkg);
            for (var cls : classes) {
                registrations.addAll(resolve(cls));
            }
        }
        return registrations;
    }

    public static List<HandlerRegistration> find(Class<?>... handlerTypes) {
        List<HandlerRegistration> registrations = new ArrayList<>();
        for (var handlerType : handlerTypes) {
            registrations.addAll(resolve(handlerType));
        }
        return registrations;
    }

    private static List<HandlerRegistration> resolve(Class<?> handlerType) {
        if (handlerType.isInterface() || handlerType.isRecord() || handlerType.isEnum() || handlerType.isArray()) {
            return List.of();
        }
        List<HandlerRegistration> registrations = new ArrayList<>();

        var methods = Arrays.stream(handlerType.getDeclaredMethods())
                            .filter(m -> m.getAnnotation(Subscribe.class) != null)
                            .toList();

        if (!methods.isEmpty()) {
            for (var method : methods) {
                var parameters = method.getParameters();
                if (parameters.length < 1) {
                    throw new IllegalArgumentException("Method " + method.getName() + " in class " + handlerType.getName() + " must have at least one parameter.");
                }
                var firstParam = parameters[0];
                if (firstParam.getType().isPrimitive()) {
                    throw new IllegalArgumentException("First parameter of method " + method.getName() + " in class " + handlerType.getName() + " cannot be a primitive type.");
                }
                if (parameters.length == 2) {
                    if (!MessageContext.class.isAssignableFrom(parameters[1].getType())) {
                        throw new IllegalArgumentException("Second parameter of method " + method.getName() + " in class " + handlerType.getName() + " must be of type MessageContext or its subtype.");
                    }
                }
                var annotation = method.getAnnotation(Subscribe.class);
                var channel = annotation.value();
                if (channel == null) {
                    channel = MessageCache.getInstance().getOrAddChannel(firstParam.getType());
                }

                var registration = new HandlerRegistration(channel, firstParam.getType(), handlerType, method);
                registrations.add(registration);
            }
        }

        var interfaces = Arrays.stream(handlerType.getGenericInterfaces())
                               .filter(i -> i instanceof ParameterizedType pt && pt.getRawType() == Handler.class)
                               .toList();

        if (!interfaces.isEmpty()) {

            for (var genericInterface : interfaces) {
                var parameterizedType = (ParameterizedType) genericInterface;
                var messageType = parameterizedType.getActualTypeArguments()[0];
                try {
                    var method = handlerType.getMethod("handle", (Class<?>) messageType, MessageContext.class);
                    var channel = MessageCache.getInstance().getOrAddChannel((Class<?>) messageType);
                    var registration = new HandlerRegistration(channel, (Class<?>) messageType, handlerType, method);
                    registrations.add(registration);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return registrations;
    }
}
