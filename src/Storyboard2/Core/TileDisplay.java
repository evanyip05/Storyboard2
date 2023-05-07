package Storyboard2.Core;

import Storyboard2.Utils.ExtendableThread;
import Storyboard2.Utils.Queue;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class TileDisplay extends Component {

    private final HashMap<Integer, Consumer<Graphics>> inbetweens = new HashMap<>();
    private final ArrayList<BufferedImage> layers = new ArrayList<>();
    private final Rectangle camera, projection;

    private TileSet tileSet;
    private Level level;

    private final Queue animations = new Queue();
    private final Queue movements = new Queue(3);

    private int tileSize;

    public TileDisplay(Level level, TileSet tileSet, int tilesX, int tilesY) {
        this.tileSize = tileSet.getTileOutputSize();
        this.tileSet = tileSet;
        this.level = level;

        generateLayers();

        camera = new Rectangle(tilesX*tileSize, tilesY*tileSize);
        projection = new Rectangle(tilesX*tileSize, tilesY*tileSize);

        setSize(tilesX*tileSize, tilesY*tileSize);
        setPreferredSize(new Dimension(tilesX*tileSize, tilesY*tileSize));
    }

    public int getTileInfo(int levelX, int levelY, TileData type) {return level.getInfo(levelX, levelY, type);}
    public String getTileInfo(int levelX, int levelY) {return level.getInfo(levelX, levelY);}

    public BufferedImage getTileImage(int levelX, int levelY) {return tileSet.getTileImage(level.getInfo(levelX, levelY, TileData.DISPLAY));}

    public Dimension getProjectionDim() {return projection.getSize();}

    public void generateLayers() {
        ArrayList<BufferedImage> res = new ArrayList<>();

        for (int i = 0; i < level.getLayers(); i++) {
            BufferedImage layer = new BufferedImage(level.getWidth()*tileSize, level.getHeight()*tileSize, BufferedImage.TRANSLUCENT);
            Graphics g = layer.getGraphics();
            level.getMatchedTiles(TileData.OVERLAY, i).forEach(tile -> {
                g.drawImage(tileSet.getTileImage(level.getInfo(tile.x,tile.y, TileData.DISPLAY)), tile.x*tileSize, tile.y*tileSize, null);
            });
            res.add(layer);
        }

        layers.clear();
        layers.addAll(res);
    }

    public void addInbetween(Consumer<Graphics> drawOp, int afterLayer) {inbetweens.put(afterLayer, drawOp);}

    // intertwine rescaling and zooming into a method,     // add: cant zoom out further than the biggest dimension of the map
    public void rescale(int left, int right, int top, int bottom, int duration) {animations.add(getAnimation(-left,-top,-left,-top,(left + right),(top + bottom),(left + right),(top + bottom),duration));}
    public void rescale(int width, int height, int duration) {rescale(width/2, width/2, height/2,height/2, duration);}
    public void zoom(double multiplier, int duration) {animations.add(getAnimation((int)-((projection.width*multiplier)/2),(int)-((projection.height*multiplier)/2),0,0,(int)(projection.width*multiplier),(int)(projection.height*multiplier),0,0,duration));}

    public void move(int tilesX, int tilesY, int duration) {if (!animations.isActive()) {movements.add(getAnimation(tilesX*tileSize, tilesY*tileSize,0,0,0,0,0,0,duration));}}
    public void panProjection(int dx, int dy, int duration) {animations.add(getAnimation(0,0,dx,dy,0,0,0,0,duration).andThen(thread -> {movements.play();}));}
    public void panCamera(int dx, int dy, int duration) {animations.add(getAnimation(dx,dy,0,0,0,0,0,0,duration).andThen(thread -> {movements.play();}));}

    public void setLevel(Level level) {this.level = level; generateLayers();}
    public void setTileSet(TileSet tileSet) {this.tileSet = tileSet; this.tileSize = tileSet.getTileOutputSize(); generateLayers();}

    @Override
    public void paint(Graphics g) {
        g.clearRect(0,0,getWidth(),getHeight());
        g.setColor(Color.BLACK);
        g.fillRect(0,0,getWidth(),getHeight());

        BufferedImage finalImage = new BufferedImage(level.getWidth()*tileSize, level.getHeight()*tileSize, BufferedImage.TRANSLUCENT);
        Graphics buffer = finalImage.getGraphics();

        for (int layer = 0; layer < layers.size(); layer++) {
            buffer.drawImage(layers.get(layer),0,0,null);
            if (inbetweens.containsKey(layer)) {inbetweens.get(layer).accept(buffer);}
        }

        inbetweens.forEach((layer, drawOp) -> {if (layer > (layers.size()-1)) {drawOp.accept(buffer);}});

        g.drawImage(
                finalImage,
                projection.x,projection.y,projection.x+projection.width,projection.y+projection.height,
                camera.x,camera.y,camera.x+camera.width,camera.y+camera.height,
                null
        );
    }

    // need to modify to check new cam coords after each animation
    // basically put call to call inline, have it make that call, so ther other call has to wait, then when it does get called, it will read the changed data
    // when intially called, need to check if there are other requests to do animations before it
    private Consumer<ExtendableThread> getAnimation(int camDx, int camDy, int projectionDx, int projectionDy, int camDw, int camDh, int projectionDw, int projectionDh, int duration) {
        return (thread -> {
            int animationAcceleration = 1, finalAnimationMillis = Math.max(duration, animationAcceleration);

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
                    Thread.sleep(1); //CHECK ME LATER FOR BUGS, idfk if this is actually sleeping the correct thread
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
    private Consumer<ExtendableThread> getAnimation(Point finalCamLoc, Point finalProjectionLoc, Dimension finalCamSize, Dimension finalProjectionSize, int duration) {
        return (thread -> {
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
