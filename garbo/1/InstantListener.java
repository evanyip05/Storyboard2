package Storyboard2.Gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

// add do while some condition is met

/** key and mouse listener in one with key and mouseButton bindings */
public class InstantListener extends Listener {
    private final ArrayList<Integer> inputs = new ArrayList<>();

    // mouseMaps for press/release
    private final HashMap<Integer/*buttonNum*/, BiConsumer<Component, MouseEvent>> mousePressMap = new HashMap<>();
    private final HashMap<Integer/*buttonNum*/, BiConsumer<Component, MouseEvent>> mouseReleaseMap = new HashMap<>();

    // keyMaps for press/release
    private final HashMap<Integer/*keyCode*/, BiConsumer<Component, KeyEvent>> keyPressMap = new HashMap<>();
    private final HashMap<Integer/*keyCode*/, BiConsumer<Component, KeyEvent>> keyReleaseMap = new HashMap<>();

    // false = released, true = pressed
    private final HashMap<Integer, AtomicReference<Boolean>> keyMapStates = new HashMap<>();
    private final HashMap<Integer, AtomicReference<Boolean>> mouseMapStates = new HashMap<>();


    private Runnable onEnable = () -> {}, onDisable = () -> {};

    private boolean enabled = true;
    private int input;

    public InstantListener(Component host) {super(host);}

    /** get keys being pressed. uses KeyEvent.keyCode */
    public ArrayList<Integer> getInputs() {return new ArrayList<>(inputs);}

    /** executes bind action once, sets key map key state to true */
    @Override public void keyPressed(KeyEvent e) {
        if (!inputs.contains(e.getExtendedKeyCode())) {inputs.add(e.getExtendedKeyCode());}
        input = e.getExtendedKeyCode();
        if (enabled) {if (keyPressMap.get(e.getExtendedKeyCode()) != null) {keyPressMap.get(e.getExtendedKeyCode()).accept(getHost(), e);}}
        if (keyMapStates.get(e.getExtendedKeyCode()) != null) {keyMapStates.get(e.getExtendedKeyCode()).set(true);}
    }
    /** executes bind action, sets key map key state to false*/
    @Override public void keyReleased(KeyEvent e) {
        inputs.removeIf(input -> input.equals(e.getExtendedKeyCode()));
        input = (inputs.size()>0)?inputs.get(0):0; // sets current input to if there are still keys being held to the next held else 0 for no key
        if (enabled) {if (keyReleaseMap.get(e.getExtendedKeyCode()) != null) {keyReleaseMap.get(e.getExtendedKeyCode()).accept(getHost(), e);}}
        if (keyMapStates.get(e.getExtendedKeyCode()) != null) {keyMapStates.get(e.getExtendedKeyCode()).set(false);}
    }
    /** executes bind action once, sets mouse map button state to true */
    @Override public void mousePressed(MouseEvent e) {
        if (enabled) {if (mousePressMap.get(e.getButton()) != null) {mousePressMap.get(e.getButton()).accept(getHost(), e);}}
        if (mouseMapStates.get(e.getButton()) != null) {mouseMapStates.get(e.getButton()).set(true);}
    }
    /** executes bind action, sets mouse map button state to false*/
    @Override public void mouseReleased(MouseEvent e) {
        if (enabled) {if (mouseReleaseMap.get(e.getButton()) != null) {mouseReleaseMap.get(e.getButton()).accept(getHost(), e);}}
        if (mouseMapStates.get(e.getButton()) != null) {mouseMapStates.get(e.getButton()).set(false);}
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseWheelMoved(MouseWheelEvent e) {}

    /** add bind for button using MouseEvent.button on press*/
    public void addMousePressBind(Integer button, BiConsumer<Component, MouseEvent> action) {mousePressMap.put(button, action); mouseMapStates.put(button, new AtomicReference<>(false));}
    /** add bind for button using MouseEvent.button on release*/
    public void addMouseReleaseBind(Integer button, BiConsumer<Component, MouseEvent> action) {mouseReleaseMap.put(button, action);}
    /** add bind for key using KeyEvent.keyCode on press */
    public void addKeyPressBind(Integer keyCode, BiConsumer<Component, KeyEvent> action) {keyPressMap.put(keyCode, action); keyMapStates.put(keyCode, new AtomicReference<>(false));}
    /** add bind for key using KeyEvent.keyCode on release */
    public void addKeyReleaseBind(Integer keyCode, BiConsumer<Component, KeyEvent> action) {keyReleaseMap.put(keyCode, action);}

    /** add action that happens on disable */
    public void setOnDisable(Runnable onDisable) {this.onDisable = onDisable;}
    /** add action that happens on enable */
    public void setOnEnable(Runnable onEnable) {this.onEnable = onEnable;}

    /** remove mouse press/release binding by button*/
    public void removeMouseBind(Integer button) {mousePressMap.remove(button); mouseReleaseMap.remove(button); mouseMapStates.remove(button);}
    /** remove key press/release binding by keyCode*/
    public void removeKeyBind(Integer keyCode) {keyPressMap.remove(keyCode); keyReleaseMap.remove(keyCode); keyMapStates.remove(keyCode);}

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
