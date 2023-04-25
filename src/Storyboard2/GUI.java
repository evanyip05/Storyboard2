package Storyboard2;

import Storyboard2.Core.Level;
import Storyboard2.Core.TileDisplay;
import Storyboard2.Core.TileSet;

import javax.swing.*;

public class GUI extends JFrame {
    private final TileDisplay tileset;
    private final TileDisplay level;

    public GUI(Level level, TileSet tileSet) {
        this.level = new TileDisplay(level, tileSet, 100,100);
        this.tileset = new TileDisplay(new Level(Main.generateLevelFromTileSet(tileSet)), tileSet, 100,100);
    }
}
