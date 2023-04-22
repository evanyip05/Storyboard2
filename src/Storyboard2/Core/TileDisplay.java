package Storyboard2.Core;

import Storyboard2.Utils.Queue;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileDisplay extends Component {

    private final Rectangle camera, projection;

    private TileSet tileSet;
    private Image image;
    private Level level;

    private final Queue animations = new Queue();

    private int tileSize;

    public TileDisplay(Level level, TileSet tileSet, int width, int height) {
        this.tileSize = tileSet.getTileOutputSize();
        this.tileSet = tileSet;
        this.level = level;

        image = generateImage();
        camera = new Rectangle(width, height);
        projection = new Rectangle(width, height);

        setSize(width, height);
        setPreferredSize(new Dimension(width, height));

        animations.restart();
    }

    public BufferedImage generateImage() {
        BufferedImage res = new BufferedImage(level.getWidth()*tileSize, level.getHeight()*tileSize, BufferedImage.TRANSLUCENT);
        Graphics g = res.getGraphics();

        for (int y = 0; y < level.getHeight()-1; y++) {
            for (int x = 0; x < level.getWidth()-1; x++) {
               g.drawImage(tileSet.getTileImage(Integer.parseInt(level.getInfo(x,y).split(":")[0])), x*tileSize, y*tileSize, null);
            }
        }
        return res;
    }

    public void zoom(int px, int duration) {
        animateCamera(px/2,px/2,0,0,-px,-px,0,0,duration);
    }

    public void pan(int dx, int dy, int duration) {
        animateCamera(dx,dy,0,0,0,0,0,0,duration);
    }

    public void setLevel(Level level) {this.level = level; image = generateImage();}
    public void setTileSet(TileSet tileSet) {this.tileSet = tileSet; this.tileSize = tileSet.getTileOutputSize(); image = generateImage();}

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
    private void animateCamera(int camDx, int camDy, int projectDx, int projectDy, int camDw, int camDh, int projectDw, int projectDh, int duration) {
        animations.add(thread -> {
            int animationAcceleration = 16, finalAnimationMillis = Math.max(duration, animationAcceleration);

            // totals
            double totalCamDistX = camera.x, totalCamDistY = camera.y;
            double totalProjectionDistX = projection.x, totalProjectionDistY = projection.y;
            double totalCamWidthTransform = camera.width, totalCamHeightTransform = camera.height;
            double totalProjectionWidthTransform = projection.width, totalProjectionHeightTransform = projection.height;

            // deltas to use
            double camDeltaX = (camDx+0.0) / finalAnimationMillis;
            double camDeltaY = (camDy+0.0) / finalAnimationMillis;
            double projectionDeltaX = (projectDx+0.0) / finalAnimationMillis;
            double projectionDeltaY = (projectDy+0.0) / finalAnimationMillis;
            double camDeltaWidth = (camDw+0.0) / finalAnimationMillis;
            double camDeltaHeight = (camDh+0.0) / finalAnimationMillis;
            double projectionDeltaWidth = (projectDw+0.0) / finalAnimationMillis;
            double projectionDeltaHeight = (projectDh+0.0) / finalAnimationMillis;

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

            repaint();
            //System.out.println("done!");
        });
    }

    // returns a composed movement for the camera to be played
    private void animateCamera(Point finalCamLoc, Point finalProjectionLoc, Dimension finalCamSize, Dimension finalProjectionSize, int duration) {
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
