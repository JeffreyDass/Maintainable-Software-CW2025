package com.comp2042.media;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Service for creating background music players.
 */
public class BackgroundMusicService {

    /**
     * Creates a looping music player for background music.
     *
     * @param resourcePath path to the audio file
     * @param volume volume level (0.0 to 1.0)
     * @return configured MediaPlayer
     */
    public static MediaPlayer createBgmPlayer(String resourcePath, double volume) {
        Media media = new Media(
                BackgroundMusicService.class.getResource(resourcePath).toExternalForm()
        );

        MediaPlayer player = new MediaPlayer(media);
        player.setCycleCount(MediaPlayer.INDEFINITE);
        player.setVolume(volume);
        return player;
    }
}