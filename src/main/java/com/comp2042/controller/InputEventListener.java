package com.comp2042.controller;

import com.comp2042.model.DownData;
import com.comp2042.model.MoveEvent;
import com.comp2042.model.ViewData;

/**
 * Interface for handling user input events during gameplay.
 * Defines methods for all possible player actions.
 */
public interface InputEventListener {

    /**
     * Handles downward movement events.
     *
     * @param event the movement event
     * @return data about cleared rows and current view state
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles leftward movement events.
     *
     * @param event the movement event
     * @return updated view data
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles rightward movement events.
     *
     * @param event the movement event
     * @return updated view data
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles rotation events.
     *
     * @param event the movement event
     * @return updated view data
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Handles hold action events.
     *
     * @return updated view data
     */
    ViewData onHoldEvent();

    /**
     * Handles hard drop events.
     *
     * @return data about cleared rows and current view state
     */
    DownData onHardDropEvent();

    /**
     * Creates a new game session.
     */
    void createNewGame();
}