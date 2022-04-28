package stickyblocks;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Unit test for Game.java
class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(4, 4);
    }

    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> {
            game = new Game(1, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            game = new Game(0, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            game = new Game(1, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            game = new Game(-1, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            game = new Game(1, -1);
        });
    }

    @Test
    void testCreatePlayer() {
        game.createPlayer(0, 0);
        assertEquals(1, game.getPlayers().size());
        assertEquals(0, game.getPlayers().get(0).getTile().getX());
        assertEquals(0, game.getPlayers().get(0).getTile().getY());
    }

    @Test
    void testCreatePlayerWithTile() {
        Tile tile = new Tile(0, 0);
        game.createPlayer(tile);
        assertEquals(1, game.getPlayers().size());
        assertEquals(0, game.getPlayers().get(0).getTile().getX());
        assertEquals(0, game.getPlayers().get(0).getTile().getY());
    }

    @Test
    void testCreatePlayerWithTiles() {
        List<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile(0, 0));
        tiles.add(new Tile(1, 0));
        tiles.add(new Tile(2, 0));
        game.createPlayer(tiles);
        assertEquals(3, game.getPlayers().size());
        assertEquals(0, game.getPlayers().get(0).getTile().getX());
        assertEquals(0, game.getPlayers().get(0).getTile().getY());
        assertEquals(1, game.getPlayers().get(1).getTile().getX());
        assertEquals(0, game.getPlayers().get(1).getTile().getY());
        assertEquals(2, game.getPlayers().get(2).getTile().getX());
        assertEquals(0, game.getPlayers().get(2).getTile().getY());
    }

    @Test
    void moveDown() {
        game.createPlayer(0, 1);
        game.createPlayer(1, 1);
        game.createPlayer(2, 1);
        game.createPlayer(3, 1);

        for (Player player : game.getPlayers()) {
            player.setActive();
        }

        game.moveDown();

        for (Player player : game.getPlayers()) {
            assertEquals(2, player.getTile().getY());
        }
    }

    @Test
    void moveUp() {
        game.createPlayer(0, 1);
        game.createPlayer(1, 1);
        game.createPlayer(2, 1);
        game.createPlayer(3, 1);

        for (Player player : game.getPlayers()) {
            player.setActive();
        }

        game.moveUp();

        for (Player player : game.getPlayers()) {
            assertEquals(0, player.getTile().getY());
        }
    }

    @Test
    void moveLeft() {
        game.createPlayer(1, 0);
        game.createPlayer(1, 1);
        game.createPlayer(1, 2);
        game.createPlayer(1, 3);

        for (Player player : game.getPlayers()) {
            player.setActive();
        }

        game.moveLeft();

        for (Player player : game.getPlayers()) {
            assertEquals(0, player.getTile().getX());
        }
    }

    @Test
    void testSetType() {
        Iterator<String> validTypes = Set.of("-", "w", "h", "f", "g").iterator();
        for (int y = 0; y <= 3; y++) {
            for (int x = 0; x <= 3; x++) {

                String type = "";
                if (validTypes.hasNext()) {
                    type = validTypes.next();
                } else {
                    validTypes = Set.of("-", "w", "h", "f", "g").iterator();
                    type = validTypes.next();
                }

                System.out.println(type);
                game.setType(x, y, type);

                Tile expected = new Tile(x, y);
                expected.setType(type);
                assertEquals(expected, game.getTile(x, y));
                assertNotEquals(null, game.getTile(x, y));
            }
        }
    }

}