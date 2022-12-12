package Storyboard2.Gui;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;

public abstract class Listener implements KeyListener, MouseListener, MouseWheelListener {
    private final Component host;

    public Listener(Component host) {
        this.host = host;
        this.host.addKeyListener(this);
        this.host.addMouseListener(this);
        this.host.addMouseWheelListener(this);
    }

    public Component getHost() {return host;}
}
