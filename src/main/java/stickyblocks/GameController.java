package stickyblocks;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class GameController {

    private Game game;

    @FXML
    public Canvas canvas;

    public GraphicsContext context;

    private double height, width;
    private double xOff, yOff;
    private double tileSize, pixelSize;

    private static String imgFolder;
    private static Image floorImg, goalImg, activePlayerImg, unactivePlayerImg, holeImg,
            fragileImg;

    private static HashMap<String, Image> wallImages = new HashMap<>();

    @FXML
    private void initialize() {
        loadAssets();
        initContext();
        initGame(17, 11);

        draw();
    }

    private void initContext() {
        context = canvas.getGraphicsContext2D();
        context.setImageSmoothing(false);

        height = canvas.getHeight();
        width = canvas.getWidth();

        System.out.println("Size: " + height + " " + width);

    }

    private void initGame(int x, int y) {
        tileSize = Math.floor(Math.min(width / (x + 1), height / (y + 1)));
        pixelSize = tileSize / 16;

        xOff = (width - (tileSize * (x + 1))) / 2;
        yOff = (height - (tileSize * (y + 1))) / 2;

        System.out.println(xOff + " " + yOff);

        System.out.println(tileSize);
        game = new Game(x, y);

        game.createPlayer(2, 3);
        game.getPlayers().get(0).setActive();

        // for (int i = 0; i < game.getBoard().length; i++) {
        // for (int j = 0; j < game.getBoard()[i].length; j++) {
        // if (i == 0 || i == game.getBoard().length - 1 || j == 0 || j ==
        // game.getBoard()[i].length - 1) {
        // game.setType(j, i, 'w');
        // }
        // }
        // }

        game.createPlayer(5, 1);
        game.createPlayer(7, 8);
        game.createPlayer(9, 2);

        game.setType(3, 0, 'w');
        game.setType(3, 1, 'w');
        game.setType(3, 2, 'w');
        game.setType(3, 3, 'w');
        game.setType(3, 4, 'w');
        game.setType(3, 6, 'w');
        game.setType(13, 2, 'w');
        game.setType(12, 2, 'w');
        game.setType(12, 3, 'w');

        game.setType(5, 6, 'h');

        game.setType(10, 6, 'g');
        game.setType(11, 6, 'g');
        game.setType(12, 6, 'g');

        game.setType(14, 8, 'f');

    }

    private void draw() {
        Tile[][] state = game.getBoard();
        context.setFill(Color.web("#080808"));
        context.fillRect(0, 0, width, height);

        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                Double x = j * tileSize + xOff;
                Double y = i * tileSize + yOff;
                Tile tile = state[i][j];

                drawTile(tile, x, y);
            }
        }

        context.setFill(Color.web("#080808"));
        context.fillRect(0, 0, width, height);

        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                Double x = j * tileSize + xOff;
                Double y = i * tileSize + yOff;
                Tile tile = state[i][j];

                if (tile.hasPlayer()) {
                    drawPlayer(tile, x, y);
                }
            }
        }

        System.out.println(game);
    }

    private void drawTile(Tile tile, double x, double y) {
        Image img;

        switch (tile.getType()) {
            case 'w':
                img = determineImageVersion(tile, "wall");
                break;
            case 'h':
                img = holeImg;
                break;
            case 'g':
                img = goalImg;
                break;
            case 'f':
                img = fragileImg;
                break;
            default:
                img = null;
                break;
        }
        if (img != null) {
            context.drawImage(img, x, y, tileSize, tileSize);
        }
    }

    public void drawPlayer(Tile tile, double x, double y) {
        Image img = null;

        if (tile.getPlayer().isActive()) {
            context.setFill(Color.PINK);
        } else {
            context.setFill(Color.RED);
        }

        context.fillRect(x, y, tileSize, tileSize);
        if (img != null) {
            context.drawImage(img, x, y, tileSize, tileSize);
        }
    }

    @FXML
    void handleKeyPressed(KeyEvent e) {
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

        draw();
    }

    private void loadAssets() {
        imgFolder = "file:" + GameController.class.getResource("img/").getFile();
        floorImg = new Image(imgFolder + "floor.png");
        goalImg = new Image(imgFolder + "goal.png");
        holeImg = new Image(imgFolder + "hole.png");
        fragileImg = new Image(imgFolder + "fragile.png");

        wallImages.put("wall_0", new Image(imgFolder + "wall/wall_0.png"));
        wallImages.put("wall_1", new Image(imgFolder + "wall/wall_1.png"));
        wallImages.put("wall_2", new Image(imgFolder + "wall/wall_2.png"));
        wallImages.put("wall_3", new Image(imgFolder + "wall/wall_3.png"));
        wallImages.put("wall_4", new Image(imgFolder + "wall/wall_4.png"));
        wallImages.put("wall_12", new Image(imgFolder + "wall/wall_12.png"));
        wallImages.put("wall_13", new Image(imgFolder + "wall/wall_13.png"));
        wallImages.put("wall_14", new Image(imgFolder + "wall/wall_14.png"));
        wallImages.put("wall_23", new Image(imgFolder + "wall/wall_23.png"));
        wallImages.put("wall_24", new Image(imgFolder + "wall/wall_24.png"));
        wallImages.put("wall_34", new Image(imgFolder + "wall/wall_34.png"));
        wallImages.put("wall_123", new Image(imgFolder + "wall/wall_123.png"));
        wallImages.put("wall_124", new Image(imgFolder + "wall/wall_124.png"));
        wallImages.put("wall_134", new Image(imgFolder + "wall/wall_134.png"));
        wallImages.put("wall_234", new Image(imgFolder + "wall/wall_234.png"));
        wallImages.put("wall_1234", new Image(imgFolder + "wall/wall_1234.png"));

    }

    /**
     * Vi har en rekke ulike vegger (og aktive spillere) som brukes i alle mulige
     * kombinasjoner hvor en
     * vegg har en nabovegg på sine ortogonale sider.
     * 
     * Dette gjør det mulig å lage finere vegger som er sammenhengende og har
     * grenser rundt brettet.
     * 
     * Veggens naboer defineres ved tall som også navngir filen.
     * 
     * ----1
     * ---4v2
     * ----3
     *
     * F.eks. wall_34 beskriver veggen som har en nabovegg på side 3 og 4.
     * wall_0 er en vegg uten naboer.
     */
    private Image determineImageVersion(Tile tile, String type) {
        String key = type + "_";
        int n = 0, i = -1, j = 0;

        while (n < 4) {
            n += 1;
            try {
                Tile neighborTile = game.getBoard()[tile.getY() + i][tile.getX() + j];
                if (neighborTile.getType() == 'w') {
                    key += n;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                key += n;
            }

            switch (n) {
                case 1:
                    i++;
                    j++;
                    break;
                case 2:
                    i++;
                    j--;
                    break;
                case 3:
                    i--;
                    j--;
                    break;
                default:
                    break;
            }
        }

        if (!isNumeric(key.substring(key.length() - 1))) {
            key += 0;
        }
        System.out.println(key);
        return wallImages.get(key);
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional '-' and decimal.
    }

}
