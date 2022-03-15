package stickyblocks;


import java.io.FileNotFoundException;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class GameController {

    private Game game;
    private String currentLevel = "level";

    private SaveHandler saveHandler = new SaveHandler();

    private GameRenderer renderer;

    @FXML
    public Canvas canvas;

    public GraphicsContext gc;

    @FXML
    public MenuItem level1, level2, level3, level4, level5;

    @FXML
    private void initialize() {

        initContext();


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



    @FXML
    public void handleLoad(ActionEvent e) {

        GameRenderer.getLevelColors().clear();

        String name = ((MenuItem) e.getSource()).getText();

        try {
            this.game = saveHandler.load(name);
        } catch (FileNotFoundException exception) {
            System.err.println("Savefile not found\n" + exception.getLocalizedMessage());
            return;
        }

        renderer.loadGame(game);
        renderer.start();
        canvas.requestFocus();

        currentLevel = ((MenuItem) e.getSource()).getText();

        System.out.println(game);
    }

    @FXML
    public void handleSave(ActionEvent e) {
        try {
            saveHandler.save(currentLevel, game);
            System.out.println("Saved File");
        } catch (FileNotFoundException exception) {
            System.err.println("Could not save file");
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
