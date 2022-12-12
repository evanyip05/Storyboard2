package Storyboard2;

import Storyboard2.Core.TileDisplay;
import Storyboard2.Utils.ExtendableThread;
import Storyboard2.Utils.Listener;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class Edit extends JFrame implements Mover {

    private final TileDisplayMover mover;

    public Edit(TileDisplay displayRef) {
        this.mover = new TileDisplayMover(displayRef, this);

        Listener l = new Listener();

        l.addMouseBind(MouseEvent.BUTTON1, () -> {
            requestFocus();
            System.out.println("debug");
        });

        l.addKeyBind(KeyEvent.VK_LEFT , () -> mover.moveX(-1));
        l.addKeyBind(KeyEvent.VK_RIGHT, () -> mover.moveX(1));
        l.addKeyBind(KeyEvent.VK_UP   , () -> mover.moveY(-1));
        l.addKeyBind(KeyEvent.VK_DOWN , () -> mover.moveY(1));

        addKeyListener(l);
        addMouseListener(l);

        add(displayRef);
        pack();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    public void useMover(Consumer<TileDisplayMover> mover) {
        mover.accept(this.mover);
    }

    @Override
    public void moveX(int tilesX, TileDisplay display, ExtendableThread thread) {
        for (int step = 0; step < tilesX*display.getTileSize(); step++) {
            display.stepCamera(1, 0);
            display.nextFrame();
            thread.pause(1);
        }
        display.setCameraOff(0,0);
        display.moveCamera(tilesX, 0);
        display.nextFrame();
        mover.endMove();
        /*
        display.moveCamera(tilesX, 0);
        display.nextFrame();

        thread.pause(250);
         */
    }

    @Override
    public void moveY(int tilesY, TileDisplay display, ExtendableThread thread) {
        for (int step = 0; step < tilesY*display.getTileSize(); step++) {
            display.stepCamera(0, 1);
            display.nextFrame();
            thread.pause(1);
        }
        display.setCameraOff(0,0);
        display.moveCamera(0, tilesY);
        display.nextFrame();
        mover.endMove();
        /*
        display.moveCamera(0, tilesY);
        display.nextFrame();

        thread.pause(250);
         */
    }
}
