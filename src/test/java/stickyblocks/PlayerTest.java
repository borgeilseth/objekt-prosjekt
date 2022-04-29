package stickyblocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PlayerTest {

    @Test
    void testGetX() {
        Player player = new Player(new Tile(0, 0));
        assertEquals(0, player.getX());

        Player player2 = new Player(null);
        assertThrows(NullPointerException.class, () -> {
            player2.getX();
        });

    }

    @Test
    void testGetY() {
        Player player = new Player(new Tile(0, 0));
        assertEquals(0, player.getY());

        Player player2 = new Player(null);
        assertThrows(NullPointerException.class, () -> {
            player2.getY();
        });
    }

    @Test
    void testSetActive() {
        Player player = new Player(new Tile(0, 0));
        assertEquals(false, player.isActive());

        player.setActive();
        assertEquals(true, player.isActive());
    }

    @Test
    void testSetTile() {
        Player player = new Player(new Tile(0, 0));
        assertEquals(new Tile(0, 0), player.getTile());

        player.setTile(null);
        assertEquals(null, player.getTile());
    }
}
