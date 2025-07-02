package inss.gca.mogi.mogi.config;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Gerenciador estático para controle de troca de cenas na aplicação JavaFX.
 * Permite definir a janela principal (Stage) e alternar cenas com efeito de loading.
 */
public class SceneManager {

    // Referência estática para a janela principal da aplicação
    private static Stage primaryStage;

    /**
     * Define a janela principal (Stage) da aplicação, necessária para troca de cenas.
     *
     * @param stage Instância do Stage principal da aplicação
     */
    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Troca a cena atual por outra especificada pelo caminho FXML, exibindo
     * antes uma tela de loading por 2 segundos.
     *
     * @param fxmlPath Caminho do arquivo FXML da nova cena
     * @param width Largura da nova cena em pixels
     * @param height Altura da nova cena em pixels
     */
    public static void switchWithLoading(String fxmlPath, int width, int height) {
        try {
            // Carrega a cena de loading para exibir durante a transição
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/Loading.fxml"));
            Parent loadingRoot = loader.load();
            primaryStage.setScene(new Scene(loadingRoot, 300, 150));  // Cena temporária de loading

            // Cria uma pausa de 2 segundos antes de carregar a nova cena
            PauseTransition pause = new PauseTransition(Duration.seconds(2));

            // Ao término da pausa, carrega e define a nova cena principal
            pause.setOnFinished(event -> {
                try {
                    FXMLLoader contentLoader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
                    Parent newRoot = contentLoader.load();
                    primaryStage.setScene(new Scene(newRoot, width, height));
                } catch (Exception e) {
                    // Imprime o stack trace para auxiliar no diagnóstico de erro durante o carregamento da cena
                    e.printStackTrace();
                }
            });

            pause.play(); // Inicia a pausa/tempo de loading

        } catch (Exception e) {
            // Captura e loga qualquer exceção ocorrida no processo de troca de cena
            e.printStackTrace();
        }
    }
}