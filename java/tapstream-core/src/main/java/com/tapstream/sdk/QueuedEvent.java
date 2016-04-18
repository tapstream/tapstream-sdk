package com.tapstream.sdk;

public class QueuedEvent {
    private final Event event;
    private final ApiFuture<EventApiResponse> responseFuture;

    public QueuedEvent(Event event, ApiFuture<EventApiResponse> responseFuture) {
        this.event = event;
        this.responseFuture = responseFuture;
    }

    public Event getEvent() {
        return event;
    }

    public ApiFuture<EventApiResponse> getResponseFuture() {
        return responseFuture;
    }
}
