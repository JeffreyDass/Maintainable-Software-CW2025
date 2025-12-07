package com.comp2042.model;

/**
 * Data returned when a brick moves down.
 * Contains information about cleared rows and updated view state.
 */
public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;

    /**
     * Creates a new DownData object.
     *
     * @param clearRow information about any rows that were cleared (can be null)
     * @param viewData the current view state for rendering
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    /**
     * Gets the clear row information.
     *
     * @return ClearRow object, or null if no rows were cleared
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Gets the view data for rendering.
     *
     * @return current ViewData
     */
    public ViewData getViewData() {
        return viewData;
    }
}