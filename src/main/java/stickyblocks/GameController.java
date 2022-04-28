package stickyblocks;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.apache.commons.io.FilenameUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyEvent;

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
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        renderer = new GameRenderer(gc);

    }

    private void addLevelMenus(String path, Menu menu) throws URISyntaxException {
        String pathString = GameController.class.getResource(path).getFile();

        File dir = new File(pathString);

        for (File file : dir.listFiles()) {

            if (file.isDirectory()) {
                Menu subMenu = new Menu(FilenameUtils.getBaseName(file.getName()));
                addLevelMenus(path + "/" + file.getName(), subMenu);
                if (!subMenu.getItems().isEmpty()) {
                    menu.getItems().add(subMenu);
                }
            } else {
                String levelName = FilenameUtils.getBaseName(file.getName());

                MenuItem item = new MenuItem(levelName);

                item.setId(path + "/" + file.getName());
                item.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        handleLoad(event);
                    }
                });

                try {
                    saveHandler.load(item.getId());
                } catch (Exception e) {
                    System.err.println("Could not load level: " + item.getId());
                    continue;
                }

                menu.getItems().add(item);
            }
        }
        game = null;

        // Sort menu items such that the standard levels are first
        menu.getItems().sort((MenuItem o1, MenuItem o2) -> {
            return o1.getText().compareTo(o2.getText());
        });

        int index = 0;
        for (MenuItem item : menu.getItems()) {
            if (item instanceof Menu) {
                menu.getItems().add(index, new SeparatorMenuItem());
                break;
            }
            index++;
        }

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
        String id = ((MenuItem) e.getSource()).getId();

        try {
            this.game = saveHandler.load(id);
        } catch (FileNotFoundException exception) {
            System.err.println("Savefile not found\n" + exception.getLocalizedMessage());
            return;
        }

        renderer.loadGame(game);
        renderer.start();
        canvas.requestFocus();

        currentLevel = ((MenuItem) e.getSource()).getText();

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
