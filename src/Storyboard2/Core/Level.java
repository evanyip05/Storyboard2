package Storyboard2.Core;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import static Storyboard2.Main.fileToString;

/**
 * interface to a level text file <br>
 * loads the info from a text file into a map of locations and info <br>
 * can read from and write to the map <br>
 * 
 */
public class Level {
    private final HashMap<Point, String> infoMap = new HashMap<>();
    private final int width, height;

    /** level from existing data */
    public Level(String info, boolean isDir) {
        String levelData = isDir?fileToString(info):info;

        width = levelData.split("[;]")[0].split("[,]").length;
        height = levelData.split("[;]").length;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                infoMap.put(new Point(x, y), levelData.split("[;]")[y].split("[,]")[x].replaceAll("[\\s]+", ""));
            }
        }
    }

    /** gets tile in map, empty tile if missing */
    public String getInfo(int levelX, int levelY) {
        String res = infoMap.get(new Point(levelX, levelY));
        return (res==null)?"0:0:0:0":res;
    }
    /** width in tiles */
    public int getWidth() {return width;}
    /** height in tiles */
    public int getHeight() {return height;}
    
    /** changes tile info in map */
    public void editInfo(int levelX, int levelY, String replace) {
        Point target = new Point(levelX, levelY);
        if (infoMap.containsKey(target)) {
            infoMap.remove(target);
            infoMap.put(target, replace);
        }
    }
    
    public void writeLevelToFile(String dir) {
        String content = "";
        
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                content += getInfo(x,y) + ",";
            }
            content=content.substring(0, content.length()-1) + ";";
        }

        try {FileWriter writer = new FileWriter(dir); writer.write(content); writer.close();}
        catch (IOException e) {System.out.println("file does not exist or could not write");}
    }
}