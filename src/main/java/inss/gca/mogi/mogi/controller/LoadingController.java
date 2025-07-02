package inss.gca.mogi.mogi.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

public class LoadingController {
    @FXML
    private ProgressBar progressBar;

    private Runnable onFinished;

    @FXML
    public void initialize() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> progressBar.setProgress(0)),
                new KeyFrame(Duration.seconds(3), event -> {
                    progressBar.setProgress(1);
                    if (onFinished != null) {
                        onFinished.run();
                    }
                })
        );
        timeline.setCycleCount(1);
        timeline.play();
    }

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }
}
