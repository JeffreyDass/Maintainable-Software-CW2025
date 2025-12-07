package com.comp2042.model;

/**
 * Indicates where a game event originated from.
 * USER = player input, THREAD = automatic game tick.
 */
public enum EventSource {
    /** Event triggered by player input */
    USER,

    /** Event triggered by game timer */
    THREAD
}