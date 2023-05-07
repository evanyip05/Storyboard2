package Storyboard2.Core;

import Storyboard2.Utils.TextFile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * interface to a level text file <br>
 * loads the info from a text file into a map of locations and info <br>
 * can read from and write to the map <br>
 * 
 */
public class Level {
    private final HashMap<Point, Integer> displayMap = new HashMap<>();
    private final HashMap<Point, Integer> collisionMap = new HashMap<>();
    private final HashMap<Point, Integer> overlayMap = new HashMap<>();
    private final HashMap<Point, Integer> interactionMap = new HashMap<>();

    private final HashMap<Integer, ArrayList<Point>> inverseDisplayMap = new HashMap<>();
    private final HashMap<Integer, ArrayList<Point>> inverseCollisionMap = new HashMap<>();
    private final HashMap<Integer, ArrayList<Point>> inverseOverlayMap = new HashMap<>();
    private final HashMap<Integer, ArrayList<Point>> inverseInteractionMap = new HashMap<>();

    private final int width, height, layers;

    /** level from existing data */
    public Level(String info) {
        width = info.split(";")[0].split(",").length;
        height = info.split(";").length - ((info.split(";")[info.split(";").length-1].length()<=1)?1:0); // make sure theres no empty line after the last actual line

        String[] rows = info.split(";");

        int maxLayers = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                String[] tile = rows[i].split(",")[j].split(":");
                int[] data = {Integer.parseInt(tile[0].strip()),Integer.parseInt(tile[1].strip()),Integer.parseInt(tile[2].strip()),Integer.parseInt(tile[3].strip())};

                displayMap.put(new Point(j,i), data[0]);
                collisionMap.put(new Point(j,i), data[1]);
                overlayMap.put(new Point(j,i), data[2]);
                interactionMap.put(new Point(j,i), data[3]);

                if (!inverseDisplayMap.containsKey(data[0])) {inverseDisplayMap.put(data[0], new ArrayList<>());}
                if (!inverseCollisionMap.containsKey(data[1])) {inverseCollisionMap.put(data[1], new ArrayList<>());}
                if (!inverseOverlayMap.containsKey(data[2])) {inverseOverlayMap.put(data[2], new ArrayList<>());}
                if (!inverseInteractionMap.containsKey(data[3])) {inverseInteractionMap.put(data[3], new ArrayList<>());}

                inverseDisplayMap.get(data[0]).add(new Point(j,i));
                inverseCollisionMap.get(data[1]).add(new Point(j,i));
                inverseOverlayMap.get(data[2]).add(new Point(j,i));
                inverseInteractionMap.get(data[3]).add(new Point(j,i));

                maxLayers = Math.max(maxLayers, data[2]);
            }
        }

        layers = maxLayers+1;
    }

    /** width in tiles */
    public int getWidth() {return width;}
    /** height in tiles */
    public int getHeight() {return height;}
    /** number of overlay layers */
    public int getLayers() {return layers;}

    public int getInfo(int levelX, int levelY, TileData type) {
        Integer res = null;
        switch (type) {
            case DISPLAY:     res=displayMap.get(new Point(levelX,levelY));break;
            case COLLISION:   res=collisionMap.get(new Point(levelX,levelY));break;
            case OVERLAY:     res=overlayMap.get(new Point(levelX,levelY));break;
            case INTERACTION: res=interactionMap.get(new Point(levelX,levelY));break;
        }

        return (res==null)?0:res;
    }

    public String getInfo(int levelX, int levelY) {
        Point target = new Point(levelX,levelY);
        return displayMap.get(target)+":"+collisionMap.get(target)+":"+overlayMap.get(target)+":"+interactionMap.get(target);
    }

    // returns actual points (please dont change their values)
    public ArrayList<Point> getMatchedTiles(TileData type, int value) {
        ArrayList<Point> res = null;
        switch (type) {
            case DISPLAY:     res =inverseDisplayMap.get(value); break;
            case COLLISION:   res =inverseCollisionMap.get(value); break;
            case OVERLAY:     res =inverseOverlayMap.get(value); break;
            case INTERACTION: res =inverseInteractionMap.get(value); break;
        }

        return (res ==null)?new ArrayList<>():new ArrayList<>(res);
    }

    /** changes tile info in map */
    public void editInfo(int levelX, int levelY, TileData type, int newInfo) {
        Point target = new Point(levelX, levelY);
        switch (type) {
            case DISPLAY:     inverseDisplayMap.get(displayMap.replace(target, newInfo)).remove(target); inverseDisplayMap.get(newInfo).add(target); break;
            case COLLISION:   inverseCollisionMap.get(collisionMap.replace(target, newInfo)).remove(target); inverseCollisionMap.get(newInfo).add(target); break;
            case OVERLAY:     inverseOverlayMap.get(overlayMap.replace(target, newInfo)).remove(target); inverseOverlayMap.get(newInfo).add(target); break;
            case INTERACTION: inverseInteractionMap.get(interactionMap.replace(target, newInfo)).remove(target); inverseInteractionMap.get(newInfo).add(target); break;
        }
    }

    public void editInfo(int levelX, int levelY, String newInfo) {
        TileData[] types = TileData.values();
        String[] info = newInfo.split(":");
        for (int i = 0; i < 4; i++) {editInfo(levelX, levelY, types[i], Integer.parseInt(info[i]));}
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