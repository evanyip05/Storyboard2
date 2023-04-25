package Storyboard2.Core;

import Storyboard2.Utils.TextFile;

import java.awt.*;
import java.util.HashMap;

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
    public Level(String info) {
        width = info.split(";")[0].split(",").length;
        height = info.split(";").length - ((info.split(";")[info.split(";").length-1].length()<=1)?1:0); // make sure theres no empty line after the last actual line


        String[] rows = info.split(";");

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                infoMap.put(new Point(j,i), rows[i].split(",")[j]);
            }
        }
    }


    /** width in tiles */
    public int getWidth() {return width;}
    /** height in tiles */
    public int getHeight() {return height;}

    /** gets tile in map, empty tile if missing */
    public String getInfo(int levelX, int levelY) {
        String res = infoMap.get(new Point(levelX, levelY));
        return (res==null)?"0:0:0:0":res;
    }

    /** changes tile info in map */
    public void editInfo(int levelX, int levelY, String replace) {
        Point target = new Point(levelX, levelY);
        infoMap.remove(target);
        infoMap.put(target, replace);
    }
    
    public void writeLevelToFile(String dir) {
        String content = "";
        
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {content += getInfo(x,y) + ",";}
            content=content.substring(0, content.length()-1) + ";";
        }

        new TextFile(dir).writeContent(content);
    }
}