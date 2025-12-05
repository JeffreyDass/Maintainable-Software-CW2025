package com.comp2042.media;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class BackgroundMusicService {

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
