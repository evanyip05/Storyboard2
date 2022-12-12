package Storyboard2.Core;

import Storyboard2.Utils.ExtendableThread;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/** subclass of jpanel built to display a level using a tileset
 *  uses a "camera" that can be moved to display an image */
public class TileDisplay extends JPanel {
    private Level level;
    private TileSet tileSet;
    private BufferedImage image;
    private Consumer<ExtendableThread> move = thread -> {};

    private int tilesX, tilesY, tileSize;
    private int imageOffsetX = 0, imageOffsetY = 0;

    private boolean animating = false;

    private final Rectangle cam;
    private final Dimension panelSize;
    private final ExtendableThread mover;

    /** level and tileset are references,
     *  tiles x/y are #tiles you want to display,
     *  tilesize is the size of the tile the tileset produces */
    public TileDisplay(Level level, TileSet tileSet, int tilesX, int tilesY, int tileSize) {
        this.tileSize = tileSize;
        this.tileSet = tileSet;
        this.tilesX = tilesX;
        this.tilesY = tilesY;
        this.level = level;

        tilesX = Math.min(tilesX, level.getWidth());
        tilesY = Math.min(tilesY, level.getHeight());

        image = createLevelImage();
        cam = new Rectangle(0, 0, tilesX, tilesY);
        panelSize = new Dimension(tileSize * tilesX, tileSize * tilesY);
        mover = new ExtendableThread() {
            @Override public void execute() {move.accept(mover);}
            @Override public boolean waitCondition() {return !animating;}
        };

        setPreferredSize(panelSize);
        setSize(panelSize);
    }

    /** create a image of the level using the tileset (only done after changes to level info were made to update image) */
    public BufferedImage createLevelImage() {
        BufferedImage res = new BufferedImage(
                level.getWidth()* tileSize,
                level.getHeight()* tileSize,
                BufferedImage.TRANSLUCENT
        );
        Graphics g = res.getGraphics();

        // never nester
        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                g.drawImage(
                        tileSet.getTileImage(Integer.parseInt(level.getInfo(x,y).split("[:]")[0])),
                        x* tileSize,
                        y* tileSize,
                        null
                );
            }
        }

        return res;
    }

    public int getTileSize() {return tileSize;}
    public int getDisplayedTilesX() {return tilesX;}
    public int getDisplayedTilesY() {return tilesY;}

    /** its just repaint (im loosing it) */
    public void nextFrame() {repaint();}

    /** redefine the level and tileset */
    public void redefine(Level level, TileSet tileSet) {redefine(level, tileSet, tilesX, tilesY, tileSize);}

    /** redefine tiles to display and tilesize */
    public void redefine(int tilesX, int tilesY, int tileSize) {redefine(level, tileSet, tilesX, tilesY, tileSize);}

    /** redefine level, tileset, tiles to display, and tilesize */
    public void redefine(Level level, TileSet tileSet, int tilesX, int tilesY, int tileSize) {
        this.tileSize = tileSize;
        this.tileSet = tileSet;
        this.tilesX = tilesX;
        this.tilesY = tilesY;
        this.level = level;

        image = createLevelImage();

        setCameraPos(0,0);

        tilesX = Math.min(tilesX, level.getWidth());
        tilesY = Math.min(tilesY, level.getHeight());

        panelSize.setSize(tileSize * tilesX, tileSize * tilesY);
        cam.setSize(tilesX, tilesY);

        setPreferredSize(panelSize);
        setSize(panelSize);
        nextFrame();
    }

    public void setCameraPos(int tileX, int tileY) {cam.setLocation(tileX, tileY);}
    public void moveCamera(int tilesX, int tilesY) {cam.translate(tilesX, tilesY);}

    /** set pixels off the tile camera is on */
    public void setCameraOff(int offX, int offY) {imageOffsetX = offX; imageOffsetY = offY;}
    /** add pixels off the tile camera is on */
    public void stepCamera(int offX, int offY) {imageOffsetX+=offX; imageOffsetY+=offY;}

    /** move the camera a specified distance x and y at the same time in a target timeframe pause after if needed,
     *  if restricted camera cant leave bounds of image */
    public void animateCamera(int tilesX, int tilesY, int animationMillis, int postAnimationMillis, boolean unrestricted) {
        if (!animating&&(unrestricted||camCanMove(tilesX, tilesY))) {
            int acceleration = 15, animationMillis1 = Math.max(animationMillis, 15);; // "acceleration" and final animation time

            animating = true;

            move = thread -> {
                // totals and deltas
                double totalX = 0, totalY = 0, dx = (tilesX * tileSize+0.0) / animationMillis1, dy = (tilesY * tileSize+0.0) / animationMillis1;
                // time error, ideal time per move
                int accumulatedError = 0, targetTime = animationMillis1/(animationMillis1/acceleration);

                for (int milli = 0; milli < animationMillis1/acceleration; milli++) {
                    // record start time
                    long moveStartTime = System.currentTimeMillis();

                    totalX += (dx * acceleration); totalY += (dy * acceleration);

                    setCameraOff((int) totalX, (int) totalY);
                    nextFrame(); thread.pause(1);

                    // calculate actual move time using difference of current and start time
                    long moveTime = System.currentTimeMillis() - moveStartTime;

                    // add difference of actual move time from ideal move time to error
                    accumulatedError += moveTime - targetTime;
                    // if error becomes more than the target time, drop a frame
                    if (accumulatedError >= targetTime) {
                        totalX += (dx * acceleration); totalY += (dy * acceleration);
                        milli+=accumulatedError/targetTime;
                        accumulatedError = 0;
                    }
                }

                setCameraOff(0,0);
                moveCamera(tilesX, tilesY);
                nextFrame();

                if(postAnimationMillis > 0) {thread.pause(postAnimationMillis);}

                animating = false;
            };

            mover.restart();
        }
    }

    /** check if cam can go to this location */
    public boolean camCanGoTo(int destTileX, int destTileY) {return destTileX>=0&&destTileY>=0&&destTileX+cam.width<=level.getWidth()&&destTileY+cam.height<=level.getHeight();}
    /** check if cam can move to this location */
    public boolean camCanMove(int tilesX, int tilesY) {return cam.x+tilesX>=0&&cam.y+tilesY>=0&&cam.x+cam.width+tilesX<=level.getWidth()&&cam.y+cam.height+tilesY<=level.getHeight();}

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, panelSize.width, panelSize.height);
        g.drawImage(image,
                0, 0,
                cam.width*tileSize,
                cam.height*tileSize,
                (cam.x*tileSize)+imageOffsetX,
                (cam.y*tileSize)+imageOffsetY,
                ((cam.x+cam.width)*tileSize)+imageOffsetX,
                ((cam.y+cam.height)*tileSize)+imageOffsetY,
                null
        );
    }
}
