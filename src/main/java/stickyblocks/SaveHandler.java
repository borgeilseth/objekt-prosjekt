package stickyblocks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Map.Entry;

import javafx.scene.paint.Color;

public class SaveHandler implements ISavheHandler {

    public void save(String filename, Game game) throws FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(new File(getFilePath(filename)))) {
            writer.println(game.getWidth());
            writer.println(game.getHeight());

            for (int y = 0; y < game.getHeight(); y++) {
                for (int x = 0; x < game.getWidth(); x++) {
                    writer.print(game.getTile(x, y).getType());
                }
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

        }
    }

    private String getFilePath(String filename) {
        return SaveHandler.class.getResource("saves/").getFile() + filename + ".txt";
    }

    public Game load(String filename) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(getFilePath(filename)))) {
            int width = scanner.nextInt();
            int height = scanner.nextInt();
            scanner.nextLine();

            String board = scanner.next();
            if (board.length() != width * height) {
                throw new FileNotFoundException("Size of map must be equal to the given size of the board");
            }

            Game game = new Game(width - 1, height - 1);

            System.out.println(board);
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

            System.out.println(scanner.nextLine());

            next = scanner.next();

            while (!next.equals("-")) {
                GameRenderer.addColor(next, Color.web(scanner.next()));
                next = scanner.next();
            }

            return game;
        }
    }
}
