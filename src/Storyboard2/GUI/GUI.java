package Storyboard2.GUI;

import Storyboard2.Core.Level;
import Storyboard2.Core.TileData;
import Storyboard2.Core.TileDisplay;
import Storyboard2.Core.TileSet;
import Storyboard2.Utils.Queue;
import Storyboard2.Utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class GUI extends JFrame {
    private final JFrame frame = new JFrame();
    private final MouseFollower<JFrame> frameFollower = new MouseFollower<>(frame);

    private final TileSet tileset;
    private final Level level;

    private final TileDisplay tilesetDisplay;
    private final TileDisplay levelDisplay;
    private final Cursor tilesetCursor;
    private final Cursor levelCursor;
    private final Menu header;
    private final Menu footer;

    private final Queue moveQueue = new Queue(1);

    private Cursor currentCursor;
    private TileDisplay currentDisplay;

    private int moveTime = 200;
    private int tileOutputSize;

    private boolean cursorLocked = false;

    private static class Menu extends Component {
        private final Consumer<Graphics> render;
        public Menu(Dimension size, Consumer<Graphics> render) {setSize(size); setPreferredSize(size); this.render=render;}
        @Override public void paint(Graphics g) {render.accept(g);}
    }

    public GUI(Level level, TileSet tileset) {
        this.tileOutputSize = tileset.getTileOutputSize();

        this.levelDisplay = new TileDisplay(level, tileset, 16,16);
        this.tilesetDisplay = new TileDisplay(new Level(generateLevelFromTileSet(tileset)), tileset, 8,4);
        this.header = new Menu(new Dimension(23*tileOutputSize,24), drawHeader());
        this.footer = new Menu(new Dimension(23*tileOutputSize,tileOutputSize*4), drawFooter());

        this.levelCursor = new Cursor(tileOutputSize, new Point(0,0), new Color(0,0,0,120));
        this.tilesetCursor = new Cursor(tileOutputSize, new Point(0,0), new Color(255,255,255,120));

        this.levelCursor.setDisplayPos(7*tileOutputSize,7*tileOutputSize);
        this.levelCursor.setRealPos(7,7);
        this.tilesetCursor.setDisplayPos(4*tileOutputSize,1*tileOutputSize);
        this.tilesetCursor.setRealPos(4,2);

        this.currentDisplay = this.levelDisplay;
        this.currentCursor = this.levelCursor;

        this.tileset = tileset;
        this.level = level;

        this.levelDisplay.addInbetween(levelCursor::draw,0);// always ends up working on the highest layer, no lower
        this.tilesetDisplay.addInbetween(tilesetCursor::draw, 0);

        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        frame.add(this.header, BorderLayout.PAGE_START);
        frame.add(this.tilesetDisplay, BorderLayout.LINE_END);
        frame.add(this.levelDisplay, BorderLayout.CENTER);
        frame.add(this.footer, BorderLayout.PAGE_END);

        setupListeners();

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.levelDisplay.rescale(0,-tileOutputSize,0,-tileOutputSize,1000);
        this.tilesetDisplay.rescale(0,0,0,-tileOutputSize,1000);
        this.levelDisplay.panProjection(tileOutputSize/2, tileOutputSize/2, 1000);
        this.tilesetDisplay.panProjection(-tileOutputSize/2, tileOutputSize/2, 1000);
    }

    private void setupListeners() {
        Listener l = new Listener();

        l.addKeyBind(KeyEvent.VK_ESCAPE,    () -> System.exit(0));
        l.addKeyBind(KeyEvent.VK_UP,        () -> moveQueue.add(((Consumer<ExtendableThread>) (t -> {currentCursor.moveRealPos(0,-1); footer.repaint(); currentDisplay.move(0, -1, moveTime);})).andThen(currentCursor.moveCursor(0,-1, moveTime))));
        l.addKeyBind(KeyEvent.VK_LEFT,      () -> moveQueue.add(((Consumer<ExtendableThread>) (t -> {currentCursor.moveRealPos(-1,0); footer.repaint(); currentDisplay.move(-1, 0, moveTime);})).andThen(currentCursor.moveCursor(-1,0, moveTime))));
        l.addKeyBind(KeyEvent.VK_DOWN,      () -> moveQueue.add(((Consumer<ExtendableThread>) (t -> {currentCursor.moveRealPos(0,1); footer.repaint(); currentDisplay.move(0, 1, moveTime);})).andThen(currentCursor.moveCursor(0,1, moveTime))));
        l.addKeyBind(KeyEvent.VK_RIGHT,     () -> moveQueue.add(((Consumer<ExtendableThread>) (t -> {currentCursor.moveRealPos(1,0); footer.repaint(); currentDisplay.move(1, 0, moveTime);})).andThen(currentCursor.moveCursor(1,0, moveTime))));
        l.addKeyBind(KeyEvent.VK_Z,         () -> {
            if (!currentDisplay.equals(tilesetDisplay)) {
                level.editInfo(levelCursor.getRealTileX(), levelCursor.getRealTileY(), TileData.DISPLAY, tilesetDisplay.getTileInfo(tilesetCursor.getRealTileX(), tilesetCursor.getRealTileY(), TileData.DISPLAY));
                levelDisplay.generateLayers();
                levelDisplay.repaint();
                footer.repaint();
            }
        });

        l.setOnMousePress(e -> {if (e.getButton()==1) {frame.requestFocus(); frameFollower.startFollowing();}});
        l.setOnMouseRelease(e -> {if (e.getButton()==1) {frameFollower.stopFollowing();}});
        l.setOnKeyPress(e -> {
            switch (e.getExtendedKeyCode()) {
                case KeyEvent.VK_SHIFT:
                    if (!cursorLocked) {
                        currentDisplay=((currentDisplay.equals(levelDisplay))?tilesetDisplay:levelDisplay); currentCursor=((currentCursor.equals(levelCursor))?tilesetCursor:levelCursor);
                        levelCursor.setColor(currentCursor.equals(levelCursor)?new Color(0,0,0,120):new Color(255,255,255,120));
                        tilesetCursor.setColor(currentCursor.equals(levelCursor)?new Color(255,255,255,120):new Color(0,0,0,120));
                        levelDisplay.repaint();
                        tilesetDisplay.repaint();
                    }
                    cursorLocked = true;
                    break;
                case KeyEvent.VK_SPACE:
                    currentDisplay=((currentDisplay.equals(levelDisplay))?tilesetDisplay:levelDisplay); currentCursor=((currentCursor.equals(levelCursor))?tilesetCursor:levelCursor);
                    levelCursor.setColor(currentCursor.equals(levelCursor)?new Color(0,0,0,120):new Color(255,255,255,120));
                    tilesetCursor.setColor(currentCursor.equals(levelCursor)?new Color(255,255,255,120):new Color(0,0,0,120));
                    levelDisplay.repaint();
                    tilesetDisplay.repaint();
                    break;
                case KeyEvent.VK_PAGE_DOWN: currentDisplay.zoom(-((2*tileOutputSize)+0.0)/currentDisplay.getProjectionDim().width, moveTime); break;
                case KeyEvent.VK_PAGE_UP: currentDisplay.zoom(((2*tileOutputSize)+0.0)/currentDisplay.getProjectionDim().width, moveTime); break;
            }
        });
        l.setOnKeyRelease(e -> {
            switch (e.getExtendedKeyCode()) {
                case KeyEvent.VK_SHIFT:
                    currentDisplay=((currentDisplay.equals(levelDisplay))?tilesetDisplay:levelDisplay); currentCursor=((currentCursor.equals(levelCursor))?tilesetCursor:levelCursor);
                    levelCursor.setColor(currentCursor.equals(levelCursor)?new Color(0,0,0,120):new Color(255,255,255,120));
                    tilesetCursor.setColor(currentCursor.equals(levelCursor)?new Color(255,255,255,120):new Color(0,0,0,120));
                    levelDisplay.repaint();
                    tilesetDisplay.repaint();
                    cursorLocked = false;
            }
        });

        frame.addKeyListener(l);
        frame.addMouseListener(l);
    }

    private Consumer<Graphics> drawHeader() {
        return g -> {};
    }

    private Consumer<Graphics> drawFooter() {
        return g -> {
            g.drawImage(levelDisplay.getTileImage(levelCursor.getRealTileX(), levelCursor.getRealTileY()), 0,0, footer.getHeight(), footer.getHeight(), null);
            g.drawImage(tilesetDisplay.getTileImage(tilesetCursor.getRealTileX(), tilesetCursor.getRealTileY()), footer.getHeight()+tileOutputSize,0, footer.getHeight(), footer.getHeight(), null);
        };
    }

    public static String generateLevelFromTileSet(TileSet tileSet) {return generateLevel(tileSet.getWidth(), tileSet.getHeight());}

    public static String generateLevel(int width, int height) {
        String res = "";

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                res+=((width*y)+x+1)+":0:0:0,";
            }
            res = res.substring(0, res.length()-1)+";\n";
        }

        return res.substring(0,res.length()-1);
    }

    public static void writeLevelToFile(Level level, String levelDir) {

        String levelContent ="";

        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                levelContent += level.getInfo(x,y) + ",";
            }
            levelContent=levelContent.substring(0, levelContent.length()-1) + ";\n";
        }

        new TextFile(levelDir).writeContent(levelContent);
    }
}
