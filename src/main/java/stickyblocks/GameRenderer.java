package stickyblocks;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import java.util.stream.Stream;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
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

    public GameRenderer(Game game, GraphicsContext gc) {

        loadAssets();

        this.game = game;
        this.gc = gc;

        this.h = gc.getCanvas().getHeight();
        this.w = gc.getCanvas().getWidth();
        this.tileSize = Math.floor(Math.min(w / game.getWidth(), h / game.getHeight()));

    }

    @Override
    public void handle(long now) {
        draw();

        delta = now - lastFrameTime;
        lastFrameTime = now;
        frameCount++;

    }

    public double getFrameRateHertz() {
        double frameRate = 1d / delta;
        return frameRate * 1e9;
    }

    private void draw() {
        Tile[][] state = game.getBoard();
        gc.setFill(Color.web("#080808"));
        gc.fillRect(0, 0, w, h);

        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                Double x = j * tileSize;
                Double y = i * tileSize;
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

        gc.drawImage(img, x, y, tileSize, tileSize);

        gc.setGlobalBlendMode(BlendMode.MULTIPLY);
        gc.setFill(color);
        gc.fillRect(x, y, tileSize, tileSize);
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);

    }

    private void drawTile(Tile tile, double x, double y) {
        String img = "";
        Color color = Color.WHITE;

        switch (tile.getType()) {
            case "w":
                img = bitMasking(tile, "wall");
                color = Color.web("#293141");
                break;
            case "h":
                img = bitMasking(tile, "water");
                color = Color.web("#5f9dd1");
                break;
            case "g":
                img = "goal_0";
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
        Color color = Color.WHITESMOKE;

        if (tile.getPlayer().isActive()) {
            img = "keke_0";
            color = Color.web("0x5c8339");
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
     * <ul>
     * <li>North West = 2⁰ = 1</li>
     * <li>North = 2¹ = 2</li>
     * <li>North East = 2² = 4</li>
     * <li>West = 2³ = 8</li>
     * <li>East = 2⁴ = 16</li>
     * <li>South West = 2⁵ = 32</li>
     * <li>South= 2⁶ = 64</li>
     * <li>South East = 2⁷ = 128</li>
     * </ul>
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
