package Storyboard2;

import Storyboard2.Core.Level;
import Storyboard2.Core.TileDisplay;
import Storyboard2.Core.TileSet;
import Storyboard2.Utils.Listener;
import Storyboard2.Utils.TextFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static final int tileSpliceSize = 16;
    public static final int tileOutputSize = 32;


    public static void main(String[] args) {
        TileSet tileset1 = new TileSet("./Files/tileset.png", tileSpliceSize, tileOutputSize);
        Level tilesetLayout = new Level(generateLevelFromTileSet(tileset1));


        TileSet tileset2 = new TileSet("./Files/tileset.png", tileSpliceSize, tileOutputSize);
        Level level = new Level(new TextFile("./Files/emptyLevel.txt").readContent());

        TileDisplay tilesetDisplay = new TileDisplay(tilesetLayout, tileset1,8*tileOutputSize,5*tileOutputSize);
        TileDisplay levelDisplay   = new TileDisplay(level,         tileset2, 480,480);

        JPanel test = new JPanel() {
            @Override
            public void paint(Graphics g) {
                g.drawImage(tileset1.getTileImage(8),0,0,null);
            }
        };

        test.setSize(tileOutputSize,tileOutputSize);
        test.setPreferredSize(new Dimension(tileOutputSize,tileOutputSize));



        JFrame frame = new JFrame();



        frame.setLayout(new BorderLayout());

        frame.add(tilesetDisplay, BorderLayout.LINE_END);
        frame.add(levelDisplay, BorderLayout.CENTER);
        frame.add(test, BorderLayout.PAGE_END);

        frame.pack();

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        test.repaint();

        //levelDisplay.rescale(-160,-320, 1000);
        //levelDisplay.panProjection(80,160, 1000);

        // SUPER FREAKING COOL
        //levelDisplay.animateCamera(80,160,80,160, -160,-320,-160,-320, 1000);
        levelDisplay.rescale(-64,-128,1000);

        levelDisplay.panCamera(32,32, 1000);

        int time = 1;
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getExtendedKeyCode()) {
                    case KeyEvent.VK_UP:
                        levelDisplay.panCamera(0, -tileOutputSize, time);
                        break;
                    case KeyEvent.VK_LEFT:
                        levelDisplay.panCamera(-tileOutputSize, 0, time);
                        break;
                    case KeyEvent.VK_DOWN:
                        levelDisplay.panCamera(0, tileOutputSize, time);
                        break;
                    case KeyEvent.VK_RIGHT:
                        levelDisplay.panCamera(tileOutputSize, 0, time);
                        break;
                    case KeyEvent.VK_PAGE_UP:
                        levelDisplay.zoom(tileOutputSize, time);
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                        levelDisplay.zoom(-tileOutputSize, time);
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

    }

    public static void main() {
        HashMap<Integer, Character> map = new HashMap<>();
        ArrayList<String> combos = new ArrayList<>();

        map.put(0, 'U');
        map.put(1, 'U');
        map.put(2, 'D');
        map.put(3, 'D');
        map.put(4, 'L');
        map.put(5, 'R');

        // find all combos no replacement
        for (int i = 0; i < 6; i++) {
            char first = map.get(i);
            for (int j = 0; j < 6; j++) {
                if (j != i) {
                    char second = map.get(j);
                    for (int k = 0; k < 6; k++) {
                        if (k != i && k != j) {
                            char third = map.get(k);
                            for (int l = 0; l < 6; l++) {
                                if (l != i && l != j && l != k) {
                                    char fourth = map.get(l);
                                    for (int m = 0; m < 6; m++) {
                                        if (m != i && m != j && m != k && m != l) {
                                            char fifth = map.get(m);
                                            for (int n = 0; n < 6; n++) {
                                                if (n != i && n != j && n != k && n != l && n != m) {
                                                    char sixth = map.get(n);
                                                    combos.add("" + first + second + third + fourth + fifth + sixth);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        combos.forEach(System.out::println);
    }

    public static String generateLevelFromTileSet(TileSet tileSet) {return generateLevel(tileSet.getWidth(), tileSet.getHeight());}

    public static String generateLevel(int width, int height) {
        int currentTile = 1; String res = "";

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                res+=currentTile+":0:0:0,";
                ++currentTile;
            }
            res = res.substring(0, res.length()-1)+";\n";
        }

        return res.substring(0,res.length()-1);
    }

    public static void writeLevelToFile(Level level, String levelDir) {

        String levelContent ="";

        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                levelContent += level.getInfo(x,y) + ",";
            }
            levelContent=levelContent.substring(0, levelContent.length()-1) + ";\n";
        }

        new TextFile(levelDir).writeContent(levelContent);
    }

}