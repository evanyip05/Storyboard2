package Storyboard2;

import Storyboard2.Core.Level;
import Storyboard2.Core.TileDisplay;
import Storyboard2.Core.TileSet;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static final int tileSize = 16;

    public static void main(String[] args) {
        TileSet tileSet = new TileSet("./Files/borderTest.png", tileSize, 32);
        writeToFile("./Files/test.txt", generateLevelFromTileSet(tileSet));
        Level level = new Level("./Files/test.txt");

        TileDisplay display1 = new TileDisplay(level, tileSet, 16, 10, 32);
        Editor editor = new Editor(display1);
    }

    public static String generateLevelFromTileSet(TileSet tileSet) {
        System.out.println(tileSet.getWidth() + " " + tileSet.getHeight());
        return generateLevel(tileSet.getWidth(), tileSet.getHeight());
    }

    public static String generateLevel(int width, int height) {
        int currentTile = 1;

        String res = "";

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                res+=currentTile+":0:0:0,";
                ++currentTile;
            }
            res = res.substring(0, res.length()-1)+";\n";
        }

        return res;
    }

    public static void writeLevelToFile(String levelDir) {
        Level level = new Level(levelDir);

        String levelContent ="";

        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                levelContent += level.getInfo(x,y) + ",";
            }
            levelContent=levelContent.substring(0, levelContent.length()-1) + ";\n";
        }

        writeToFile(levelDir, levelContent);
    }

    /** returns string from a text file using a directory, returns an empty string if read fails*/
    public static String fileToString(String dir) {
        String res = "";

        try {
            Scanner reader = new Scanner(Path.of(dir));
            while (reader.hasNext()) {res = res + reader.nextLine();}
            return res;
        }
        catch (IOException e) {System.out.println("file did not exist or could not read"); return res;}
    }

    /** overwrite a file with a string*/
    public static void writeToFile(String dir, String content) {
        try {FileWriter writer = new FileWriter(dir); writer.write(content); writer.close();}
        catch (IOException e) {System.out.println("file does not exist or could not write");}
    }
}
/*
listener.addMousePressBind(MouseEvent.BUTTON1, (panel, mouseEvent) -> game.requestFocus());

        listener.addKeyPressBind(KeyEvent.VK_ESCAPE, (panel, keyEvent) -> System.exit(0));
        listener.addKeyPressBind(KeyEvent.VK_SPACE,  (panel, keyEvent) -> System.out.println("debug"));

        listener.addKeyPressBind(KeyEvent.VK_UP   ,  (panel, keyEvent) -> game.moveCameraY(-1));
        listener.addKeyPressBind(KeyEvent.VK_DOWN ,  (panel, keyEvent) -> game.moveCameraY(1));
        listener.addKeyPressBind(KeyEvent.VK_LEFT ,  (panel, keyEvent) -> game.moveCameraX(-1));
        listener.addKeyPressBind(KeyEvent.VK_RIGHT,  (panel, keyEvent) -> game.moveCameraX(1));

        //listener.addKeyBind(KeyEvent.VK_UP   , (panel, keyEvent) -> game.endMoveU());
        //listener.addKeyBind(KeyEvent.VK_DOWN , (panel, keyEvent) -> game.endMoveD());
        //listener.addKeyBind(KeyEvent.VK_LEFT , (panel, keyEvent) -> game.endMoveL());
        //listener.addKeyBind(KeyEvent.VK_RIGHT, (panel, keyEvent) -> game.endMoveR());

        listener.addKeyPressBind(KeyEvent.VK_CONTROL, (panel, keyEvent) -> {
            display.redefine(10, 10, 16);
            display.nextFrame();
        });
        listener.addKeyPressBind(KeyEvent.VK_SHIFT, (panel, keyEvent) -> {
            display.redefine(10, 10, 32);
            display.nextFrame();
        });
 */
