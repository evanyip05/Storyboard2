package Storyboard2;

import Storyboard2.Core.Level;
import Storyboard2.Core.TileDisplay;
import Storyboard2.Core.TileSet;
import Storyboard2.Utils.Listener;
import Storyboard2.Utils.TextFile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static final int tileSize = 16;


    public static void main(String[] args) {
        Level level = new Level(new TextFile("./Files/test.txt").readContent());
        TileSet tileSet = new TileSet("./Files/breadboard.png", 16, 32);

        TileDisplay display = new TileDisplay(level, tileSet,240,240);

        JFrame frame = new JFrame();
        frame.add(display);
        frame.pack();

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        for (int i = 0; i < 100; i++) {
            display.pan(100,100,100);
            display.pan(100,100,100);
            display.pan(100,100,100);
            display.pan(-300,-300,100);
        }

        display.pan(-100,-100,1000);
        display.pan(98,98,1000);

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