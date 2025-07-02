package inss.gca.mogi.mogi;

import inss.gca.mogi.mogi.config.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ViewApp extends Application {

    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.UNDECORATED);
        SceneManager.setStage(stage);
        SceneManager.switchWithLoading("/Login.fxml", 395, 350); // primeira tela
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}