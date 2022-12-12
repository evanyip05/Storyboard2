package Storyboard2;

import Storyboard2.Core.TileDisplay;
import Storyboard2.Utils.Listener;
import Storyboard2.Utils.MouseFollower;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Editor extends JFrame {
    private final TileDisplay displayRef;

    private boolean scalingWidth = false;

    public Editor(TileDisplay displayRef) {
        this.displayRef = displayRef;

        setUndecorated(true);

        Listener l = new Listener(100);

        MouseFollower<Editor> follower = new MouseFollower<>(this);

        l.addMousePressBind(MouseEvent.BUTTON1, () -> {
            requestFocus();
            follower.startFollowing();
        });

        l.addMouseReleaseBind(MouseEvent.BUTTON1, follower::stopFollowing);

        l.addKeyBind(KeyEvent.VK_ESCAPE, () -> System.exit(0));
        l.addKeyBind(KeyEvent.VK_SPACE , () -> scalingWidth = !scalingWidth);
        l.addKeyBind(KeyEvent.VK_SLASH , () -> displayRef.setCameraPos(0,0));

        l.addKeyBind(KeyEvent.VK_LEFT , () -> displayRef.animateCamera(-1, 0,100, 0, false));
        l.addKeyBind(KeyEvent.VK_RIGHT, () -> displayRef.animateCamera( 1, 0,100, 0, false));
        l.addKeyBind(KeyEvent.VK_UP   , () -> displayRef.animateCamera( 0,-1,100, 0, false));
        l.addKeyBind(KeyEvent.VK_DOWN , () -> displayRef.animateCamera( 0, 1,100, 0, false));

        l.addKeyBind(KeyEvent.VK_PAGE_UP, () -> {
            displayRef.redefine(
                    displayRef.getDisplayedTilesX()+(scalingWidth?(displayRef.getDisplayedTilesX()-1>0?-1:0):0),
                    displayRef.getDisplayedTilesY()+(scalingWidth?0:(displayRef.getDisplayedTilesY()-1>0?-1:0)),
                    displayRef.getTileSize()
            );
            displayRef.nextFrame();

            pack();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        l.addKeyBind(KeyEvent.VK_PAGE_DOWN, () -> {
            displayRef.redefine(
                    displayRef.getDisplayedTilesX()+(scalingWidth?1:0),
                    displayRef.getDisplayedTilesY()+(scalingWidth?0:1),
                    displayRef.getTileSize()
            );
            displayRef.nextFrame();

            pack();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        addKeyListener(l);
        addMouseListener(l);

        setLayout(new BorderLayout());

        add(new Menu(216, 16, (g, host) -> {
            g.fillRect(0,0,216,16);
            host.repaint();
        }), BorderLayout.PAGE_START);
        add(displayRef,BorderLayout.LINE_START);
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