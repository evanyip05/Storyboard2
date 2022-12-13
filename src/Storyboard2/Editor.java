package Storyboard2;

import Storyboard2.Core.Level;
import Storyboard2.Core.TileDisplay;
import Storyboard2.Core.TileSet;
import Storyboard2.Utils.Listener;
import Storyboard2.Utils.MouseFollower;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Editor extends JFrame {
    private TileDisplay levelDisplay, tileSetDisplay;

    private boolean scalingWidth = false;

    public Editor(Level level, TileSet tileSet, Dimension levelDisplayDim, Dimension tileSetDisplayDim) {
        levelDisplay = new TileDisplay(level, tileSet, 100, 100, levelDisplayDim.width, levelDisplayDim.height, tileSet.getTileOutputSize());
        tileSetDisplay = new TileDisplay(new Level(Main.generateLevelFromTileSet(tileSet), false), tileSet,100,100, tileSetDisplayDim.width, tileSetDisplayDim.height, tileSet.getTileOutputSize());

        setUndecorated(true);

        Listener l = new Listener(100);

        MouseFollower<Editor> follower = new MouseFollower<>(this);

        l.addMousePressBind(MouseEvent.BUTTON1, () -> {System.out.println("-----begin-----"); requestFocus();});
        l.addMouseReleaseBind(MouseEvent.BUTTON1, () -> {System.out.println("-----end-----"); requestFocus();});
        l.addMouseBind(MouseEvent.BUTTON1, () -> {System.out.println("BRUH");});

        addKeyListener(l);

        //l.addMousePressBind(MouseEvent.BUTTON1, () -> {
        //    requestFocus();
        //    follower.startFollowing();
        //});
        //
        //l.addMouseReleaseBind(MouseEvent.BUTTON1, ()-> {
        //    follower.stopFollowing();
        //    System.out.println("tes");
        //});
        //
        //l.addKeyBind(KeyEvent.VK_ESCAPE, () -> {
        //    System.out.println("aaaa");
        //    System.exit(0);
        //});
        //l.addKeyBind(KeyEvent.VK_SPACE , () -> scalingWidth = !scalingWidth);
        //
        //l.addKeyBind(KeyEvent.VK_LEFT , () -> levelDisplay.animateCameraLoc(-16,   0, 100, 0));
        //l.addKeyBind(KeyEvent.VK_RIGHT, () -> levelDisplay.animateCameraLoc( 16,   0, 100, 0));
        //l.addKeyBind(KeyEvent.VK_UP   , () -> levelDisplay.animateCameraLoc(  0, -16, 100, 0));
        //l.addKeyBind(KeyEvent.VK_DOWN , () -> levelDisplay.animateCameraLoc(  0,  16, 100, 0));

        //l.addKeyBind(KeyEvent.VK_PAGE_UP, () -> {
        //    mainDisplay.redefine(
        //            mainDisplay.getDisplayedTilesX()+(scalingWidth?(mainDisplay.getDisplayedTilesX()-1>0?-1:0):0),
        //            mainDisplay.getDisplayedTilesY()+(scalingWidth?0:(mainDisplay.getDisplayedTilesY()-1>0?-1:0)),
        //            mainDisplay.getTileSize()
        //    );
        //    mainDisplay.nextFrame();
        //
        //    pack();
        //
        //    try {
        //        Thread.sleep(100);
        //    } catch (InterruptedException e) {
        //        e.printStackTrace();
        //    }
        //});
        //l.addKeyBind(KeyEvent.VK_PAGE_DOWN, () -> {
        //    mainDisplay.redefine(
        //            mainDisplay.getDisplayedTilesX()+(scalingWidth?1:0),
        //            mainDisplay.getDisplayedTilesY()+(scalingWidth?0:1),
        //            mainDisplay.getTileSize()
        //    );
        //    mainDisplay.nextFrame();
        //
        //    pack();
        //
        //    try {
        //        Thread.sleep(100);
        //    } catch (InterruptedException e) {
        //        e.printStackTrace();
        //    }
        //});

        addKeyListener(l);
        addMouseListener(l);

        setLayout(new BorderLayout());

        add(new Menu(216, 16, (g, host) -> {g.fillRect(0,0,216,16); host.repaint();}), BorderLayout.PAGE_START);
        add(levelDisplay, BorderLayout.LINE_START);
        add(tileSetDisplay, BorderLayout.LINE_END);
        add(new Menu(216, 48, (g, host) -> {g.fillRect(0,0, 216, 48); host.repaint();}), BorderLayout.PAGE_END);

        pack();

        


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocation(100,100);
        setResizable(false);
        setVisible(true);
    }
}

/*
    today, Ill be talking about technological illetaracy. Whats technological illitaracy? Well, for some its self explanatory -explain levels

    repl.it

    cs is going to become a basic skill everyone will use
 */