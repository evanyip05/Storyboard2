package Storyboard2.GUI;

import Storyboard2.Utils.ExtendableThread;

import java.awt.*;
import java.util.function.Consumer;

public class Cursor {
    private int realTileX, realTileY;
    private int displayX, displayY;
    private final int tileSize;

    private final Point origin;
    private Color color;

    public Cursor(int tileSize, Point origin, Color color) {this.tileSize = tileSize;this.origin = origin;this.color=color;}

    public void setColor(Color color) {this.color = color;}

    public void moveRealPos(int tilesX, int tilesY) {realTileX+=tilesX;realTileY+=tilesY;}
    public void setRealPos(int tileX, int tileY) {realTileX=tileX;realTileY=tileY;}
    public void moveDisplayPos(int dx, int dy) {displayX += dx;displayY += dy;}
    public void setDisplayPos(int x, int y) {displayX=x;displayY=y;}

    public int getDisplayX() {return displayX;}
    public int getDisplayY() {return displayY;}
    public int getRealTileX() {return realTileX;}
    public int getRealTileY() {return realTileY;}

    public Consumer<ExtendableThread> moveCursor(int tilesX, int tilesY, int duration) {
        return (thread -> {
            int animationAcceleration = 1, finalAnimationMillis = Math.max(duration, animationAcceleration);

            int targetCursorX = getDisplayX()+(tilesX*tileSize);
            int targetCursorY = getDisplayY()+(tilesY*tileSize);

            double totalCursorDistX = getDisplayX();
            double totalCursorDistY = getDisplayY();

            double cursorDx = (tilesX*tileSize+0.0)/finalAnimationMillis;
            double cursorDy = (tilesY*tileSize+0.0)/finalAnimationMillis;

            int accumulatedTimeError = 0, targetTime = finalAnimationMillis / (finalAnimationMillis / animationAcceleration);

            for (int milli = 0; milli < finalAnimationMillis / animationAcceleration; milli++) {
                // time action
                long moveStartTime = System.currentTimeMillis();

                totalCursorDistX += cursorDx * animationAcceleration;
                totalCursorDistY += cursorDy * animationAcceleration;
                setDisplayPos((int)totalCursorDistX, (int) totalCursorDistY);

                try {Thread.sleep(1);}//CHECK ME LATER FOR BUGS, idfk if this is actually sleeping the correct thread
                catch (InterruptedException e) {e.printStackTrace();}

                // time action
                long moveTotalTime = System.currentTimeMillis() - moveStartTime;

                // calculate time error
                accumulatedTimeError += moveTotalTime - targetTime;
                if (accumulatedTimeError >= targetTime) {
                    // figure out how many frames to drop
                    int droppedFrames = accumulatedTimeError / targetTime;
                    //totalDroppedFrames += droppedFrames;
                    // advance frames by dropped frames
                    milli += droppedFrames;
                    // reset accumulated error
                    accumulatedTimeError = 0;

                    // advance totals for each frame dropped
                    totalCursorDistX += (cursorDx * animationAcceleration)*droppedFrames;
                    totalCursorDistY += (cursorDy * animationAcceleration)*droppedFrames;
                }
            }

            setDisplayPos(targetCursorX, targetCursorY);
        });
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(displayX+origin.x, (displayY+origin.y), tileSize, tileSize);
    }
}
