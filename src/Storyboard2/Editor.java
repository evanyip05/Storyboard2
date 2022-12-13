package Storyboard2;

import Storyboard2.Core.Level;
import Storyboard2.Core.TileDisplay;
import Storyboard2.Core.TileSet;
import Storyboard2.Utils.Listener;
import Storyboard2.Utils.MouseFollower;
import Storyboard2.Utils.SynchronousSequence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class Editor extends JFrame {
    private TileDisplay levelDisplay, tileSetDisplay;

    private boolean scalingWidth = false;

    public Editor(Level level, TileSet tileSet, Dimension levelDisplayDim, Dimension tileSetDisplayDim) {
        levelDisplay = new TileDisplay(level, tileSet, levelDisplayDim.width, levelDisplayDim.height, levelDisplayDim.width, levelDisplayDim.height, tileSet.getTileOutputSize());
        tileSetDisplay = new TileDisplay(new Level(Main.generateLevelFromTileSet(tileSet), false), tileSet,tileSetDisplayDim.width, tileSetDisplayDim.height, tileSetDisplayDim.width, tileSetDisplayDim.height, tileSet.getTileOutputSize());

        setUndecorated(true);

        Listener l = new Listener(100);

        MouseFollower<Editor> follower = new MouseFollower<>(this);

        l.addMousePressBind(MouseEvent.BUTTON1, () -> {requestFocus(); follower.startFollowing();});
        l.addMouseReleaseBind(MouseEvent.BUTTON1, follower::stopFollowing);

        l.addKeyBind(KeyEvent.VK_ESCAPE, () -> System.exit(0));
        l.addKeyBind(KeyEvent.VK_SPACE , () -> scalingWidth = !scalingWidth);

        System.out.println(tileSet.getTileOutputSize());
        l.addKeyBind(KeyEvent.VK_LEFT , () -> levelDisplay.animateCameraLoc(-tileSet.getTileOutputSize(),   0, 100, 0));
        l.addKeyBind(KeyEvent.VK_RIGHT, () -> levelDisplay.animateCameraLoc( tileSet.getTileOutputSize(),   0, 100, 0));
        l.addKeyBind(KeyEvent.VK_UP   , () -> levelDisplay.animateCameraLoc(  0, -tileSet.getTileOutputSize(), 100, 0));
        l.addKeyBind(KeyEvent.VK_DOWN , () -> levelDisplay.animateCameraLoc(  0,  tileSet.getTileOutputSize(), 100, 0));

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

        int totalWidth = levelDisplay.getWidth()+tileSetDisplay.getWidth();

        add(new Menu(totalWidth, 16, (g, host) -> {g.fillRect(0,0,host.getWidth(),16); host.repaint();}), BorderLayout.PAGE_START);
        add(levelDisplay, BorderLayout.LINE_START);
        add(tileSetDisplay, BorderLayout.LINE_END);
        add(new Menu(totalWidth, 48, (g, host) -> {g.fillRect(0,0, host.getWidth(), 48); host.repaint();}), BorderLayout.PAGE_END);

        pack();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocation(100,100);
        setResizable(false);
        setVisible(true);
    }

    // cam demo
    public void doRandomAnimation(boolean doubleMove) {
        ArrayList<SynchronousSequence.SynchronousExecutable> animations = new ArrayList<>();
        Random generator = new Random();

        for (int i = 0; i < 100; i++) {
            Point finalLoc = new Point(generator.nextInt(500), generator.nextInt(300));
            if (doubleMove) {
                animations.add(levelDisplay.getCameraAnimation(finalLoc, new Point(generator.nextInt(500), generator.nextInt(300)), new Dimension(generator.nextInt(500), generator.nextInt(500)), generator.nextInt(1000) + 1000, 0));
            } else {
                animations.add(levelDisplay.getCameraAnimation(finalLoc, finalLoc, new Dimension(generator.nextInt(500), generator.nextInt(500)), generator.nextInt(1000) + 1000, 0));
            }
        }

        levelDisplay.animateCamera(animations.toArray(SynchronousSequence.SynchronousExecutable[]::new));
    }
}