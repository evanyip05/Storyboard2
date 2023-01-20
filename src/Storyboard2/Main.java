package Storyboard2;

import Storyboard2.Core.Level;
import Storyboard2.Core.TileSet;
import Storyboard2.Utils.ExtendableThread;
import Storyboard2.Utils.TextFile;

import java.util.function.Consumer;

public class Main {

    public static final int tileSize = 16;

    public static void main(String[] args) {
        TileSet tileSet = new TileSet("./Files/breadboard.png", tileSize, 32);
        TextFile file = new TextFile("./Files/test.txt");
        Level level = new Level(file.readContent());

        Consumer<ExtendableThread> action = thread -> System.out.println("aaa");


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