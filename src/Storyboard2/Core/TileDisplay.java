package Storyboard2.Core;

import Storyboard2.Utils.Queue;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileDisplay extends Component {

    private final Rectangle camera, projection;

    private TileSet tileSet;
    private Image image;
    private Level level;

    private final Queue animations = new Queue(1);

    private int tileSize;

    public TileDisplay(Level level, TileSet tileSet, int width, int height) {
        this.tileSize = tileSet.getTileOutputSize();
        this.tileSet = tileSet;
        this.level = level;

        image = generateImage(level.getWidth()*tileSize, level.getHeight()*tileSize);
        camera = new Rectangle(width, height);
        projection = new Rectangle(width, height);

        setSize(width, height);
        setPreferredSize(new Dimension(width, height));

        animations.restart();
    }

    public BufferedImage generateImage(int width, int height) {
        BufferedImage res = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics g = res.getGraphics();

        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                g.drawImage(tileSet.getTileImage(Integer.parseInt(level.getInfo(x,y).split(":")[0].strip())), x*tileSize, y*tileSize, null);
            }
        }

        return res;
    }

    public void rescale(int dw, int dh, int duration) {
        animateCamera(0,0,0,0,dw,dh,dw,dh,duration);
    }

    // add: cant zoom out further than the biggest dimension of the map
    public void zoom(int px, int duration) {
        if (camera.height>tileSize||px<0) {
            animateCamera(px, px, 0, 0, -2 * px, -2 * px, 0, 0, duration);
        }
    }

    public void panProjection(int dx, int dy, int duration) {
        animateCamera(0,0,dx,dy,0,0,0,0,duration);
    }

    public void panCamera(int dx, int dy, int duration) {
        animateCamera(dx,dy,0,0,0,0,0,0,duration);
    }

    public void setLevel(Level level) {this.level = level; image = generateImage(level.getWidth()*tileSize, level.getHeight()*tileSize);}
    public void setTileSet(TileSet tileSet) {this.tileSet = tileSet; this.tileSize = tileSet.getTileOutputSize(); image = generateImage(level.getWidth()*tileSize, level.getHeight()*tileSize);}

    @Override
    public void paint(Graphics g) {
        g.clearRect(0,0,getWidth(),getHeight());
        g.drawImage(
                image,
                projection.x,projection.y,projection.x+projection.width,projection.y+projection.height,
                camera.x,camera.y,camera.x+camera.width,camera.y+camera.height,
                null
        );
    }


    // need to modify to check new cam coords after each animation
    // basically put call to call inline, have it make that call, so ther other call has to wait, then when it does get called, it will read the changed data
    // when intially called, need to check if there are other requests to do animations before it
    public void animateCamera(int camDx, int camDy, int projectionDx, int projectionDy, int camDw, int camDh, int projectionDw, int projectionDh, int duration) {
        animations.add(thread -> {
            int animationAcceleration = 16, finalAnimationMillis = Math.max(duration, animationAcceleration);

            int oldCamX = camera.x, oldCamY = camera.y;
            int oldProjectionX = projection.x, oldProjectionY = projection.y;
            int oldCamWidth = camera.width, oldCamHeight = camera.height;
            int oldProjectionWidth = projection.width, oldProjectionHeight = projection.height;

            // totals
            double totalCamDistX = camera.x, totalCamDistY = camera.y;
            double totalProjectionDistX = projection.x, totalProjectionDistY = projection.y;
            double totalCamWidthTransform = camera.width, totalCamHeightTransform = camera.height;
            double totalProjectionWidthTransform = projection.width, totalProjectionHeightTransform = projection.height;

            // deltas to use
            double camDeltaX = (camDx+0.0) / finalAnimationMillis;
            double camDeltaY = (camDy+0.0) / finalAnimationMillis;
            double projectionDeltaX = (projectionDx +0.0) / finalAnimationMillis;
            double projectionDeltaY = (projectionDy +0.0) / finalAnimationMillis;
            double camDeltaWidth = (camDw+0.0) / finalAnimationMillis;
            double camDeltaHeight = (camDh+0.0) / finalAnimationMillis;
            double projectionDeltaWidth = (projectionDw +0.0) / finalAnimationMillis;
            double projectionDeltaHeight = (projectionDh +0.0) / finalAnimationMillis;

            int accumulatedTimeError = 0, targetTime = finalAnimationMillis / (finalAnimationMillis / animationAcceleration);

            for (int milli = 0; milli < finalAnimationMillis / animationAcceleration; milli++) {
                // time action
                long moveStartTime = System.currentTimeMillis();

                totalCamDistX += camDeltaX * animationAcceleration;
                totalCamDistY += camDeltaY * animationAcceleration;
                totalProjectionDistX += projectionDeltaX * animationAcceleration;
                totalProjectionDistY += projectionDeltaY * animationAcceleration;
                totalCamWidthTransform += camDeltaWidth * animationAcceleration;
                totalCamHeightTransform += camDeltaHeight * animationAcceleration;
                totalProjectionWidthTransform += projectionDeltaWidth * animationAcceleration;
                totalProjectionHeightTransform += projectionDeltaHeight * animationAcceleration;

                camera.setLocation((int) totalCamDistX, (int) totalCamDistY);
                camera.setSize((int) totalCamWidthTransform, (int) totalCamHeightTransform);
                projection.setLocation((int) totalProjectionDistX, (int) totalProjectionDistY);
                projection.setSize((int) totalProjectionWidthTransform, (int) totalProjectionHeightTransform);
                repaint();
                try {
                    thread.wait(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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
                    totalCamDistX += (camDeltaX * animationAcceleration) * droppedFrames;
                    totalCamDistY += (camDeltaY * animationAcceleration) * droppedFrames;
                    totalProjectionDistX += (projectionDeltaX * animationAcceleration) * droppedFrames;
                    totalProjectionDistY += (projectionDeltaY * animationAcceleration) * droppedFrames;
                    totalCamWidthTransform += (camDeltaWidth * animationAcceleration) * droppedFrames;
                    totalCamHeightTransform += (camDeltaHeight * animationAcceleration) * droppedFrames;
                    totalProjectionWidthTransform += (projectionDeltaWidth * animationAcceleration) * droppedFrames;
                    totalProjectionHeightTransform += (projectionDeltaHeight * animationAcceleration) * droppedFrames;
                }
            }

            camera.setLocation(new Point(oldCamX+camDx, oldCamY+camDy));
            camera.setSize(new Dimension(oldCamWidth+camDw, oldCamHeight+camDh));
            projection.setLocation(new Point(oldProjectionX+ projectionDx, oldProjectionY+ projectionDy));
            projection.setSize(new Dimension(oldProjectionWidth+ projectionDw, oldProjectionHeight+ projectionDh));
            repaint();
            //System.out.println("done!");
        });
    }

    // returns a composed movement for the camera to be played
    public void animateCamera(Point finalCamLoc, Point finalProjectionLoc, Dimension finalCamSize, Dimension finalProjectionSize, int duration) {
        animations.add(thread -> {
            int animationAcceleration = 16, finalAnimationMillis = Math.max(duration, animationAcceleration);

            // totals
            double totalCamDistX = camera.x, totalCamDistY = camera.y;
            double totalProjectionDistX = projection.x, totalProjectionDistY = projection.y;
            double totalCamWidthTransform = camera.width, totalCamHeightTransform = camera.height;
            double totalProjectionWidthTransform = projection.width, totalProjectionHeightTransform = projection.height;

            // deltas to use
            double camDeltaX = (finalCamLoc.x - camera.x + 0.0) / finalAnimationMillis;
            double camDeltaY = (finalCamLoc.y - camera.y + 0.0) / finalAnimationMillis;
            double projectionDeltaX = (finalProjectionLoc.x - projection.x + 0.0) / finalAnimationMillis;
            double projectionDeltaY = (finalProjectionLoc.y - projection.y + 0.0) / finalAnimationMillis;
            double camDeltaWidth = (finalCamSize.width - camera.width + 0.0) / finalAnimationMillis;
            double camDeltaHeight = (finalCamSize.height - camera.height + 0.0) / finalAnimationMillis;
            double projectionDeltaWidth = (finalProjectionSize.width - projection.width + 0.0) / finalAnimationMillis;
            double projectionDeltaHeight = (finalProjectionSize.height - projection.height + 0.0) / finalAnimationMillis;

            int accumulatedTimeError = 0, targetTime = finalAnimationMillis / (finalAnimationMillis / animationAcceleration);

            for (int milli = 0; milli < finalAnimationMillis / animationAcceleration; milli++) {
                // time action
                long moveStartTime = System.currentTimeMillis();

                totalCamDistX += camDeltaX * animationAcceleration;
                totalCamDistY += camDeltaY * animationAcceleration;
                totalProjectionDistX += projectionDeltaX * animationAcceleration;
                totalProjectionDistY += projectionDeltaY * animationAcceleration;
                totalCamWidthTransform += camDeltaWidth * animationAcceleration;
                totalCamHeightTransform += camDeltaHeight * animationAcceleration;
                totalProjectionWidthTransform += projectionDeltaWidth * animationAcceleration;
                totalProjectionHeightTransform += projectionDeltaHeight * animationAcceleration;

                camera.setLocation((int) totalCamDistX, (int) totalCamDistY);
                camera.setSize((int) totalCamWidthTransform, (int) totalCamHeightTransform);
                projection.setLocation((int) totalProjectionDistX, (int) totalProjectionDistY);
                projection.setSize((int) totalProjectionWidthTransform, (int) totalProjectionHeightTransform);

                repaint();
                try {
                    thread.wait(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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
                    totalCamDistX += (camDeltaX * animationAcceleration) * droppedFrames;
                    totalCamDistY += (camDeltaY * animationAcceleration) * droppedFrames;
                    totalProjectionDistX += (projectionDeltaX * animationAcceleration) * droppedFrames;
                    totalProjectionDistY += (projectionDeltaY * animationAcceleration) * droppedFrames;
                    totalCamWidthTransform += (camDeltaWidth * animationAcceleration) * droppedFrames;
                    totalCamHeightTransform += (camDeltaHeight * animationAcceleration) * droppedFrames;
                    totalProjectionWidthTransform += (projectionDeltaWidth * animationAcceleration) * droppedFrames;
                    totalProjectionHeightTransform += (projectionDeltaHeight * animationAcceleration) * droppedFrames;
                }
            }

            camera.setLocation(finalCamLoc);
            camera.setSize(finalCamSize);
            projection.setLocation(finalProjectionLoc);
            projection.setSize(finalProjectionSize);
            repaint();
            //System.out.println("done!");
        });
    }
}
