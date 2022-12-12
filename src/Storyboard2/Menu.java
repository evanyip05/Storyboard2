package Storyboard2;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Menu extends JPanel {

    private final BiConsumer<Graphics, Component> draw;

    public Menu(int width, int height, BiConsumer<Graphics, Component> draw) {
        Dimension d = new Dimension(width, height);

        this.draw = draw;

        setSize(d);
        setPreferredSize(d);
    }

    @Override
    public void paint(Graphics g) {
        draw.accept(g, this);
    }
}
