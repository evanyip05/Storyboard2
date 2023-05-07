package Storyboard2.Utils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.function.Consumer;

public class Listener implements KeyListener, MouseListener {

    private final HashMap<Integer, Boolean> keyStates = new HashMap<>();
    private final HashMap<Integer, Boolean> mouseStates = new HashMap<>();

    private final HashMap<Integer, Runnable> keyBinds = new HashMap<>();
    private final HashMap<Integer, Runnable> mouseBinds = new HashMap<>();

    private final ExtendableThread runner = new ExtendableThread() {
        @Override public void task() throws InterruptedException {
            boolean noActiveKeys = !keyStates.containsValue(true);
            boolean noActiveButtons = !mouseStates.containsValue(true);
            if (noActiveKeys&&noActiveButtons) {wait();}
            if (!noActiveKeys) {keyStates.forEach((keycode, state) -> {if (state) {keyBinds.get(keycode).run();}});}
            if (!noActiveButtons) {mouseStates.forEach((button, state) -> {if (state) {mouseBinds.get(button).run();}});}
        }
    };

    private Consumer<KeyEvent> onKeyType    = e -> {};
    private Consumer<KeyEvent> onKeyPress   = e -> {};
    private Consumer<KeyEvent> onKeyRelease = e -> {};

    private Consumer<MouseEvent> onMouseClick   = e -> {};
    private Consumer<MouseEvent> onMousePress   = e -> {};
    private Consumer<MouseEvent> onMouseRelease = e -> {};
    private Consumer<MouseEvent> onMouseEnter   = e -> {};
    private Consumer<MouseEvent> onMouseExit    = e -> {};

    public Listener() {runner.restart();}

    @Override public void keyTyped(KeyEvent e)    {onKeyType.accept(e);   }
    @Override public void keyPressed(KeyEvent e)  {onKeyPress.accept(e);   if (keyStates.containsKey(e.getExtendedKeyCode())){keyStates.replace(e.getExtendedKeyCode(), true);} if (runner.getState().equals(Thread.State.WAITING)){runner.restart();}}
    @Override public void keyReleased(KeyEvent e) {onKeyRelease.accept(e); if (keyStates.containsKey(e.getExtendedKeyCode())){keyStates.replace(e.getExtendedKeyCode(), false);}}

    @Override public void mouseClicked(MouseEvent e)  {onMouseClick.accept(e);}
    @Override public void mousePressed(MouseEvent e)  {onMousePress.accept(e);   if (mouseStates.containsKey(e.getButton())){mouseStates.replace(e.getButton(), true);} if (runner.getState().equals(Thread.State.WAITING)){runner.restart();}}
    @Override public void mouseReleased(MouseEvent e) {onMouseRelease.accept(e); if (mouseStates.containsKey(e.getButton())){mouseStates.replace(e.getButton(), false);}}
    @Override public void mouseEntered(MouseEvent e)  {onMouseEnter.accept(e);}
    @Override public void mouseExited(MouseEvent e)   {onMouseExit.accept(e);}

    public void addKeyBind(int keycode, Runnable action) {keyBinds.put(keycode, action); if (!keyStates.containsKey(keycode)) {keyStates.put(keycode, false);}}
    public void addMouseBind(int button, Runnable action) {mouseBinds.put(button, action); if (!mouseStates.containsKey(button)) {mouseStates.put(button, false);}}

    public void setOnKeyType(Consumer<KeyEvent> onKeyType) {this.onKeyType = onKeyType;}
    public void setOnKeyPress(Consumer<KeyEvent> onKeyPress) {this.onKeyPress = onKeyPress;}
    public void setOnKeyRelease(Consumer<KeyEvent> onKeyRelease) {this.onKeyRelease = onKeyRelease;}
    public void setOnMouseClick(Consumer<MouseEvent> onMouseClick) {this.onMouseClick = onMouseClick;}
    public void setOnMousePress(Consumer<MouseEvent> onMousePress) {this.onMousePress = onMousePress;}
    public void setOnMouseRelease(Consumer<MouseEvent> onMouseRelease) {this.onMouseRelease = onMouseRelease;}
    public void setOnMouseEnter(Consumer<MouseEvent> onMouseEnter) {this.onMouseEnter = onMouseEnter;}
    public void setOnMouseExit(Consumer<MouseEvent> onMouseExit) {this.onMouseExit = onMouseExit;}

    public boolean keyPressed(int keycode) {return keyStates.get(keycode);}
    public boolean buttonPressed(int button) {return mouseStates.get(button);}
}
