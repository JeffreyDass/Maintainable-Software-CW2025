package com.comp2042.model;

/**
 * Represents a movement event in the game.
 * Contains the type of movement and its source.
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    /**
     * Creates a new movement event.
     *
     * @param eventType the type of movement (down, left, right, rotate)
     * @param eventSource where the event came from (user or game thread)
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /**
     * Gets the type of movement.
     *
     * @return the event type
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Gets the source of the event.
     *
     * @return the event source
     */
    public EventSource getEventSource() {
        return eventSource;
    }
}