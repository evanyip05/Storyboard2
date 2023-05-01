package Storyboard2;

import Storyboard2.Core.Level;
import Storyboard2.Core.TileDisplay;
import Storyboard2.Core.TileSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GUI extends JFrame {
    private final TileDisplay tileset;
    private final TileDisplay level;

    private final JFrame frame = new JFrame();

    private TileDisplay currentDisplay;

    private int moveTime = 100;

    public GUI(Level level, TileSet tileset) {
        this.level = new TileDisplay(level, tileset, 15,15);
        this.tileset = new TileDisplay(new Level(Main.generateLevelFromTileSet(tileset)), tileset, 8,5);
        this.currentDisplay = this.level;


        frame.setLayout(new BorderLayout());

        frame.add(this.tileset, BorderLayout.LINE_END);
        frame.add(this.level, BorderLayout.CENTER);

        frame.addKeyListener(new KeyListener() {
            @Override public void keyTyped(KeyEvent e) {}

            @Override public void keyPressed(KeyEvent e) {
                switch (e.getExtendedKeyCode()) {
                    case KeyEvent.VK_UP: currentDisplay.move(0, -1, moveTime); break;
                    case KeyEvent.VK_LEFT: currentDisplay.move(-1, 0, moveTime); break;
                    case KeyEvent.VK_DOWN: currentDisplay.move(0, 1, moveTime); break;
                    case KeyEvent.VK_RIGHT: currentDisplay.move(1, 0, moveTime); break;
                    case KeyEvent.VK_PAGE_UP: Dimension projection = currentDisplay.getProjectionDim(); currentDisplay.zoom(((2*tileset.getTileOutputSize())+0.0)/projection.width, moveTime); break;
                    case KeyEvent.VK_PAGE_DOWN: projection = currentDisplay.getProjectionDim(); currentDisplay.zoom(-((2*tileset.getTileOutputSize())+0.0)/projection.width, moveTime); break;
                }
            }

            @Override public void keyReleased(KeyEvent e) {}
        });

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
