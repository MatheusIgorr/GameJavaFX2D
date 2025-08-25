package HollowKnightFX.src;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class Main extends Application {

    private Game game;
    private AnimationTimer gameLoop;

    @Override
    public void start(Stage primaryStage) {
        game = new Game();

        Scene scene = new Scene(game, 1000, 700);
        scene.setFill(Color.BLACK);

        // Configurar controles
        scene.setOnKeyPressed(game::handleKeyPress);
        scene.setOnKeyReleased(game::handleKeyRelease);

        primaryStage.setTitle("Hollow Knight - JavaFX Edition");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Iniciar o jogo
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                game.update();
            }
        };
        gameLoop.start();
    }

    @Override
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}