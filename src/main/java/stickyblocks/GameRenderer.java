package stickyblocks;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class GameRenderer extends AnimationTimer {

    private int moveDir = 0;

    private final HashMap<Player, Integer> playerFrameNum = new HashMap<Player, Integer>();

    private Game game;
    private final GraphicsContext gc;

    private final double h, w;
    private double xOff, yOff;
    private double tileSize;

    private static String imgFolderPath;

    private static HashMap<String, Image> imageFiles = new HashMap<>();
    private static HashMap<String, Color> levelColors = new HashMap<>();

    private final static Map<String, Color> defaultColors;

    static {
        defaultColors = new HashMap<>();
        defaultColors.put("bg", Color.web("#080808"));
        defaultColors.put("w", Color.web("#293141"));
        defaultColors.put("h", Color.web("#5f9dd1"));
        defaultColors.put("g", Color.web("#080808").invert().grayscale());
        defaultColors.put("p", Color.web("#5c8339"));
        defaultColors.put("f", Color.web("#d9d9d9"));
    }

    public GameRenderer(final GraphicsContext gc) {
        loadAssets();
        this.gc = gc;

        this.h = gc.getCanvas().getHeight();
        this.w = gc.getCanvas().getWidth();
    }

    @Override
    public void handle(final long now) {

        if (game == null) {
            this.stop();
            return;
        }

        draw();
    }

    public void loadGame(final Game game) {
        gc.clearRect(0, 0, w, h);
        this.game = game;

        this.tileSize = Math.floor(Math.min(w / game.getWidth(), h / game.getHeight()));

        xOff = (w - (tileSize * game.getWidth())) / 2;
        yOff = (h - (tileSize * game.getHeight())) / 2;

        for (final Player player : game.getPlayers()) {
            playerFrameNum.put(player, (int) (Math.random() * 4));
        }
    }

    private void draw() {
        final Tile[][] state = game.getBoard();
        gc.setFill(getColor("bg"));
        gc.fillRect(xOff, yOff, w - (2 * xOff), h - (2 * yOff));

        for (int y = 0; y < state.length; y++) {
            for (int x = 0; x < state[0].length; x++) {
                final Tile tile = state[y][x];
                drawTile(tile, x, y);
            }
        }

        for (int y = 0; y < state.length; y++) {
            for (int x = 0; x < state[0].length; x++) {
                final Tile tile = state[y][x];

                if (tile.hasPlayer()) {
                    drawPlayer(tile, x, y);
                }
            }
        }

    }

    private void drawImage(final String fileName, final Color color, final int x, final int y) {
        if (fileName.equals(""))
            return;

        // int animationNumber = (animationNumbers[y][x] + (frameCount / 9)) % 3 + 1;

        Image img = imageFiles.get(fileName);

        img = reColor(img, color);

        final Double xCoord = x * tileSize + xOff;
        final Double yCoord = y * tileSize + yOff;

        gc.drawImage(img, xCoord, yCoord, tileSize, tileSize);

    }

    private Image reColor(final Image inputImage, final Color newColor) {
        final int W = (int) inputImage.getWidth();
        final int H = (int) inputImage.getHeight();
        final WritableImage outputImage = new WritableImage(W, H);
        final PixelReader reader = inputImage.getPixelReader();
        final PixelWriter writer = outputImage.getPixelWriter();

        final int nb = (int) (newColor.getBlue() * 255);
        final int nr = (int) (newColor.getRed() * 255);
        final int ng = (int) (newColor.getGreen() * 255);

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {

                int argb = reader.getArgb(x, y);
                final int a = (argb >> 24) & 0xFF;
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

    private void drawTile(final Tile tile, final int x, final int y) {
        String img = "";
        final String type = tile.getType();
        final Color color = getColor(type);

        switch (type) {
            case "w":
                img = bitMasking(tile, "wall");
                break;
            case "h":
                img = bitMasking(tile, "hole");
                break;
            case "g":
                img = "goal_0";
                break;
            case "f":
                img = "fragile_0";
                break;
            default:
                break;
        }

        drawImage(img, color, x, y);
    }

    public void moveRight() {
        move(0);
    }

    public void moveUp() {
        move(1);
    }

    public void moveLeft() {
        move(2);
    }

    public void moveDown() {
        move(3);
    }

    private void move(final int moveDir) {
        this.moveDir = moveDir;

        for (final Player player : game.getPlayers()) {
            if (player.isActive())
                playerFrameNum.put(player, (playerFrameNum.get(player) + 1) % 4);
        }
    }

    private void drawPlayer(final Tile tile, final int x, final int y) {
        String img = "";
        final Color color = getColor("p");

        final Player player = tile.getPlayer();

        if (player.isActive()) {
            img = "player_" + (playerFrameNum.get(player) + 4 * moveDir);
        } else {
            img = "player_sit_" + (playerFrameNum.get(player) % 2);
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
    private String bitMasking(final Tile tile, final String typeName) {

        final Tile[][] board = game.getBoard();
        final String type = tile.getType();
        final String filename = typeName + "_";
        final int x = tile.getX(), y = tile.getY();

        /*
         * Returns the tile index after checking all 8 positions around the tile.
         * Includes corners.
         */

        int index, north_tile, south_tile, west_tile, east_tile;

        // Directional Check, including corners, returns Boolean
        north_tile = (getType(x, y - 1, board).equals(type)) ? 1 : 0;
        south_tile = (getType(x, y + 1, board).equals(type)) ? 1 : 0;
        west_tile = (getType(x + 1, y, board).equals(type)) ? 1 : 0;
        east_tile = (getType(x - 1, y, board).equals(type)) ? 1 : 0;

        index = west_tile + 2 * north_tile + 4 * east_tile + 8 * south_tile;

        return filename + index;
    }

    private String getType(final int x, final int y, final Tile[][] board) {
        if (x < 0 || x >= board[0].length) {
            final int normX = x < 0 ? 0 : board[0].length - 1;
            return board[y][normX].getType();
        } else if (y < 0 || y >= board.length) {
            final int normY = y < 0 ? 0 : board.length - 1;
            return board[normY][x].getType();
        }
        return game.getBoard()[y][x].getType();
    }

    public static void addColor(final String key, final Color color) {
        levelColors.put(key, color);
    }

    private Color getColor(final String color) {

        if (levelColors.containsKey("bg") && color.equals("g")) {
            return levelColors.get("bg").invert().grayscale();
        }

        return levelColors.getOrDefault(color, defaultColors.get(color));
    }

    public static HashMap<String, Color> getLevelColors() {
        return levelColors;
    }

    public static void clearColors() {
        levelColors.clear();
    }

    private void loadAssets() {
        imgFolderPath = GameRenderer.class.getResource("img").getFile();
        final File imgFolder = new File(imgFolderPath);

        for (final File file : imgFolder.listFiles()) {
            final String fileName = file.getName();
            if (fileName.endsWith(".png")) {
                final Image img = new Image(file.toURI().toString());
                imageFiles.put(fileName.substring(0, fileName.length() - 4), img);
            }
        }

    }

}
