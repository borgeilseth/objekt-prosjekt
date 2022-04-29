package stickyblocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.scene.paint.Color;

public class SaveHandlerTest {

    SaveHandler saveHandler = new SaveHandler();
    Game game = new Game(4, 4);

    @BeforeEach
    public void setUp() {
        game.createPlayer(0, 0);
        game.createPlayer(1, 1);
        game.createPlayer(2, 2);
        game.createPlayer(3, 3);
        game.getPlayers().get(0).setActive();
        game.getPlayers().get(2).setActive();
        game.setType(0, 1, "w");
        game.setType(1, 3, "h");

    }

    @Test
    void testSave() {

        String expected = """
                4
                4
                ----w--------h--
                0 0 true
                1 1 false
                2 2 true
                3 3 false
                -
                -
                    """;

        try {
            saveHandler.save("test/test", game);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // read the file lvls/test.txt
        assertEquals(expected, readFile("test"));

        GameRenderer.addColor("bg", Color.web("#080808"));
        expected = """
                4
                4
                ----w--------h--
                0 0 true
                1 1 false
                2 2 true
                3 3 false
                -
                bg 0x080808ff
                -
                    """;
        try {
            saveHandler.save("test/test", game);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(expected, readFile("test"));

        // test file with a null game
        assertThrows(FileNotFoundException.class, () -> saveHandler.save("test/test", null));
    }

    @Test
    void testLoad() {
        try {
            saveHandler.save("test/test", game);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Game loadedGame = null;
        try {
            loadedGame = saveHandler.load("test/test");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals(game.getWidth(), loadedGame.getWidth());
        assertEquals(game.getHeight(), loadedGame.getHeight());
        assertEquals(game.getBoard()[0][0].getType(), loadedGame.getBoard()[0][0].getType());
        assertEquals(game.getBoard()[1][3].getType(), loadedGame.getBoard()[1][3].getType());
        assertEquals(game.getPlayers().get(0).getX(), loadedGame.getPlayers().get(0).getX());
        assertEquals(game.getPlayers().get(0).getY(), loadedGame.getPlayers().get(0).getY());
        assertEquals(game.getPlayers().get(0).isActive(), loadedGame.getPlayers().get(0).isActive());
        assertEquals(game.getPlayers().get(1).getX(), loadedGame.getPlayers().get(1).getX());
        assertEquals(game.getPlayers().get(1).getY(), loadedGame.getPlayers().get(1).getY());
        assertEquals(game.getPlayers().get(1).isActive(), loadedGame.getPlayers().get(1).isActive());
        assertEquals(game.getPlayers().get(2).getX(), loadedGame.getPlayers().get(2).getX());
        assertEquals(game.getPlayers().get(2).getY(), loadedGame.getPlayers().get(2).getY());
        assertEquals(game.getPlayers().get(2).isActive(), loadedGame.getPlayers().get(2).isActive());
        assertEquals(game.getPlayers().get(3).getX(), loadedGame.getPlayers().get(3).getX());

        assertThrows(FileNotFoundException.class, () -> saveHandler.load("test/badFile1"));
        assertThrows(FileNotFoundException.class, () -> saveHandler.load("test/badFile2"));

    }

    private String readFile(String fileName) {
        String path = getClass().getResource("levels/test/").getFile() + fileName + ".txt";
        StringBuilder result = new StringBuilder("");
        try {
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                result.append(scanner.nextLine() + "\n");
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}
