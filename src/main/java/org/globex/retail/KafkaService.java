package org.globex.retail;


import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class KafkaService {

    @Inject
    @Channel("order-event")
    Emitter<String> emitter;

    public void emit(String payload) {
        emitter.send(toMessage(payload));
    }

    private Message<String> toMessage(String payload) {
        return Message.of(payload);
    }
}
