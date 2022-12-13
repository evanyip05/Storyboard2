package Storyboard2;

import Storyboard2.Core.*;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static final int tileSize = 16;

    public static void main(String[] args) {
        TileSet tileSet = new TileSet("./Files/breadboard.png", tileSize, 32);
        Level level = new Level("./Files/test.txt", true);

        Editor editor = new Editor(level, tileSet, new Dimension(700, 500), new Dimension(240, 480));

        //writeToFile("./Files/test.txt", generateLevelFromTileSet(tileSet));

        editor.doRandomAnimation(false);
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

    public static void writeLevelToFile(String levelDir) {
        Level level = new Level(levelDir, true);

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