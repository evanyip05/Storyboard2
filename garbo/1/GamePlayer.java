package Storyboard2;

import Storyboard2.Utils.ExtendableThread;
import Storyboard2.Utils.Listener;
import Storyboard2.Core.TileDisplay;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class GamePlayer extends JFrame {
    private final TileDisplay displayRef;
    private final ExtendableThread mover;

    private Consumer<ExtendableThread> task = thread -> {};

    private boolean active = false;

    public GamePlayer(TileDisplay display) {
        mover = new ExtendableThread() {
            @Override public void execute() {task.accept(mover);}
            @Override public boolean waitCondition() {return !active;}
        };

        displayRef = display;

        Listener listener = new Listener();

        listener.addKeyBind(KeyEvent.VK_UP   , () -> moveCameraY(-1));
        listener.addKeyBind(KeyEvent.VK_DOWN , () -> moveCameraY(1));
        listener.addKeyBind(KeyEvent.VK_LEFT , () -> moveCameraX(-1));
        listener.addKeyBind(KeyEvent.VK_RIGHT, () -> moveCameraX(1));

        listener.addMouseBind(MouseEvent.BUTTON1, () -> {System.out.println("player"); requestFocus();});

        addKeyListener(listener);
        addMouseListener(listener);
        addMouseWheelListener(listener);
        add(display);
        pack();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);

        animate(displayRef.getTileSize()*1,displayRef.getTileSize()*2, 1,2, 10);
    }

    public void animate(int xSteps, int ySteps, int tileXFinal, int tileYFinal, int frames) {
        if (!active && displayRef.camCanGoTo(tileXFinal, tileYFinal)) {
            active = true;

            task = thread -> {
                int dx = xSteps/frames, dy = ySteps/frames;

                for (int frame = 0; frame < frames; frame++) {
                    displayRef.stepCamera(dx, dy);
                    displayRef.nextFrame();
                    thread.pause(1);
                }

                displayRef.setCameraOff(0,0);
                displayRef.setCameraPos(tileXFinal, tileYFinal);
                displayRef.nextFrame();

                thread.pause(1);

                active = false;
            };

            mover.restart();
        }
    }

    public void moveCameraX(int tilesX) {
        int tileSize = displayRef.getTileSize();

        if (!active && displayRef.camCanMove(tilesX, 0)) {
            active = true;

            task = thread -> {
                for (int frame = 0; frame < (Math.abs(tilesX) * tileSize)/2; frame++) {
                    displayRef.stepCamera(((tilesX > 0) ? 2 : -2), 0);
                    displayRef.nextFrame();
                    thread.pause(1);
                }

                displayRef.setCameraOff(0,0);
                displayRef.moveCamera(tilesX, 0);
                displayRef.nextFrame();

                thread.pause(1);

                active = false;
            };

            mover.restart();
        }
    }
    public void moveCameraY(int tilesY) {
        int tileSize = displayRef.getTileSize();

        if (!active && displayRef.camCanMove(0, tilesY)) {
            active = true;

            task = thread -> {
                for (int frame = 0; frame < (Math.abs(tilesY) * tileSize)/2; frame++) {
                    displayRef.stepCamera(0, ((tilesY > 0) ? 2 : -2));
                    displayRef.nextFrame();
                    thread.pause(1);
                }

                displayRef.setCameraOff(0,0);
                displayRef.moveCamera(0, tilesY);
                displayRef.nextFrame();

                thread.pause(1);

                active = false;
            };

            mover.restart();
        }
    }
}
