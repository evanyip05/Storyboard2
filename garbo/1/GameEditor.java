package Storyboard2;

import Storyboard2.Utils.ExtendableThread;
import Storyboard2.Utils.Listener;
import Storyboard2.Core.TileDisplay;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class GameEditor extends JFrame {
    private final TileDisplay displayRef;

    private final ExtendableThread mover;

    private Consumer<ExtendableThread> task = thread -> {};

    private boolean active = false;

    public GameEditor(TileDisplay display) {
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

        listener.addMouseBind(MouseEvent.BUTTON1, () -> {System.out.println("editor"); requestFocus();});

        addKeyListener(listener);
        addMouseListener(listener);
        addMouseWheelListener(listener);
        add(display);
        pack();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    public void moveCameraX(int tilesX) {
        if (!active && displayRef.camCanMove(tilesX, 0)) {
            active = true;
            task = thread -> {
                displayRef.moveCamera(tilesX, 0);
                displayRef.nextFrame();
                thread.pause(250);
                active = false;
            };
            mover.restart();
        }
    }
    public void moveCameraY(int tilesY) {
        if (!active && displayRef.camCanMove(0, tilesY)) {
            active = true;
            task = thread -> {
                displayRef.moveCamera(0, tilesY);
                displayRef.nextFrame();
                thread.pause(250);
                active = false;
            };
            mover.restart();
        }
    }
}
