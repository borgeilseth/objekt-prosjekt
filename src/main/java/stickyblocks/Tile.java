package stickyblocks;

import java.util.Set;

/**
 * 
 * @param player
 */
public class Tile {

    private String type = " ";
    private int x, y;
    private Player player;

    /**
     * Characters define the type and state of each tile
     * empty: '-',
     * wall: 'w',
     * hole: 'h',
     * fragile: 'f'
     * goal: 'g'
     */
    private static final Set<String> VALID_TYPES = Set.of("-", "w", "h", "f", "g");

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.setType("-");
    }

    public boolean hasPlayer() {
        return player != null;
    }

    public void setType(String type) {
        if (VALID_TYPES.contains(type)) {
            this.type = type;
        } else {
            this.type = "-";
        }
    }

    public String getType() {
        return type;
    }

    public void setPlayer(Player player) {
        if (player == null && type == "f") {
            this.type = "h";
        }

        // sjekk om koblingen er riktig allerede
        if (this.player == player) {
            return;
        }
        // husk den gamle og sett den nye
        Player oldPlayer = this.player;
        this.player = player;
        // hvis det var en kobling fra før, koble den andre fra
        if (oldPlayer != null && oldPlayer.getTile() == this) {
            oldPlayer.setTile(null);
        }
        // hvis dette er en ny kobling, koble den andre til
        if (this.player != null) {
            this.player.setTile(this);
        }
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Player getPlayer() {
        return player;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        if (hasPlayer() && player.isActive())
            return "P";
        else if (hasPlayer())
            return "p";
        return String.valueOf(type);
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof Tile) {
            Tile other = (Tile) arg0;
            return this.x == other.x && this.y == other.y && this.type.equals(other.type)
                    && this.player == other.player;
        }
        return false;
    }
}