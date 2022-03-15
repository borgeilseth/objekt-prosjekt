package stickyblocks;

import java.io.FileNotFoundException;

public interface ISavheHandler {

    public void save(String filename, Game game) throws FileNotFoundException;

    public Game load(String filename) throws FileNotFoundException;
}
