package stickyblocks;

import java.nio.IntBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

public class GameRenderer extends AnimationTimer {

    private long delta;
    private long lastFrameTime;
    private long frameCount = 0;

    private Game game;
    private GraphicsContext gc;

    private double h, w;
    private double xOff, yOff;
    private double tileSize;

    private static String imgFolderPath;

    private static HashMap<String, Image> imageFiles = new HashMap<>();

    private final static Map<String, Color> defaultColors = Stream.of(new Object[][] {
            { "bg", Color.web("#080808") },
            { "w", Color.web("#293141") },
            { "h", Color.web("#5f9dd1") },
            { "g", Color.web("#d9396a") },
            { "p", Color.web("#5c8339") }
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Color) data[1]));

    public GameRenderer(GraphicsContext gc) {
        loadAssets();
        this.gc = gc;

        this.h = gc.getCanvas().getHeight();
        this.w = gc.getCanvas().getWidth();
    }

    @Override
    public void handle(long now) {

        if (game == null) {
            this.stop();
            return;
        }

        draw();

        delta = now - lastFrameTime;
        lastFrameTime = now;
        frameCount++;
    }

    public void loadGame(Game game) {
        gc.clearRect(0, 0, w, h);
        this.game = game;

        this.tileSize = Math.floor(Math.min(w / game.getWidth(), h / game.getHeight()));

        xOff = (w - (tileSize * game.getWidth())) / 2;
        yOff = (h - (tileSize * game.getHeight())) / 2;

    }

    public double getFrameRateHertz() {
        double frameRate = 1d / delta;
        return frameRate * 1e9;
    }

    private void draw() {
        Tile[][] state = game.getBoard();
        gc.setFill(defaultColors.get("bg"));
        gc.fillRect(xOff, yOff, w - (2 * xOff), h - (2 * yOff));

        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                Double x = j * tileSize + xOff;
                Double y = i * tileSize + yOff;
                Tile tile = state[i][j];

                drawTile(tile, x, y);
            }
        }

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

    }

    private void drawImage(String fileName, Color color, double x, double y) {
        if (fileName == "")
            return;

        int animationNumber = ((int) frameCount / 9) % 3 + 1;
        Image img = imageFiles.get(fileName + "_" + animationNumber);

        img = reColor(img, color);

        gc.drawImage(img, x, y, tileSize, tileSize);

    }

    public static Image reColor(Image inputImage, Color newColor) {
        int W = (int) inputImage.getWidth();
        int H = (int) inputImage.getHeight();
        WritableImage outputImage = new WritableImage(W, H);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        int nb = (int) (newColor.getBlue() * 255);
        int nr = (int) (newColor.getRed() * 255);
        int ng = (int) (newColor.getGreen() * 255);

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {

                int argb = reader.getArgb(x, y);
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                r = r * nr / 255;
                b = b * nb / 255;
                g = g * ng / 255;

                argb = (a << 24) | (r << 16) | (g << 8) | b;
                writer.setArgb(x, y, argb);
            }
        }
        return outputImage;
    }

    private void drawTile(Tile tile, double x, double y) {
        String img = "";
        Color color = Color.GREY;

        switch (tile.getType()) {
            case "w":
                img = bitMasking(tile, "wall");
                color = defaultColors.get("w");
                break;
            case "h":
                img = bitMasking(tile, "water");
                color = defaultColors.get("h");
                break;
            case "g":
                img = "goal_0";
                color = defaultColors.get("g");
                break;
            case "f":
                // img = "fragile";
                break;
            default:
                break;
        }

        drawImage(img, color, x, y);

    }

    private void drawPlayer(Tile tile, double x, double y) {
        String img = "";
        Color color = defaultColors.get("p");

        if (tile.getPlayer().isActive()) {
            img = "keke_0";
        } else {
            img = "keke_15";
        }

        drawImage(img, color, x, y);
    }

    /**
     * Determines what picture a tile should have, determined by the surrounding
     * tiles.
     * <p>
     * Bithmasking determines the tile ID by counting what neigbours has tiles in
     * the following way
     * 
     * Each neighbour will add its value to a sum which then determines the tile
     * number.
     * 
     * Reference:
     * https://gamedevelopment.tutsplus.com/tutorials/how-to-use-tile-bitmasking-to-auto-tile-your-level-layouts--cms-25673
     * 
     * @param tile
     * @param type
     * @return File name of image to be drawn
     */
    private String bitMasking(Tile tile, String typeName) {

        Tile[][] board = game.getBoard();
        String type = tile.getType();
        String filename = typeName + "_";
        int n = 0, x = tile.getX(), y = tile.getY();

        /*
         * Returns the tile index after checking all 8 positions around the tile.
         * Includes corners.
         */

        int index, north_tile, south_tile, west_tile, east_tile;

        // Directional Check, including corners, returns Boolean
        north_tile = (getType(x, y - 1, board) == type) ? 1 : 0;

        south_tile = (getType(x, y + 1, board) == type) ? 1 : 0;
        west_tile = (getType(x + 1, y, board) == type) ? 1 : 0;
        east_tile = (getType(x - 1, y, board) == type) ? 1 : 0;

        index = west_tile + 2 * north_tile + 4 * east_tile + 8 * south_tile;

        return filename + index;
    }

    private String getType(int x, int y, Tile[][] board) {
        if (x < 0 || y < 0 || x >= board[0].length || y >= board.length) {
            return null;
        }
        return game.getBoard()[y][x].getType();
    }

    private void loadAssets() {
        imgFolderPath = "src/main/resources/stickyblocks/img/";

        try (Stream<Path> paths = Files.walk(Paths.get(imgFolderPath))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach((f) -> {
                        String filename = f.getFileName().toString();
                        Image img = new Image(
                                getClass().getResource("img/" + filename).toString());
                        imageFiles.put(filename.substring(0, filename.length() - 4), img);
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
