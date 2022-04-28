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
     *               Indexed at zero.
     * @param height The height of the play tilemap, the number of y-coordinates.
     *               Indexed at zero.
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

    private void move(int dx, int dy) {

        activePlayers = new ArrayList<Player>();
        for (Player player : players) {
            if (player.isActive())
                activePlayers.add(player);
        }

        if (!canMove(dx, dy) || gameIsWon) {
            return;
        }

        // Sortering av spiller brikkene slik at de ikke flytter seg oppå felt
        // hvor en annen brikke står
        // FIXME: Dette bør gjøres bedre. kanskje knytte direkte opp mot dx og dy.
        activePlayers.sort((obj1, obj2) -> {
            if (dx != 0) {
                if (dx > 0) {
                    return obj2.getX() - obj1.getX();
                }
                return obj1.getX() - obj2.getX();
            } else {
                if (dy > 0) {
                    return obj2.getY() - obj1.getY();
                }
                return obj1.getY() - obj2.getY();
            }
        });

        for (Player player : activePlayers) {

            player.setTile(board[player.getY() + dy][player.getX() + dx]);

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (Math.abs(i) == Math.abs(j)) {
                        continue;
                    }

                    try {
                        Tile neighborTile = board[player.getY() + j][player.getX() + i];

                        if (neighborTile.hasPlayer()) {
                            neighborTile.getPlayer().setActive();
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        continue;
                    }

                }
            }

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

    public void moveUp() {
        move(0, -1);
    }

    public void moveRight() {
        move(+1, 0);
    }

    public void moveDown() {
        move(0, +1);
    }

    public void moveLeft() {
        move(-1, 0);
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
                return x < board.length && y < board[0].length;
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
