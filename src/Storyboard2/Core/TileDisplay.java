package Storyboard2.Core;

import Storyboard2.Utils.ExtendableThread;
import Storyboard2.Utils.TimedExecutable;
import Storyboard2.Utils.TimedSequence;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class TileDisplay extends JPanel {
    private final TimedSequence animationSequence;
    private final Dimension displaySize;
    private final Rectangle cam;
    private final TileSet tileSet;
    private final Point projection;
    private final Level level;

    private final Image image;

    private final int tileSize;

    public TileDisplay(Level level, TileSet tileSet, int camWidth, int camHeight, int displayWidth, int displayHeight, int tileSize) {
        this.tileSize = tileSize;
        this.tileSet = tileSet;
        this.level = level;

        image = generateImage();

        cam = new Rectangle(0,0, camWidth, camHeight);
        projection = new Point(0,0);

        animationSequence = new TimedSequence();
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

    public TimedExecutable getCameraAnimation(Point finalCamLoc, Point finalProjectionLoc, Dimension finalCamSize, int animationMillis, int postMillis) {
        return thread -> {
            int animationAcceleration = 16, finalAnimationMillis = Math.max(animationMillis, animationAcceleration);
            int totalDroppedFrames = 0;

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
                    totalDroppedFrames += droppedFrames;
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

            if (postMillis > 0) {thread.pause(postMillis);}

            System.out.println(totalDroppedFrames + " frames dropped");
        };
    }

    public void animateCameraProjection(Point finalLoc, int animationTime, int postTime) {
        if (animationSequence.notRunning()) {
            animationSequence.setActionSequence(getCameraAnimation(cam.getLocation(), finalLoc, cam.getSize(), animationTime, postTime));
        }
    }

    public void animateCameraLoc(int dx, int dy, int animationTime, int postTime) {
        if (animationSequence.notRunning()) {
            animationSequence.setActionSequence(getCameraAnimation(new Point(cam.x+dx, cam.y+dy), projection, cam.getSize(), animationTime, postTime));
            animationSequence.run();
        }
    }

    public void animateCameraFrame(Point finalLoc, int animationTime, int postTime) {
        if (animationSequence.notRunning()) {
            animationSequence.setActionSequence(getCameraAnimation(finalLoc, finalLoc, cam.getSize(), animationTime, postTime));
            animationSequence.run();
        }
    }

    public void animateCameraDimension(Dimension finalDim, int animationTime, int postTime) {
        if (animationSequence.notRunning()) {
            animationSequence.setActionSequence(getCameraAnimation(cam.getLocation(), projection, finalDim, animationTime, postTime));
            animationSequence.run();
        }
    }

    public void animateCamera(Point finalCamLoc, Point finalProjectionLoc, Dimension finalDim, int animationTime, int postTime) {
        if (animationSequence.notRunning()) {
            animationSequence.setActionSequence(getCameraAnimation(finalCamLoc, finalProjectionLoc, finalDim, animationTime, postTime));
            animationSequence.run();
        }
    }

    public void animateCamera(TimedExecutable... animations) {
        if (animationSequence.notRunning()) {
            animationSequence.setActionSequence(animations);
            animationSequence.run();
        }
    }

    @Override public void paint(Graphics g) {
        g.clearRect(0,0, displaySize.width, displaySize.height);
        g.drawImage(
                image,
                projection.x,
                projection.y,
                projection.x+cam.width,
                projection.y+cam.height,
                cam.x,
                cam.y,
                cam.x+cam.width,
                cam.y+cam.height,
                null
        );
    }
}

/*



    public void animate(Consumer<ExtendableThread> animation) {
        if (!animating) {
            animating = true;
            move = animation.andThen(thread -> {animating = false;});
            mover.restart();
        }
    }

    public void animateCamera(Consumer<ExtendableThread>... animationSequence) {
        if (!animating) {
            Consumer<ExtendableThread> res = thread -> {};
            for (Consumer<ExtendableThread> animation : animationSequence) {res = res.andThen(animation);}

            animating = true;
            move = res.andThen(thread -> animating = false);
            mover.restart();
        }
    }





 public void setOffQueue() {
        queueRunning = true;
        queueAction(0);
        System.out.println("queue finished");
        queueRunning = false;
    }
    public void queueAction(int index) {
        while (animating) {Thread.onSpinWait();}
        if (moveQueue.size() > 0) {
            animating = true;
            move = moveQueue.get(index).andThen(thread -> moveQueue.remove(index));
            mover.restart();
            queueAction(index+1);
        }
    }
 */
