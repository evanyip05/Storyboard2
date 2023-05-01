package Storyboard2;

import Storyboard2.Core.Level;
import Storyboard2.Core.TileSet;
import Storyboard2.Utils.TextFile;

public class Main {

    public static final int tileSpliceSize = 16;
    public static final int tileOutputSize = 32;

    public static void main(String[] args) {
        TileSet tileset1 = new TileSet("./Files/tileset.png", tileSpliceSize, tileOutputSize);
        Level level = new Level(new TextFile("./Files/emptyLevel.txt").readContent());

        new GUI(level, tileset1);
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
/*

public void animationDemo() {
        / SUPER FREAKING COOL
        level.rescale(-2,-6,1000);
        level.panCamera(128,128, 1000);
        level.panCamera(320,64, 2500);
        level.rescale(2,6,1000);

        level.rescale(-1,-3,-5,-7, 1000);
        level.rescale(1,3,5,7, 1000);
        /
        level.rescale(0,0,-64,-64,1000);

        //level.rescale(-1, -2, 1000);
        //level.rescale(0,-4,1000);
        }

public void tester() {
        int tileSpliceSize= 16, tileOutputSize = 32;

        JPanel test = new JPanel() {@Override public void paint(Graphics g) {
        g.drawImage(new TileSet("./Files/tileset.png", tileSpliceSize, tileOutputSize).getTileImage(8),0,0,null);
        }};

        test.setSize(tileOutputSize,tileOutputSize);
        test.setPreferredSize(new Dimension(tileOutputSize,tileOutputSize));

        frame.add(test, BorderLayout.PAGE_END);
        }
 */