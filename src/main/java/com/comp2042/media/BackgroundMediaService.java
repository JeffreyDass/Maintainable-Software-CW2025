package com.comp2042.media;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Service for creating background video players.
 */
public class BackgroundMediaService {

    /**
     * Creates a looping video player for background effects.
     *
     * @param resourcePath path to the video file
     * @param volume volume level (0.0 to 1.0)
     * @return configured MediaPlayer
     */
    public static MediaPlayer createVideoPlayer(String resourcePath, double volume) {
        Media media = new Media(BackgroundMediaService.class
                .getResource(resourcePath)
                .toExternalForm());

        MediaPlayer player = new MediaPlayer(media);
        player.setCycleCount(MediaPlayer.INDEFINITE);
        player.setVolume(volume);

        return player;
    }
}