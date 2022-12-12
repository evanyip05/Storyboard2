package Storyboard2;

import Storyboard2.Core.TileDisplay;
import Storyboard2.Utils.ExtendableThread;

import java.util.function.Consumer;

public class TileDisplayMover {

    private final TileDisplay displayRef;
    private final ExtendableThread moveExecutor;
    private final Mover mover;

    private boolean moving = false;
    private Consumer<ExtendableThread> move = thread -> {};

    private int xMoves = 0, yMoves = 0, moves = 0;

    public TileDisplayMover(TileDisplay displayRef, Mover mover) {
        this.mover = mover;
        this.displayRef = displayRef;
        this.moveExecutor = new ExtendableThread() {
            @Override public void execute() {
                System.out.println("starting move #" + moves);
                ++moves;
                move.accept(moveExecutor);
            }
            @Override public boolean waitCondition() {return !moving;}
        };
    }

    public void endMove() {
        moving = false;
    }

    public void stopMoveOp() {
        move = thread -> {};
        moving = false;
        moveExecutor.restart();



        moving = true;
    }

    public void cimematicMove(int xSteps, int ySteps, int dx, int dy, int timeDiv, int startTileX, int startTileY, int destTileX, int destTileY) {
        if (!moving) {
            displayRef.setCameraOff(0,0);
            displayRef.setCameraPos(startTileX, startTileY);
            displayRef.nextFrame();

            moving = true;
            move = thread -> {
                int totalX = 0, totalY = 0;

                while (totalX + dx < xSteps && totalY + dy < ySteps) {

                    displayRef.stepCamera(dx, dy);
                    displayRef.nextFrame();

                    totalY += dy;
                    totalX += dx;

                    thread.pause(timeDiv);
                }

                int remainingX = xSteps - totalX, remainingY = ySteps - totalY;
                displayRef.stepCamera(remainingX, remainingY);
                displayRef.nextFrame();

                thread.pause(timeDiv);

                displayRef.setCameraOff(0, 0);
                displayRef.setCameraPos(destTileX, destTileY);

                moving = false;
            };

            moveExecutor.restart();
        }
    }

    //public void moveX(int tilesX) {cimematicMove(displayRef.getTileSize()*tilesX, 0, 1, 0, 16, displayRef.getCamX(), displayRef.getCamY(), displayRef.getCamX()+tilesX, displayRef.getCamY());}
    //public void moveY(int tilesY) {cimematicMove(0, displayRef.getTileSize()*tilesY, 0, 1, 16, displayRef.getCamX(), displayRef.getCamY(), displayRef.getCamX(), displayRef.getCamY()+tilesY);}

    public void moveX(int tilesX) {
        if (!moving) {
            System.out.println("starting move x #"+xMoves);
            ++xMoves;
            moving = true;
            move = thread -> {
                mover.moveX(tilesX, displayRef, thread);
            };
            moveExecutor.restart();
        }
    }

    public void moveY(int tilesY) {
        if (!moving) {
            System.out.println("starting move y #"+yMoves);
            ++yMoves;
            moving = true;
            move = thread -> {
                mover.moveY(tilesY, displayRef, thread);
            };
            moveExecutor.restart();
        }
    }
}
