package Storyboard2.Core;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/** interface to a tileset image,
 *  can splice images into tile sized squares
 *  can output scaled images using tileset xy or tilenum */
public class TileSet {

    private final HashMap<Integer, BufferedImage> tileMap = new HashMap<>();
    private final HashMap<Point, Integer> tileNumMap = new HashMap<>();

    private final int width, height, tileOutputSize;

    /** parses an image into a set of tiles,
     *  tile division size (define how to cut the image),
     *  tile output size (define how to produce output images) */
    public TileSet(String tileSetDir, int tileSpliceSize, int tileOutputSize) {
        Image tileSet = new ImageIcon(tileSetDir).getImage();

        this.tileOutputSize = tileOutputSize;
        this.width = (tileSet.getWidth(null)/tileSpliceSize)+((tileSet.getWidth(null)/tileSpliceSize)%tileSpliceSize!=0?1:0);
        this.height = (tileSet.getHeight(null)/tileSpliceSize)+((tileSet.getHeight(null)/tileSpliceSize)%tileSpliceSize!=0?1:0);

        int tileNum = 1;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                BufferedImage tile = new BufferedImage(tileSpliceSize, tileSpliceSize, BufferedImage.TRANSLUCENT);
                Graphics g = tile.getGraphics();
                g.drawImage(tileSet, 0, 0, tileSpliceSize, tileSpliceSize, x* tileSpliceSize, y* tileSpliceSize, x* tileSpliceSize + tileSpliceSize, y* tileSpliceSize + tileSpliceSize, null);
                g.dispose();

                tileMap.put(tileNum, tile);
                tileNumMap.put(new Point(x, y), tileNum);
                ++tileNum;
            }
        }
    }

    /** create image from tileset xy, blank tile if dne (calls overload using numMap -> still creates scaled duplicate) */
    public BufferedImage getTileImage(int x, int y) {return getTileImage(getTileNum(x, y));}

    /** tile image by tile num, blank tile if dne */ // make a copy of the image from the tileMap
    public BufferedImage getTileImage(int tileNum) {
        BufferedImage tile = tileMap.get(tileNum);
        BufferedImage res = new BufferedImage(tileOutputSize, tileOutputSize, BufferedImage.TRANSLUCENT);
        if (tile!=null) {Graphics g = res.getGraphics(); g.drawImage(tile, 0, 0, tileOutputSize, tileOutputSize, null); g.dispose();}
        return res;
    }

    /** get tile num for point on tileset, returns 0 if missing */
    public int getTileNum(int x, int y) {
        Integer num = tileNumMap.get(new Point(x,y));
        return num==null?0:num;
    }

    /** width in tiles */
    public int getWidth() {return width;}
    /** height in tiles */
    public int getHeight() {return height;}
}