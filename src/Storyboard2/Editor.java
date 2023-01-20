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
    private final TileDisplay levelDisplay, tileSetDisplay;

    private boolean scalingWidth = false, graphicsReady = false;

    public Editor(Level level, TileSet tileSet, Dimension levelDisplayDim, Dimension tileSetDisplayDim) {
        levelDisplay = new TileDisplay(level, tileSet, levelDisplayDim.width, levelDisplayDim.height, levelDisplayDim.width, levelDisplayDim.height, tileSet.getTileOutputSize());
        tileSetDisplay = new TileDisplay(new Level(Main.generateLevelFromTileSet(tileSet)), tileSet,tileSetDisplayDim.width, tileSetDisplayDim.height, tileSetDisplayDim.width, tileSetDisplayDim.height, tileSet.getTileOutputSize());

        setUndecorated(true);

        Listener l = new Listener(100);

        MouseFollower<Editor> follower = new MouseFollower<>(this);

        l.addMousePressBind(MouseEvent.BUTTON1, () -> {requestFocus(); follower.startFollowing();});
        l.addMouseReleaseBind(MouseEvent.BUTTON1, follower::stopFollowing);

        l.addKeyBind(KeyEvent.VK_ESCAPE, () -> System.exit(0));
        l.addKeyBind(KeyEvent.VK_SPACE , () -> scalingWidth = !scalingWidth);

        l.addKeyBind(KeyEvent.VK_LEFT , () -> levelDisplay.animateCameraLoc(-tileSet.getTileOutputSize(),   0, 100, 0));
        l.addKeyBind(KeyEvent.VK_RIGHT, () -> levelDisplay.animateCameraLoc( tileSet.getTileOutputSize(),   0, 100, 0));
        l.addKeyBind(KeyEvent.VK_UP   , () -> levelDisplay.animateCameraLoc(  0, -tileSet.getTileOutputSize(), 100, 0));
        l.addKeyBind(KeyEvent.VK_DOWN , () -> levelDisplay.animateCameraLoc(  0,  tileSet.getTileOutputSize(), 100, 0));

        l.addKeyBind(KeyEvent.VK_PAGE_DOWN, () -> {
            levelDisplay.reConstruct(levelDisplay.getCamWidth(), levelDisplay.getCamHeight(),levelDisplay.getDisplayWidth()+1, levelDisplay.getDisplayHeight()+1,tileSet.getTileOutputSize());
            levelDisplay.repaint();
            pack();
        });

        l.addKeyBind(KeyEvent.VK_PAGE_UP, () -> {
            levelDisplay.reConstruct(levelDisplay.getCamWidth(), levelDisplay.getCamHeight(),levelDisplay.getDisplayWidth()-1, levelDisplay.getDisplayHeight()-1,tileSet.getTileOutputSize());
            levelDisplay.repaint();
            pack();
        });

        l.addKeyBind(KeyEvent.VK_COMMA, () -> {
            levelDisplay.reConstruct(levelDisplay.getCamWidth()+1, levelDisplay.getCamHeight()+1,levelDisplay.getDisplayWidth(), levelDisplay.getDisplayHeight(),tileSet.getTileOutputSize());
            levelDisplay.repaint();
        });

        l.addKeyBind(KeyEvent.VK_PERIOD, () -> {
            levelDisplay.reConstruct(levelDisplay.getCamWidth()-1, levelDisplay.getCamHeight()-1,levelDisplay.getDisplayWidth(), levelDisplay.getDisplayHeight(),tileSet.getTileOutputSize());
            levelDisplay.repaint();
        });

        addKeyListener(l);
        addMouseListener(l);

        setLayout(new BorderLayout());

        int totalWidth = levelDisplay.getWidth()+tileSetDisplay.getWidth();

        add(new Menu(totalWidth, 16, (g, host) -> {g.fillRect(0,0,host.getWidth(),16); host.repaint();}), BorderLayout.NORTH);
        add(levelDisplay, BorderLayout.WEST);
        add(tileSetDisplay, BorderLayout.EAST);
        add(new Menu(totalWidth, 48, (g, host) -> {g.fillRect(0,0, host.getWidth(), 48); host.repaint();}), BorderLayout.SOUTH);

        pack();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocation(100,100);
        setResizable(false);
        setVisible(true);

        graphicsReady = true;
    }

    @Override
    public void pack() {
        if (graphicsReady) {
            Component[] components = getContentPane().getComponents();

            if (components.length >= 4) {
                Component top = components[0];
                Component level = components[1];
                Component tileSet = components[2];
                Component bottom = components[3];

                int totalWidth = levelDisplay.getWidth() + tileSetDisplay.getWidth();
                int verticalDiffLevel = level.getHeight() - tileSet.getHeight();
                int verticalDiffTileSet = tileSet.getHeight() - level.getHeight();

                Graphics main = getContentPane().getGraphics();
                Dimension topDim = new Dimension(totalWidth, 16);
                Dimension bottomDim = new Dimension(totalWidth, 48);

                if (verticalDiffLevel > 0) {
                    main.setColor(Color.WHITE);
                    main.fillRect(0, level.getHeight()+top.getHeight(), level.getWidth(), verticalDiffLevel);
                }

                top.setSize(topDim);
                bottom.setSize(bottomDim);

                top.setPreferredSize(topDim);
                bottom.setPreferredSize(bottomDim);
            }
        }

        super.pack();

        // SynchronousSequence.SynchronousExecutable animation = tileSetDisplay.getCameraAnimation(new Point(10, 24), new Point(274, 382), new Dimension(2994, 578), 2748,58687);
    }
}
/*
// cam demo
    public void doRandomAnimation(boolean doubleMove) {
        ArrayList<Consumer<ExtendableThread>> animations = new ArrayList<>();
        Random generator = new Random();

        for (int i = 0; i < 100; i++) {
            Point finalLoc = new Point(generator.nextInt(500), generator.nextInt(300));
            if (doubleMove) {
                animations.add(levelDisplay.getCameraAnimation(finalLoc, new Point(generator.nextInt(500), generator.nextInt(300)), new Dimension(generator.nextInt(500), generator.nextInt(500)), generator.nextInt(1000) + 1000, 0));
            } else {
                animations.add(levelDisplay.getCameraAnimation(finalLoc, finalLoc, new Dimension(generator.nextInt(500), generator.nextInt(500)), generator.nextInt(1000) + 1000, 0));
            }
        }

        //levelDisplay.animateCamera(animations);
    }
 */