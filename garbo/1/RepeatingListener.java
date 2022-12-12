package Storyboard2.Gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class RepeatingListener extends Listener {
    private final ArrayList<Integer> inputs = new ArrayList<>();

    // mouseMapings for mouse event
    private final HashMap<Integer/*buttonNum*/, Consumer<Component>> mouseMap = new HashMap<>();

    // keyMappings for key event
    private final HashMap<Integer/*keyCode*/, Consumer<Component>> keyMap = new HashMap<>();

    // false = released, true = pressed
    private final HashMap<Integer, AtomicReference<Boolean>> keyMapStates = new HashMap<>();
    private final HashMap<Integer, AtomicReference<Boolean>> mouseMapStates = new HashMap<>();

    private final ExtendableThread runner = new ExtendableThread() {
        @Override
        public void execute() {
            keyMap.forEach((keyCode, action) -> {
                if (keyMapStates.get(keyCode).get()) {action.accept(getHost());}
            });

            mouseMap.forEach((button, action) -> {
                if (mouseMapStates.get(button).get()) {action.accept(getHost());}
            });
        }

        @Override
        public boolean waitCondition() {
            AtomicReference<Boolean> trueRef = new AtomicReference<>(true);
            return !keyMapStates.containsValue(trueRef)&&!mouseMapStates.containsValue(trueRef);
        }
    };

    private Runnable onEnable = () -> {}, onDisable = () -> {};

    private boolean enabled = true;
    private int input;

    public RepeatingListener(Component host) {super(host);}

    /** get keys being pressed. uses KeyEvent.keyCode */
    public ArrayList<Integer> getInputs() {return new ArrayList<>(inputs);}

    /** executes bind action once, sets key map key state to true */
    @Override public void keyPressed(KeyEvent e) {
        if (keyMapStates.get(e.getExtendedKeyCode()) != null) {keyMapStates.get(e.getExtendedKeyCode()).set(true);}
        if (runner.getState().equals(Thread.State.TIMED_WAITING)||runner.getState().equals(Thread.State.WAITING)) {runner.restart();}
    }
    /** executes bind action, sets key map key state to false*/
    @Override public void keyReleased(KeyEvent e) {
        if (keyMapStates.get(e.getExtendedKeyCode()) != null) {keyMapStates.get(e.getExtendedKeyCode()).set(false);}
    }
    /** executes bind action once, sets mouse map button state to true */
    @Override public void mousePressed(MouseEvent e) {
        if (mouseMapStates.get(e.getButton()) != null) {mouseMapStates.get(e.getButton()).set(true);}
        if (runner.getState().equals(Thread.State.TIMED_WAITING)||runner.getState().equals(Thread.State.WAITING)) {runner.restart();}
    }
    /** executes bind action, sets mouse map button state to false*/
    @Override public void mouseReleased(MouseEvent e) {
        if (mouseMapStates.get(e.getButton()) != null) {mouseMapStates.get(e.getButton()).set(false);}
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseWheelMoved(MouseWheelEvent e) {}

    /** add bind for button using MouseEvent.button on press*/
    public void addMouseBind(Integer button, Consumer<Component> action) {mouseMap.put(button, action); mouseMapStates.put(button, new AtomicReference<>(false));}
    /** add bind for key using KeyEvent.keyCode on press */
    public void addKeyBind(Integer keyCode, Consumer<Component> action) {keyMap.put(keyCode, action); keyMapStates.put(keyCode, new AtomicReference<>(false));}


    /** add action that happens on disable */
    public void setOnDisable(Runnable onDisable) {this.onDisable = onDisable;}
    /** add action that happens on enable */
    public void setOnEnable(Runnable onEnable) {this.onEnable = onEnable;}

    /** remove mouse press/release binding by button*/
    public void removeMouseBind(Integer button) {mouseMap.remove(button); mouseMapStates.remove(button);}
    /** remove key press/release binding by keyCode*/
    public void removeKeyBind(Integer keyCode) {keyMap.remove(keyCode); keyMapStates.remove(keyCode);}

    /** enable listener binds (listener still aware of states) */
    public void enable() {onEnable.run(); enabled = true;}
    /** disable listener binds (listener still aware of states) */
    public void disable() {onDisable.run(); enabled = false;}

    /** get current input, if multiple keys are pressed, the oldest input gets read */
    public int getCurrentInput() {return input;}

    public boolean disabled() {return !enabled;}
    public boolean enabled() {return enabled;}

    /** check if key is pressed using KeyEvent.keyCode, false if not binded*/
    public boolean keyPressed(Integer keyCode) {return keyMapStates.get(keyCode) != null && keyMapStates.get(keyCode).get();}
    /** check if key is released using KeyEvent.keyCode, true if not binded*/
    public boolean keyReleased(Integer keyCode) {return keyMapStates.get(keyCode) == null || !keyMapStates.get(keyCode).get();}
    /** check if mouse is pressed using MouseEvent.button, false if not binded*/
    public boolean mousePressed(Integer button) {return mouseMapStates.get(button) != null && mouseMapStates.get(button).get();}
    /** check if mouse is released using MouseEvent.button, true if not binded*/
    public boolean mouseReleased(Integer button) {return mouseMapStates.get(button) == null || !mouseMapStates.get(button).get();}
}
