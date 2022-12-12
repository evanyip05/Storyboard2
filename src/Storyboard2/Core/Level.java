package Storyboard2.Core;

import java.awt.*;
import java.util.HashMap;

import static Storyboard2.Main.fileToString;

/** interface to a level file, can get and locally edit tile info */
public class Level {
    private final HashMap<Point, String> infoMap = new HashMap<>();
    private final int width, height;

    /** level from existing data */
    public Level(String levelDir) {
        String levelData = fileToString(levelDir);

        width = levelData.split("[;]")[0].split("[,]").length;
        height = levelData.split("[;]").length;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                infoMap.put(new Point(x, y), levelData.split("[;]")[y].split("[,]")[x]);
            }
        }
    }

    /** blank level from dims */
    public Level(int width, int height) {
        System.out.println("creating level...");

        this.width = width;
        this.height = height;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                infoMap.put(new Point(x, y), "0:0:0:0");
            }
        }

        System.out.println("done\n");
    }

    /** gets tile in map, empty tile if missing */
    public String getInfo(int levelX, int levelY) {
        String res = infoMap.get(new Point(levelX, levelY));
        return (res==null)?"0:0:0:0":res;
    }

    /** changes tile info in map */
    public void editInfo(int levelX, int levelY, String replace) {
        Point target = new Point(levelX, levelY);
        if (infoMap.containsKey(target)) {
            infoMap.remove(target);
            infoMap.put(target, replace);
        }
    }

    /** width in tiles */
    public int getWidth() {return width;}
    /** height in tiles */
    public int getHeight() {return height;}
}