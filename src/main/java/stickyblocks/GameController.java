package stickyblocks;


import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;

public class GameController {

    private Game game;

    private GameRenderer renderer;

    @FXML
    public Canvas canvas;

    public GraphicsContext gc;

    @FXML
    private void initialize() {

        initContext();

        // TODO: Create function, load game.
        initGame(17, 11);

        // TODO: Create function for defining handlers of fxml objects
        initEventHandlers();

        // Start the renderer
        renderer = new GameRenderer(game, gc);
        renderer.start();

    }

    private void initContext() {
        gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);

    }

    private void initEventHandlers() {

        canvas.setOnKeyPressed(handleKeyPressed);

    }

    private void initGame(int x, int y) {

        game = new Game(x, y);

        game.createPlayer(2, 3);
        game.getPlayers().get(0).setActive();

        game.createPlayer(5, 1);
        game.createPlayer(7, 8);
        game.createPlayer(9, 2);

        game.setType(3, 0, "w");
        game.setType(3, 1, "w");
        game.setType(3, 2, "w");
        game.setType(3, 3, "w");
        game.setType(3, 4, "w");
        game.setType(3, 6, "w");
        game.setType(13, 2, "w");
        game.setType(12, 2, "w");
        game.setType(12, 3, "w");

        game.setType(5, 5, "h");
        game.setType(5, 6, "h");
        game.setType(5, 7, "h");
        game.setType(6, 5, "h");
        game.setType(6, 6, "h");
        game.setType(6, 7, "h");
        game.setType(7, 5, "h");
        game.setType(7, 6, "h");
        game.setType(7, 7, "h");


        game.setType(10, 6, "g");
        game.setType(11, 6, "g");
        game.setType(12, 6, "g");

        game.setType(14, 8, "f");

    }

    private EventHandler<KeyEvent> handleKeyPressed = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent e) {
            switch (e.getCode()) {
                case RIGHT:
                case D:
                    game.moveRight();
                    break;

                case UP:
                case W:
                    game.moveUp();
                    break;

                case DOWN:
                case S:
                    game.moveDown();
                    break;

                case LEFT:
                case A:
                    game.moveLeft();
                    break;

                default:
                    break;

            }
        }
    };


}
