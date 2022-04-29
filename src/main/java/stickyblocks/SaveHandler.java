package stickyblocks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.Map.Entry;

import javafx.scene.paint.Color;

public class SaveHandler implements ISavheHandler {

    public void save(String fileName, Game game) throws FileNotFoundException {
        System.out.println(fileName);
        try (PrintWriter writer = new PrintWriter(new File(getLevelResourcePath(fileName)))) {

            writer.println(game.getWidth());
            writer.println(game.getHeight());

            String level = "";
            for (Tile tile : game) {
                level += tile.getType() + "";
            }

            writer.println(level);

            for (Player player : game.getPlayers()) {
                writer.println(String.format("%d %d %b", player.getX(), player.getY(), player.isActive()));
            }

            writer.println("-");

            for (Entry<String, Color> entry : GameRenderer.getLevelColors().entrySet()) {
                writer.println(entry.getKey() + " " + entry.getValue());
            }

            writer.println("-");

        } catch (Exception e) {
            throw new FileNotFoundException();
        }
    }

    public String getLevelResourcePath(String filename) throws URISyntaxException {
        return SaveHandler.class.getResource("levels/").getFile() + filename + ".txt";
    }

    public Game load(String filename) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(getLevelResourcePath(filename)))) {
            GameRenderer.clearColors();

            int width = scanner.nextInt();
            int height = scanner.nextInt();
            scanner.nextLine();

            String board = scanner.next();
            if (board.length() != width * height) {
                throw new FileNotFoundException("Size of map," + width * height
                        + ", must be equal to the given size of the board," + board.length());
            }

            Game game = new Game(width, height);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    String symbol = String.valueOf(board.charAt(y * width + x));
                    game.getBoard()[y][x].setType(symbol);
                }
            }

            String next = scanner.next();

            while (!next.equals("-")) {

                game.createPlayer(Integer.valueOf(next), Integer.valueOf(scanner.next()));

                if (Boolean.valueOf(scanner.next())) {
                    game.getPlayers().get(game.getPlayers().size() - 1).setActive();
                }

                next = scanner.next();
            }

            next = scanner.next();

            while (!next.equals("-")) {
                GameRenderer.addColor(next, Color.web(scanner.next()));
                next = scanner.next();
            }

            return game;
        } catch (Exception e) {
            System.err.println("\n" + e.getMessage());
            throw new FileNotFoundException("Could not load level: " + filename);
        }
    }
}
