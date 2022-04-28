package stickyblocks;

public class Player {

    private Tile tile;
    private boolean active = false;

    public Player(Tile tile) {
        setTile(tile);
    }

    public void setActive() {
        this.active = true;
    }

    public void setTile(Tile tile) {
        // sjekk om koblingen er riktig allerede
        if (this.tile == tile) {
            return;
        }
        // husk den gamle og sett den nye
        Tile oldTile = this.tile;
        this.tile = tile;
        // hvis det var en kobling fra før, koble den andre fra
        if (oldTile != null && oldTile.getPlayer() == this) {
            oldTile.setPlayer(null);
        }
        // hvis dette er en ny kobling, koble den andre til
        if (this.tile != null) {
            this.tile.setPlayer(this);
        }
    }

    public int getX() {
        return tile.getX();
    }

    public int getY() {
        return tile.getY();
    }

    public Tile getTile() {
        return tile;
    }

    public Boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "Player [awake=" + active + ", tile=" + tile.getType() + ", x=" + getX() + ", y=" + getY() + "]";
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof Player) {
            Player player = (Player) arg0;
            return player.getX() == getX() && player.getY() == getY() && player.isActive() == isActive();
        }
        return false;
    }
}
