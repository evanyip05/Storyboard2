package Storyboard2;

import Storyboard2.Core.TileDisplay;
import Storyboard2.Utils.ExtendableThread;
import Storyboard2.Utils.Listener;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class Play extends JFrame implements Mover {

    private final TileDisplay displayRef;
    private final TileDisplayMover mover;

    public Play(TileDisplay displayRef) {
        this.mover = new TileDisplayMover(displayRef, this);
        this.displayRef = displayRef;

        Listener l = new Listener();

        l.addMouseBind(MouseEvent.BUTTON1, () -> {
            requestFocus();
            System.out.println("debug");
        });

        l.addKeyBind(KeyEvent.VK_LEFT , () -> mover.cimematicMove(-displayRef.getTileSize(), 0, -1, 0, 1, displayRef.getCamX(), displayRef.getCamY(), displayRef.getCamX()-1, displayRef.getCamY()));
        l.addKeyBind(KeyEvent.VK_RIGHT, () -> mover.cimematicMove( displayRef.getTileSize(), 0,  1, 0, 1, displayRef.getCamX(), displayRef.getCamY(), displayRef.getCamX()+1, displayRef.getCamY()));
        l.addKeyBind(KeyEvent.VK_UP   , () -> mover.cimematicMove(0, displayRef.getTileSize(), 0, -1, 1, displayRef.getCamX(), displayRef.getCamY(), displayRef.getCamX(), displayRef.getCamY()-1));
        l.addKeyBind(KeyEvent.VK_DOWN , () -> mover.cimematicMove(0, displayRef.getTileSize(), 0,  1, 1, displayRef.getCamX(), displayRef.getCamY(), displayRef.getCamX(), displayRef.getCamY()+1));

        addKeyListener(l);
        addMouseListener(l);

        add(displayRef);
        pack();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    @Override
    public void moveX(int tilesX, TileDisplay display, ExtendableThread thread) {

    }

    @Override
    public void moveY(int tilesY, TileDisplay display, ExtendableThread thread) {

    }
}