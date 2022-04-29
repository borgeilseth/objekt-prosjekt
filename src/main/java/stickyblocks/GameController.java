package stickyblocks;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.apache.commons.io.FilenameUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

public class GameController {

    private Game game;
    private String currentLevelName, currentLevelPath;

    private final SaveHandler saveHandler = new SaveHandler();

    private GameRenderer renderer;

    @FXML
    public Canvas canvas;
    public GraphicsContext gc;

    @FXML
    public Menu levels;

    @FXML
    public MenuItem quit, save;

    @FXML
    public Text textBox;

    @FXML
    private void initialize() {
        initContext();
        initEventHandlers();
        try {
            addLevelMenus("levels", levels);
        } catch (final URISyntaxException e) {
            e.printStackTrace();
        }
        renderer = new GameRenderer(gc);
    }

    // Add level buttons to the menu bar
    private void addLevelMenus(final String path, final Menu menu) throws URISyntaxException {
        final ObservableList<MenuItem> items = FXCollections.observableArrayList();
        menu.getItems().clear();

        // Collect the folder of the levels
        final String pathString = GameController.class.getResource(path).getFile();
        final File dir = new File(pathString);

        // Iterate over the files in the folder
        for (final File file : dir.listFiles()) {
            if (file.isDirectory()) {
                // if the file is a folder, add a submenu
                final Menu subMenu = new Menu(FilenameUtils.getBaseName(file.getName()));
                addLevelMenus(path + "/" + file.getName(), subMenu);
                if (!subMenu.getItems().isEmpty()) {
                    items.add(subMenu);
                    // menu.getItems().add(subMenu);
                }
            } else {
                // if the file is a level, add a button with the name of the level
                final String levelName = FilenameUtils.getBaseName(file.getName());
                final MenuItem item = new MenuItem(levelName);

                String levelPath;
                if (path == "levels") {
                    levelPath = "/" + levelName;
                } else {
                    // if the level is in a subfolder, add it to the submenu
                    levelPath = path.substring(7) + "/" + levelName;
                }
                // Set the id to the path of the level, and add the event handler
                item.setId(levelPath);
                item.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent event) {
                        loadGameButtonPress(event);
                    }
                });

                // Add a separator between the levels and the submenus
                try {
                    saveHandler.load(item.getId());
                } catch (final Exception e) {
                    System.err.println("Could not load level: " + item.getId());
                    continue;
                }
                items.add(item);
            }
        }

        // Sort menu items such that the standard levels are first in alphabetical order
        // menu.getItems().sort((MenuItem o1, MenuItem o2) -> {
        items.sort((final MenuItem o1, final MenuItem o2) -> {
            // Sort menuitems before menus (submenus), and then alphabetically
            if (o1 instanceof Menu && o2 instanceof Menu) {
                return o1.getText().compareTo(o2.getText());
            } else if (o1 instanceof Menu) {
                return 1;
            } else if (o2 instanceof Menu) {
                return -1;
            } else {
                if (o1.getText().length() > o2.getText().length()) {
                    return 1;
                } else if (o1.getText().length() < o2.getText().length()) {
                    return -1;
                } else {
                    return o1.getText().compareTo(o2.getText());
                }
            }
        });

        int index = 0;
        for (final MenuItem item : items) {
            if (item instanceof Menu) {
                items.add(index, new SeparatorMenuItem());
                break;
            }
            index++;
        }
        menu.getItems().addAll(items);
    }

    // Initialize the graphics context
    private void initContext() {
        gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
    }

    private void initEventHandlers() {
        canvas.setOnKeyPressed(handleKeyPressed);
    }

    // Load a level when the a menu item is pressed
    public void loadGameButtonPress(final ActionEvent e) {
        GameRenderer.getLevelColors().clear();
        final String fileName = ((MenuItem) e.getSource()).getId();
        loadGame(fileName);
        // levels.getItems().clear();
        try {
            addLevelMenus("levels", levels);
        } catch (final URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    // Load a level from a filepath
    public void loadGame(final String filename) {
        try {
            this.game = saveHandler.load(filename);
        } catch (final FileNotFoundException exception) {
            System.err.println("Savefile not found\n" + exception.getLocalizedMessage());
            return;
        }

        renderer.loadGame(game);
        renderer.start();
        canvas.requestFocus();
        currentLevelPath = filename;
        currentLevelName = FilenameUtils.getBaseName(filename);
        textBox.setText(currentLevelName);

    }

    // Save the current game to the save folder
    @FXML
    public void handleSave(final ActionEvent e) {
        try {
            saveHandler.save("saves/" + currentLevelName, game);
            System.out.println("Saved File");
            addLevelMenus("levels", levels);
        } catch (final FileNotFoundException exception) {
            System.err.println("Could not save file");
        } catch (final URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    // Quit the game
    @FXML
    public void handleQuit(final ActionEvent e) {
        System.exit(0);
    }

    // Key pressed event handler
    private final EventHandler<KeyEvent> handleKeyPressed = new EventHandler<KeyEvent>() {
        @Override
        public void handle(final KeyEvent e) {

            if (game == null) {
                return;
            }

            switch (e.getCode()) {
                case RIGHT:
                case D:
                    if (game.moveRight())
                        renderer.moveRight();
                    break;
                case UP:
                case W:
                    if (game.moveUp())
                        renderer.moveUp();
                    break;
                case DOWN:
                case S:
                    if (game.moveDown())
                        renderer.moveDown();
                    break;
                case LEFT:
                case A:
                    if (game.moveLeft())
                        renderer.moveLeft();
                    break;
                case R:
                    loadGame(currentLevelPath);
                    break;
                default:
                    break;

            }
            if (game != null && game.isWin()) {
                textBox.setText("You win!");
            }
        }
    };
}
