package Storyboard2.Utils;

import java.awt.event.*;
import java.util.HashMap;
import java.util.Objects;

/** edited listener, executes actions while key is pressed */
public class Listener implements KeyListener, MouseListener, MouseWheelListener {

    /** executes <b>while</b> key is being held */
    private final HashMap<Integer, Runnable> keyBinds = new HashMap<>();
    /** executes <b>while</b> button is being held */
    private final HashMap<Integer, Runnable> mouseBinds = new HashMap<>();

    /** executes <b>on</b> key release */
    private final HashMap<Integer, Runnable> keyReleaseBinds = new HashMap<>();
    /** executes <b>on</b> button release */
    private final HashMap<Integer, Runnable> mouseReleaseBinds = new HashMap<>();

    /** executes <b>on</b> key press */
    private final HashMap<Integer, Runnable> keyPressBinds = new HashMap<>();
    /** executes <b>on</b> button press */
    private final HashMap<Integer, Runnable> mousePressBinds = new HashMap<>();

    private final HashMap<Integer, BoolWrapper> keyStates = new HashMap<>();
    private final HashMap<Integer, BoolWrapper> mouseStates = new HashMap<>();

    private final ExtendableThread runner = new ExtendableThread() {
        @Override public void task() throws InterruptedException {
            keyBinds.forEach((key, action) -> {if (keyStates.get(key).get()) {action.run();}});
            mouseBinds.forEach((button, action) -> {if (mouseStates.get(button).get()) {action.run();}});
            this.wait(actionDelay);

            if (mouseNotPressed()&&keysNotPressed()) {
                this.wait();
            }
        }
    };

    private final int actionDelay;

    /** approxamately number of actions to occur per second, to define delay for thread (delay = 1000/actionsPerSec) */
    public Listener(int actionDelay) {this.actionDelay = actionDelay;}

    /** in order, run on-press code, set button state, then start while op */
    @Override public void mousePressed(MouseEvent e) {
        if (mousePressBinds.containsKey(e.getButton())) {mousePressBinds.get(e.getButton()).run();}
        if (mouseStates.containsKey(e.getButton())) {mouseStates.get(e.getButton()).set(true);}
        runner.restart();
    }

    /** in order, run on-press code, set key state, then start while op */
    @Override public void keyPressed(KeyEvent e) {
        if (keyPressBinds.containsKey(e.getExtendedKeyCode())) {keyPressBinds.get(e.getExtendedKeyCode()).run();}
        if (keyStates.containsKey(e.getExtendedKeyCode())) {keyStates.get(e.getExtendedKeyCode()).set(true);}
        runner.restart();
    }

    /** in order, run on-release code, then set button state*/
    @Override public void mouseReleased(MouseEvent e) {
        if (mouseReleaseBinds.containsKey(e.getButton())) {mouseReleaseBinds.get(e.getButton()).run();}
        if (mouseStates.containsKey(e.getButton())) {mouseStates.get(e.getButton()).set(false);}
    }

    /** in order, run on-release code, then set key state*/
    @Override public void keyReleased(KeyEvent e) {
        if (keyReleaseBinds.containsKey(e.getExtendedKeyCode())) {keyReleaseBinds.get(e.getExtendedKeyCode()).run();}
        if (keyStates.containsKey(e.getExtendedKeyCode())) {keyStates.get(e.getExtendedKeyCode()).set(false);}
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseWheelMoved(MouseWheelEvent e) {}

    /** check if any of the mouse buttons are pressed */
    public boolean mouseNotPressed() {return !mouseStates.containsValue(new BoolWrapper(true));}
    /** check if any of the keys are pressed */
    public boolean keysNotPressed()  {return !keyStates.containsValue(new BoolWrapper(true));}

    /** add an action associated with a key */
    public void addKeyBind(int keyCode, Runnable action) {keyBinds.put(keyCode, action); if(!keyStates.containsKey(keyCode)){keyStates.put(keyCode, new BoolWrapper(false));}}
    /** add an action associated with a mouse button */
    public void addMouseBind(int button, Runnable action) {mouseBinds.put(button, action); if(!mouseStates.containsKey(button)){mouseStates.put(button, new BoolWrapper(false));}}

    /** add a release action associated with a key */
    public void addKeyReleaseBind(int keyCode, Runnable action) {keyReleaseBinds.put(keyCode, action); if(!keyStates.containsKey(keyCode)){keyStates.put(keyCode, new BoolWrapper(false));}}
    /** add a release action associated with a mouse button */
    public void addMouseReleaseBind(int button, Runnable action) {mouseReleaseBinds.put(button, action); if(!mouseStates.containsKey(button)){mouseStates.put(button, new BoolWrapper(false));}}

    /** add a release action associated with a key */
    public void addKeyPressBind(int keyCode, Runnable action) {keyPressBinds.put(keyCode, action); if(!keyStates.containsKey(keyCode)){keyStates.put(keyCode, new BoolWrapper(false));}}
    /** add a release action associated with a mouse button */
    public void addMousePressBind(int button, Runnable action) {mousePressBinds.put(button, action); if(!mouseStates.containsKey(button)){mouseStates.put(button, new BoolWrapper(false));}}

    /** boolean wrapper with equals override for bool value */
    private static class BoolWrapper {
        private boolean value;
        public BoolWrapper(boolean initalValue) {value = initalValue;}
        public void set(boolean value) {this.value = value;}
        public boolean get() {return value;}

        @Override public boolean equals(Object o) {
            if (!(o instanceof BoolWrapper)) {return false;}
            else {return value == ((BoolWrapper) o).get();}
        }

        @Override public int hashCode() {return Objects.hash(value);}
    }
}
