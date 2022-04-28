package stickyblocks;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;

import org.apache.commons.io.FilenameUtils;

public class GameController {

    private Game game;
    private String currentLevel = "level";

    private SaveHandler saveHandler = new SaveHandler();

    private GameRenderer renderer;

    @FXML
    public Canvas canvas;

    public GraphicsContext gc;

    @FXML
    public Menu levels, customLevels;

    @FXML
    private void initialize() {

        initContext();

        initEventHandlers();

        try {
            addLevelMenus("levels", levels);
            System.out.println("\n");
            addLevelMenus("levels/customLevels", customLevels);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        renderer = new GameRenderer(gc);

    }

    private void addLevelMenus(String path, Menu menu) throws URISyntaxException {

        URI pathString = GameController.class.getResource(path).toURI();
        System.out.println(pathString);

        File dir = new File(pathString);

        int i = 1;
        for (File file : dir.listFiles()) {

            if (file.isDirectory())
                continue;

            System.out.println(file);

            String fileName = FilenameUtils.removeExtension(file.getName());

            MenuItem menuItem = new MenuItem(fileName);
            menuItem.setOnAction(e -> {
                handleLoad(e);
            });

            menu.getItems().add(i - 1, menuItem);

            i++;
        }

        // for (File file : dir.listFiles()) {
        // if (file.isDirectory()) {
        // System.out.println("Directory: " + file.getAbsolutePath());
        // } else {
        // System.out.println("File: " + file.getAbsolutePath());
        // }
        // }

    }

    private void initContext() {
        gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
    }

    private void initEventHandlers() {
        canvas.setOnKeyPressed(handleKeyPressed);
    }

    public void handleLoad(ActionEvent e) {
        GameRenderer.getLevelColors().clear();
        String level = ((MenuItem) e.getSource()).getText();
        loadGame(level);
    }

    private void loadGame(String level) {
        try {
            this.game = saveHandler.load(level);

        } catch (FileNotFoundException exception) {
            System.err.println("Savefile not found\n" + exception.getLocalizedMessage());
            return;
        }

        renderer.loadGame(game);
        renderer.start();
        canvas.requestFocus();

        currentLevel = level;
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
                case R:
                    loadGame(currentLevel);
                    break;
                default:
                    break;

            }
        }
    };

}
