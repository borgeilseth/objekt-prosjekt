package stickyblocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game implements Iterable<Tile> {

    private Tile[][] board;
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Player> activePlayers;
    private boolean gameIsWon = false;

    /***
     * The Constructor of the Game class
     * 
     * @param width  The width of the play tilemap, the number of x-coordinates.
     * @param height The height of the play tilemap, the number of y-coordinates.
     */
    public Game(int width, int height) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Width and height must be greater than zero.");
        }

        board = new Tile[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                board[y][x] = new Tile(x, y);
            }
        }
    }

    public void createPlayer(Tile tile) {
        Player player = new Player(tile);
        players.add(player);
    }

    public void createPlayer(int x, int y) {
        createPlayer(board[y][x]);
    }

    public void createPlayer(List<Tile> tiles) {
        for (Tile tile : tiles) {
            createPlayer(tile);
        }
    }

    private boolean move(int dx, int dy) {

        activePlayers = new ArrayList<Player>();
        for (Player player : players) {
            if (player.isActive())
                activePlayers.add(player);
        }

        if (!canMove(dx, dy) || gameIsWon) {
            return false;
        }

        // Sort the players by their coordinates to prevent players from moving into
        // each other.
        activePlayers.sort((obj1, obj2) -> {
            return dx * (obj2.getX() - obj1.getX()) + dy * (obj2.getY() - obj1.getY());
        });

        for (Player player : activePlayers) {

            player.setTile(board[player.getY() + dy][player.getX() + dx]);
            activateNeighbors(player.getTile());

        }

        for (int i = activePlayers.size() - 1; i >= 0; --i) {
            if (activePlayers.get(i).getTile().getType().equals("h")) {

                players.remove(activePlayers.get(i));
                activePlayers.get(i).setTile(null);
                activePlayers.remove(i);

            }
        }

        if (isWin()) {
            System.out.println("You have won");
        }

        return true;
    }

    // Activates all the player tiles that are adjacent to the given tile, and all
    // the tiles that are adjacent to those.
    private void activateNeighbors(Tile tile) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (Math.abs(i) == Math.abs(j)) {
                    continue;
                }
                try {
                    Tile neighborTile = board[tile.getY() + j][tile.getX() + i];

                    if (neighborTile.hasPlayer() && !neighborTile.getPlayer().isActive()) {
                        neighborTile.getPlayer().setActive();
                        activateNeighbors(neighborTile);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }

            }
        }
    }

    private boolean canMove(int dx, int dy) {

        for (Player player : activePlayers) {
            // Ut av brettet
            if ((player.getX() + dx >= getWidth() || player.getX() + dx < 0) ||
                    (player.getY() + dy >= getHeight() || player.getY() + dy < 0)) {
                return false;
            }

            try {
                if ((board[player.getY() + dy][player.getX() + dx].getType().equals("w")) ||
                        (board[player.getY() + dy][player.getX() + dx].hasPlayer()
                                && !board[player.getY() + dy][player.getX() + dx].getPlayer().isActive()))
                    return false;

            } catch (Exception e) {
                continue;
            }

        }

        return true;
    }

    public boolean isWin() {

        for (Tile[] row : board) {
            for (Tile tile : row) {
                if ((tile.getType().equals("g") && !tile.hasPlayer())
                        || (tile.hasPlayer() && !tile.getType().equals("g"))) {
                    return false;
                }
            }
        }
        gameIsWon = true;
        return true;
    }

    public void setType(int x, int y, String set) {
        board[y][x].setType(set);
    }

    public boolean moveUp() {
        return move(0, -1);
    }

    public boolean moveRight() {
        return move(+1, 0);
    }

    public boolean moveDown() {
        return move(0, +1);
    }

    public boolean moveLeft() {
        return move(-1, 0);
    }

    public int getWidth() {
        return board[0].length;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getHeight() {
        return board.length;
    }

    public Tile[][] getBoard() {
        return board;
    }

    public Tile getTile(int x, int y) {
        return board[y][x];
    }

    @Override
    public String toString() {

        String s = " ";

        for (int i = 0; i < board[0].length; i++) {
            s += " " + i;
        }
        ;
        int i = 0;
        for (Tile[] row : board) {
            s += String.format("\n%s", i);
            for (Tile tile : row) {
                s += String.format(" %s", tile);
            }
            i++;
        }

        return s + "\n";
    }

    @Override
    public Iterator<Tile> iterator() {
        return new Iterator<Tile>() {
            int x = 0;
            int y = 0;

            @Override
            public boolean hasNext() {
                return y < board.length && x < board[0].length;
            }

            @Override
            public Tile next() {
                Tile tile = board[y][x];
                x++;
                if (x >= board[0].length) {
                    x = 0;
                    y++;
                }
                return tile;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    };
}
