package com.multimediaplayer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Button stop, play_pause;

    @FXML
    private Text current_status;

    @FXML
    private MediaView media_view;

    @FXML
    private Slider duration, volume;

    Media media = new Media(getClass().getResource("video.mp4").toString());
    MediaPlayer mediaPlayer = new MediaPlayer(media);

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        media_view.setMediaPlayer(mediaPlayer);
        mediaPlayer.setOnReady(mediaPlayer::play);
    }

    @FXML
    protected void stop_media(ActionEvent e) {
        if (!mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)) {
            mediaPlayer.stop();
            update_status();
        }
    }

    @FXML
    protected void play_pause_media(ActionEvent e) {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            play_pause.setText("▶");
            mediaPlayer.pause();
            update_status();
        } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            play_pause.setText("⏸");
            mediaPlayer.play();
            update_status();
        }
    }

    @FXML
    protected void halve_opacity(ActionEvent e) {
        media_view.setOpacity(0.5);
    }

    @FXML
    protected void full_opacity(ActionEvent e) {
        media_view.setOpacity(1.0);
    }

    @FXML
    protected void mute_or_unmute(ActionEvent e) {
        mediaPlayer.setMute(!mediaPlayer.isMute());
    }

    private void update_status() {
        current_status.setText(mediaPlayer.getStatus().toString());
    }
}
