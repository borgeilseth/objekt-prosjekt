package stickyblocks;


import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class GameController {

    private Game game1 = new Game(17, 11);
    private Game game2 = new Game(5, 5);
    private Game game;

    private GameRenderer renderer;

    @FXML
    public Canvas canvas;

    public GraphicsContext gc;

    @FXML
    public MenuItem level1, level2, level3, level4, level5;

    @FXML
    private void initialize() {

        initContext();

        initGame(17, 11);

        initEventHandlers();

        renderer = new GameRenderer(gc);

    }

    private void initContext() {
        gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
    }

    private void initEventHandlers() {
        canvas.setOnKeyPressed(handleKeyPressed);
    }

    private void initGame(int x, int y) {

        game1 = new Game(x, y);

        game1.createPlayer(2, 3);
        game1.getPlayers().get(0).setActive();

        game1.createPlayer(5, 1);
        game1.createPlayer(7, 8);
        game1.createPlayer(9, 2);

        game1.setType(3, 0, "w");
        game1.setType(3, 1, "w");
        game1.setType(3, 2, "w");
        game1.setType(3, 3, "w");
        game1.setType(3, 4, "w");
        game1.setType(3, 6, "w");
        game1.setType(13, 2, "w");
        game1.setType(12, 2, "w");
        game1.setType(12, 3, "w");

        game1.setType(5, 5, "h");
        game1.setType(5, 6, "h");
        game1.setType(5, 7, "h");
        game1.setType(6, 5, "h");
        game1.setType(6, 6, "h");
        game1.setType(6, 7, "h");
        game1.setType(7, 5, "h");
        game1.setType(7, 6, "h");
        game1.setType(7, 7, "h");

        game1.setType(10, 6, "g");
        game1.setType(11, 6, "g");
        game1.setType(12, 6, "g");

        game1.setType(14, 8, "f");

        game2.createPlayer(0, 0);
        game2.createPlayer(2, 0);
        game2.createPlayer(0, 2);
        game2.getPlayers().get(0).setActive();

        game2.setType(5, 5, "g");
        game2.setType(4, 5, "w");
        game2.setType(3, 5, "g");
        game2.setType(3, 0, "h");

    }

    private void loadLevel(Game game) {
        this.game = game;
        renderer.loadGame(game);
        renderer.start();
        canvas.requestFocus();
    }

    @FXML
    public void handleMenuPress(ActionEvent e) {
        MenuItem target = (MenuItem) e.getSource();

        switch (target.getParentMenu().getId()) {
            case "levelMenu":
                System.out.println("Level button press");
                System.out.println(target.getId());
                if (target.getId().equals("1")) {
                    loadLevel(game1);
                } else if (target.getId().equals("2")) {
                    loadLevel(game2);
                }

                break;

            case "customLevelMenu":

                break;
            default:
                break;
        }

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
