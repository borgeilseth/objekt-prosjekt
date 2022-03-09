package stickyblocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {

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
        board = new Tile[height + 1][width + 1];

        for (int y = 0; y <= height; y++) {
            for (int x = 0; x <= width; x++) {
                board[y][x] = new Tile(x, y);
            }
        }
    }

    public void createPlayer(Tile tile) {
        createPlayer(Arrays.asList(tile));
    }

    public void createPlayer(int x, int y) {
        List<Tile> tiles = Arrays.asList(new Tile(x, y));
        createPlayer(tiles);
    }

    public void createPlayer(List<Tile> tiles) {

        for (Tile newTile : tiles) {
            int x = newTile.getX();
            int y = newTile.getY();

            Player player = new Player(board[y][x]);
            players.add(player);
        }
        System.out.println(this);
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
            if (activePlayers.get(i).getTile().getType() == 'h') {

                activePlayers.get(i).setTile(null);
                players.remove(activePlayers.get(i));
                activePlayers.remove(i);

                System.out.println(activePlayers);
            }
        }

        if (isWin()) {
            System.out.println("You have won");
        }

    }

    private boolean canMove(int dx, int dy) {

        // Sjekk for hver enkelt brikke om noe er lovlig.

        for (Player player : activePlayers) {
            System.out.println(player);
            // Ut av brettet
            if ((player.getX() + dx >= getWidth() || player.getX() + dx < 0) ||
                    (player.getY() + dy >= getHeight() || player.getY() + dy < 0)) {
                return false;
            }

            try {

                if (board[player.getY() + dy][player.getX() + dx].getType() == 'w' ||
                        (board[player.getY() + dy][player.getX() + dx].hasPlayer()
                                && !board[player.getY() + dy][player.getX() + dx].getPlayer().isActive()))
                    return false;

                // Inn i en annen spiller

            } catch (Exception e) {
                continue;
            }

        }

        return true;
    }

    public boolean isWin() {

        for (Tile[] row : board) {
            for (Tile tile : row) {
                if ((tile.getType() == 'g' && !tile.hasPlayer()) || (tile.hasPlayer() && tile.getType() != 'g')) {
                    return false;
                }
            }
        }
        gameIsWon = true;
        return true;
    }

    public void setType(int x, int y, char set) {
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
                // s += " " + tile.getType();
            }
            i++;
        }

        return s + "\n";
        // return "Game [board=" + Arrays.toString(board) + ", isGameOver=" + isGameOver
        // + ", players=" + players + "]";
    }

}
