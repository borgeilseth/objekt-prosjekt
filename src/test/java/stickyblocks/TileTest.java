package stickyblocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

public class TileTest {
    @Test
    void testEquals() {
        Tile tile1 = new Tile(0, 0);
        tile1.setType("w");
        Tile tile2 = new Tile(0, 0);
        tile2.setType("");
        assertNotEquals(tile1, tile2);

        tile2.setType("w");
        assertEquals(tile1, tile2);

        Tile tile3 = new Tile(0, 1);
        tile3.setType("w");
        assertNotEquals(tile1, tile3);
    }

    @Test
    void testSetPlayer() {
        Tile tile = new Tile(0, 0);
        tile.setPlayer(new Player(tile));
        assertEquals(tile, tile.getPlayer().getTile());

        tile.setPlayer(null);
        assertEquals(null, tile.getPlayer());

        tile.setPlayer(new Player(tile));
        assertEquals(new Player(tile), tile.getPlayer());

    }

    @Test
    void testSetType() {
        Set<String> valudTypes = Set.of("-", "w", "h", "f", "g");
        String defaultValue = "-";

        Tile tile = new Tile(0, 0);
        assertEquals(defaultValue, tile.getType());

        for (String type : valudTypes) {
            tile.setType(type);
            assertEquals(type, tile.getType());
        }

        tile.setType("");
        assertEquals(defaultValue, tile.getType());

    }
}
