package com.multimediaplayer;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.scene.paint.Color;

public class MediaButton extends MFXButton {
    private String filePath;

    public MediaButton(String filePath, String name) {
        this.filePath = filePath;
        this.setText(name);

        this.setRippleColor(Color.GRAY);
    }

    public String getFilePath() {
        return this.filePath;
    }
}
