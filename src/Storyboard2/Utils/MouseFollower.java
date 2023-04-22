package Storyboard2.Utils;

import java.awt.*;

/** class that moves some moveable subclass with the mouse pointer */
public class MouseFollower<E extends Component> extends ExtendableThread {
    private final Point mouseInital = MouseInfo.getPointerInfo().getLocation();
    private boolean following = false;
    private E target;

    /** target must implement movable */
    public MouseFollower(E target) {this.target = target;}

    // follow code
    @Override public void task() throws InterruptedException {
        Point cursor = MouseInfo.getPointerInfo().getLocation();
        int mDeltaX = (int) (cursor.getX()-mouseInital.getX());
        int mDeltaY = (int) (cursor.getY()-mouseInital.getY());

        if (target!=null) {
            target.setLocation(target.getLocationOnScreen().x+mDeltaX, target.getLocationOnScreen().y+mDeltaY);
        }

        mouseInital.setLocation(cursor);
        this.wait(16);

        if (!following) {
            this.wait();
        }
    }

    /** change the target */
    public void setTarget(E target) {this.target = target;}

    /** have the target start following the cursor */
    public void startFollowing() {following = true; restart(); mouseInital.setLocation(MouseInfo.getPointerInfo().getLocation());}

    /** have the target stop following the cursor */
    public void stopFollowing() {following = false; }
}

/*
    /** use the target without major side effects (setting the target to something else) still may change the targets state /
    public void useTarget(Consumer<E> action) {if (target!=null) {action.accept(target);}}
 */