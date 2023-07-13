package com.multimediaplayer;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Button stop, play_pause;

    @FXML
    private Text media_info, current_status, alert_message, media_mute_info;

    @FXML
    private MediaView media_view;

    @FXML
    private Slider duration_slider, volume;

    @FXML
    private Label duration_count, total_duration, hours_label, minutes_label, seconds_label;

    Media media = new Media(getClass().getResource("video.mp4").toString());
    MediaPlayer mediaPlayer = new MediaPlayer(media);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String media_duration = media.getDuration().toString();
        media_view.setMediaPlayer(mediaPlayer);
        total_duration.setText(mediaPlayer.getTotalDuration().toString());
        total_duration.setText(media_duration);

        init_media_player();
        evaluate_duration_slider();
        evaluate_volume_slider();
    }

    @FXML
    protected void stop_media(ActionEvent e) {
        if (mediaPlayer != null) {
            if (!mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                mediaPlayer.stop();
                current_status.setText("STOPPED");
                mediaPlayer.seek(new Duration(0));
                media_view.setOpacity(1);
                duration_slider.adjustValue(0);
            }
        }
//        exit from platform after few seconds
//        Platform.exit();
    }

    @FXML
    protected void play_pause_media(ActionEvent e) {
        Status status = mediaPlayer.getStatus();
        if (status == Status.UNKNOWN || status == Status.HALTED) return;
        if (status == Status.PAUSED || status == Status.READY) {
            mediaPlayer.play();
            current_status.setText("PLAYING...");
        } else if (status == Status.PLAYING) {
            mediaPlayer.pause();
            current_status.setText("PAUSED");
        }
        alert_message.setText("Enjoy your music, cheers!");
    }

    @FXML
    protected void mute_or_unmute(ActionEvent e) {
        boolean muted = mediaPlayer.isMute();
        mediaPlayer.setMute(!muted);
        media_mute_info.setText(muted ? "Media muted." : "Media on sound.");
    }

    protected void init_media_player() {
        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration duration = mediaPlayer.getMedia().getDuration();
                double seconds = duration.toSeconds();
                int hours = (int) (seconds / 3600);
                int minutes = (int) (seconds % 3600) / 60;

                hours_label.setText(String.format("%02d hours", hours));
                minutes_label.setText(String.format("%02d minutes", minutes));
                seconds_label.setText(String.format("%02d seconds", (int) seconds % 60));

                duration_slider.setMax(duration.toMillis());
                total_duration.setText(String.format("%02d:%02d:%02d", hours, minutes, (int) seconds % 60));
                mediaPlayer.play();
            }
        });

        mediaPlayer.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                play_pause.setText("⏸");
            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            @Override
            public void run() {
                play_pause.setText("▶");
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                update_duration_count();
                duration_slider.valueProperty().set(mediaPlayer.getCurrentTime().toMillis());
            }
        });
    }

    protected void evaluate_duration_slider() {
        duration_slider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (duration_slider.isValueChanging()) {
                    mediaPlayer.pause();
                    Duration currentDur = new Duration(duration_slider.getValue());
                    duration_count.setText(mediaPlayer.getCurrentTime().toString());
                    mediaPlayer.seek(currentDur);
                    update_duration_count();
                    mediaPlayer.play();
                }
            }
        });

        duration_slider.setOnMouseClicked(e -> {
            mediaPlayer.pause();
            Duration currentDur = new Duration(duration_slider.getValue());
            mediaPlayer.seek(currentDur);
            update_duration_count();
            mediaPlayer.play();
        });
    }

    protected void evaluate_volume_slider() {
        volume.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (volume.isValueChanging()) {
                    mediaPlayer.setVolume(volume.getValue() / 100.0);
                }
            }
        });

        volume.setOnMouseClicked(e -> {
            mediaPlayer.setVolume(volume.getValue() / 100.0);
        });
    }

    protected void update_duration_count() {
        Duration duration = mediaPlayer.getCurrentTime();
        double seconds = duration.toSeconds();
        int hours = (int) (seconds / 3600);
        int minutes = (int) (seconds % 3600) / 60;
        duration_count.setText(String.format("%02d:%02d:%02d", hours, minutes, (int) seconds % 60));
    }
}