package stickyblocks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.Map.Entry;

import javafx.scene.paint.Color;

public class SaveHandler implements ISavheHandler {

    public void save(String filename, Game game) throws FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(new File(getFilePath(filename)))) {
            writer.println(game.getWidth());
            writer.println(game.getHeight());

            // for (int y = 0; y < game.getHeight(); y++) {
            // for (int x = 0; x < game.getWidth(); x++) {
            // writer.print(game.getTile(x, y).getType());
            // }
            // }

            for (Tile tile : game) {
                writer.println(tile.getType());
            }

            writer.println(game.getPlayers().size());

            for (Player player : game.getPlayers()) {
                writer.println(String.format("%d %d %b", player.getX(), player.getY(), player.isActive()));
            }

            writer.println("-");

            for (Entry<String, Color> entry : GameRenderer.getLevelColors().entrySet()) {
                writer.println(entry.getKey() + " " + entry.getValue());
            }

            writer.println("-");
        } catch (Exception e) {
            System.err.println("Error saving game: " + e.getMessage());
            throw new FileNotFoundException();
        }
    }

    public URI getFilePath(String filename) throws URISyntaxException {
        return SaveHandler.class.getResource(filename).toURI();
    }

    public Game load(String filename) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(getFilePath(filename)))) {

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
