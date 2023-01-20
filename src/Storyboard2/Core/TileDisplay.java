package Storyboard2.Core;

import Storyboard2.Utils.ExtendableThread;
import Storyboard2.Utils.SynchronousSequence;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/** NOT A CONTAINER, meant to be added to a container */
public class TileDisplay extends Component {
    private final SynchronousSequence animationSequence;
    private final Dimension displaySize;
    private final Rectangle cam;
    private final Point projection;

    private TileSet tileSet;
    private Image image;
    private Level level;

    private int tileSize;

    public TileDisplay(Level level, TileSet tileSet, int camWidth, int camHeight, int displayWidth, int displayHeight, int tileSize) {
        this.tileSize = tileSize;
        this.tileSet = tileSet;
        this.level = level;

        image = generateImage();

        cam = new Rectangle(0,0, camWidth, camHeight);
        projection = new Point(0,0);

        animationSequence = new SynchronousSequence();
        displaySize = new Dimension(displayWidth, displayHeight);

        setSize(displaySize);
        setPreferredSize(displaySize);
    }

    public BufferedImage generateImage() {
        BufferedImage res = new BufferedImage(level.getWidth()*tileSize, level.getHeight()*tileSize, BufferedImage.TRANSLUCENT);
        Graphics g = res.getGraphics();

        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                g.drawImage(tileSet.getTileImage(Integer.parseInt(level.getInfo(x, y).split("[:]")[0])), x*tileSize, y*tileSize, null);
            }
        }

        return res;
    }

    public int getCamX() {return cam.x;}
    public int getCamY() {return cam.y;}
    public int getCamWidth() {return cam.width;}
    public int getCamHeight() {return cam.height;}
    public int getDisplayWidth() {return displaySize.width;}
    public int getDisplayHeight() {return displaySize.height;}
    public int getProjectionX() {return projection.x;}
    public int getProjectionY() {return projection.y;}

    // resizing methods
    public void reConstruct(int camWidth, int camHeight, int displayWidth, int displayHeight, int tileSize) {reConstruct(level, tileSet, camWidth, camHeight, displayWidth, displayHeight, tileSize);}
    public void reConstruct(Level level, TileSet tileSet) {reConstruct(level, tileSet, cam.width, cam.height, displaySize.width, displaySize.height, tileSize);}
    public void reConstruct(Level level, TileSet tileSet, int camWidth, int camHeight, int displayWidth, int displayHeight, int tileSize) {
        this.tileSize = tileSize;
        this.tileSet = tileSet;
        this.image = generateImage();
        this.level = level;

        displaySize.setSize(displayWidth, displayHeight);
        cam.setSize(camWidth, camHeight);

        setSize(displaySize);
        setPreferredSize(displaySize);
    }

    // returns a composed movement for the camera to be played
    private Consumer<ExtendableThread> getCameraAnimation(Point finalCamLoc, Point finalProjectionLoc, Dimension finalCamSize, int animationMillis, int postMillis) {
        return thread -> {
            int animationAcceleration = 16, finalAnimationMillis = Math.max(animationMillis, animationAcceleration);

            // totals
            double totalCamDistX = cam.x, totalCamDistY = cam.y;
            double totalProjectionDistX = projection.x, totalProjectionDistY = projection.y;
            double totalCamWidthTransform = cam.width, totalCamHeightTransform = cam.height;

            // deltas to use
            double camDeltaX = (finalCamLoc.x - cam.x + 0.0) / finalAnimationMillis;
            double camDeltaY = (finalCamLoc.y - cam.y + 0.0) / finalAnimationMillis;
            double projectionDeltaX = (finalProjectionLoc.x - projection.x + 0.0) / finalAnimationMillis;
            double projectionDeltaY = (finalProjectionLoc.y - projection.y + 0.0) / finalAnimationMillis;
            double camWidthDelta = (finalCamSize.width - cam.width + 0.0) / finalAnimationMillis;
            double camHeightDelta = (finalCamSize.height - cam.height + 0.0) / finalAnimationMillis;

            int accumulatedTimeError = 0, targetTime = finalAnimationMillis / (finalAnimationMillis / animationAcceleration);

            for (int milli = 0; milli < finalAnimationMillis / animationAcceleration; milli++) {
                // time action
                long moveStartTime = System.currentTimeMillis();

                totalCamDistX += camDeltaX * animationAcceleration;
                totalCamDistY += camDeltaY * animationAcceleration;
                totalProjectionDistX += projectionDeltaX * animationAcceleration;
                totalProjectionDistY += projectionDeltaY * animationAcceleration;
                totalCamWidthTransform += camWidthDelta * animationAcceleration;
                totalCamHeightTransform += camHeightDelta * animationAcceleration;

                cam.setLocation((int) totalCamDistX, (int) totalCamDistY);
                cam.setSize((int) totalCamWidthTransform, (int) totalCamHeightTransform);
                projection.setLocation((int) totalProjectionDistX, (int) totalProjectionDistY);

                repaint();
                thread.pause(1);

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
                    totalCamWidthTransform += (camWidthDelta * animationAcceleration) * droppedFrames;
                    totalCamHeightTransform += (camHeightDelta * animationAcceleration) * droppedFrames;
                }
            }

            cam.setLocation(finalCamLoc);
            cam.setSize(finalCamSize);
            projection.setLocation(finalProjectionLoc);
            repaint();

            if (postMillis > 0) {thread.pause(postMillis);}
        };
    }

    public void animateCamera(Point finalCamLoc, Point finalProjectionLoc, Dimension finalDim, int animationTime, int postTime) {
        if (animationSequence.notRunning()) {
            animationSequence.setActionSequence(getCameraAnimation(finalCamLoc, finalProjectionLoc, finalDim, animationTime, postTime));
            animationSequence.run();
        }
    }
    public void animateCameraProjection(Point finalLoc, int animationTime, int postTime) {animateCamera(cam.getLocation(), finalLoc, cam.getSize(), animationTime, postTime);}
    public void animateCameraLoc(int dx, int dy, int animationTime, int postTime) {animateCamera(new Point(cam.x+dx, cam.y+dy), projection, cam.getSize(), animationTime, postTime);}
    public void animateCameraFrame(Point finalLoc, int animationTime, int postTime) {animateCamera(finalLoc, finalLoc, cam.getSize(), animationTime, postTime);}
    public void animateCameraDimension(Dimension finalDim, int animationTime, int postTime) {animateCamera(cam.getLocation(), projection, finalDim, animationTime, postTime);}

    // paint camera rect to component
    @Override public void paint(Graphics g) {
        g.clearRect(0,0, displaySize.width, displaySize.height);
        g.drawImage(image,
                projection.x, projection.y, (projection.x+cam.width), (projection.y+cam.height),
                cam.x       , cam.y       , (cam.x+cam.width)       , (cam.y+cam.height)       , (null)
        );
    }
}