package Storyboard2;

import Storyboard2.Core.TileDisplay;
import Storyboard2.Utils.ExtendableThread;

public interface Mover {
    void moveX(int tilesX, TileDisplay display, ExtendableThread thread);
    void moveY(int tilesY, TileDisplay display, ExtendableThread thread);
}
