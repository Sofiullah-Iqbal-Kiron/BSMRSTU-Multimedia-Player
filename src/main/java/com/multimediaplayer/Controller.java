package com.multimediaplayer;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.multimediaplayer.Utils.update_Text_message;

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

    @FXML
    private VBox playlist_container;

    Media media = new Media(getClass().getResource("video.mp4").toString());
    MediaPlayer mediaPlayer = new MediaPlayer(media);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(getClass().getResource("video.mp4").toString());

        String media_duration = media.getDuration().toString();
        media_view.setMediaPlayer(mediaPlayer);
        total_duration.setText(mediaPlayer.getTotalDuration().toString());
        total_duration.setText(media_duration);

        init_media_player();
        evaluate_duration_slider();
        evaluate_volume_slider();
        evaluate_playlist();
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
        } else if (status == Status.PLAYING) {
            mediaPlayer.pause();
        }
        alert_message.setText("Enjoy your music, cheers!");
    }

    @FXML
    protected void rewind_media(ActionEvent e) {
        if (mediaPlayer.getMedia() != null) {
            if (mediaPlayer.getStatus() == Status.PLAYING || mediaPlayer.getStatus() == Status.READY)
                mediaPlayer.pause();
            mediaPlayer.seek(new Duration(0));
            mediaPlayer.play();
        }
    }

    @FXML
    protected void mute_or_unmute(ActionEvent e) {
        boolean muted = mediaPlayer.isMute();
        mediaPlayer.setMute(!muted);
        media_mute_info.setText(muted ? "Media muted." : "Media on sound.");
    }

    @FXML
    protected void new_media_via_file_browser(ActionEvent e) {
        File file = new FileChooser().showOpenDialog(media_view.getScene().getWindow());
        if (file != null) {
            try {
                String filePath = file.toURI().toURL().toString();
                String fileName = file.getName();
                add_media_to_playlist(filePath, fileName);
                re_init_mediaPlayer(filePath);
            } catch (MalformedURLException exception) {
                exception.printStackTrace();
            }
        }
        e.consume();
    }

    @FXML
    protected void play_media(ActionEvent e) {
        if (mediaPlayer.getStatus() == Status.READY || mediaPlayer.getStatus() == Status.PAUSED) {
            mediaPlayer.play();
            current_status.setText("PLAYING...");
        }
    }

    @FXML
    protected void pause_media(ActionEvent e) {
        if (mediaPlayer.getStatus() == Status.READY || mediaPlayer.getStatus() == Status.PLAYING) {
            mediaPlayer.pause();
            current_status.setText("PAUSED");
        }
    }

    @FXML
    protected void increase_volume(ActionEvent e) {
        mediaPlayer.setVolume(mediaPlayer.getVolume() + 0.1);
    }

    @FXML
    protected void decrease_volume(ActionEvent e) {
        mediaPlayer.setVolume(mediaPlayer.getVolume() - 0.1);
    }

    @FXML
    protected void close_application(ActionEvent e) {
        Platform.exit();
    }

    protected void init_media_player() {
        mediaPlayer.setOnReady(() -> {
                    current_status.setText("READY");

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
        );

        mediaPlayer.setOnPlaying(() -> {
                    play_pause.setText("⏸");
                    current_status.setText("PLAYING...");
                }
        );

        mediaPlayer.setOnPaused(new Runnable() {
            @Override
            public void run() {
                play_pause.setText("▶");
                current_status.setText("PAUSED");
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

    protected void evaluate_playlist() {
        playlist_container.setOnDragOver(e -> {
            Dragboard dragboard = e.getDragboard();
            if (dragboard.hasFiles() || dragboard.hasUrl()) {
                e.acceptTransferModes(TransferMode.LINK);
            } else e.consume();
        });

        playlist_container.setOnDragDropped(e -> {
            Dragboard dragboard = e.getDragboard();
            String filePath = null;
            if (dragboard.hasFiles()) {
                if (dragboard.getFiles().size() > 0) {
                    try {
                        filePath = dragboard.getFiles().get(0).toURI().toURL().toString();
                        add_media_to_playlist(filePath, dragboard.getFiles().get(0).getName());
                    } catch (MalformedURLException exception) {
                        exception.printStackTrace();
                    }
                }
            }
            e.consume();
        });
    }

    protected void re_init_mediaPlayer(String filePath) {
        mediaPlayer.dispose();
        mediaPlayer = new MediaPlayer(new Media(filePath));
        init_media_player();
        media_view.setMediaPlayer(mediaPlayer);
    }

    protected void add_media_to_playlist(String filePath, String fileName) {
//        check that file is not duplicate
        ObservableList<Node> list = playlist_container.getChildren();
        for (Node n : list) {
            if (n instanceof MediaButton) {
                if (((MediaButton) n).getFilePath().equals(filePath)) {
//                    show an error popup
                    update_Text_message("Media already exists on this playlist.", alert_message);
                    return;
                }
            }
        }

        MediaButton newMedia = new MediaButton(filePath, fileName);
        newMedia.setOnMouseClicked(clickEvent -> {
            if (clickEvent.getClickCount() == 2) {
                re_init_mediaPlayer(newMedia.getFilePath());
            }
            clickEvent.consume();
        });
        playlist_container.getChildren().add(newMedia);
        update_Text_message("Media added to the playlist.", alert_message);
        re_init_mediaPlayer(filePath);
    }
}