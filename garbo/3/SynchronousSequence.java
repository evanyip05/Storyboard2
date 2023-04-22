package Storyboard2.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class SynchronousSequence {
    private final ArrayList<Consumer<ExtendableThread>> actionSequence = new ArrayList<>();
    private final ExtendableThread executor = new ExtendableThread() {
        @Override public void doTask() {action.accept(executor);}
        @Override public boolean shouldWait() {return !running;}
        @Override public boolean shouldRun() {return !kill;}
    };

    private Consumer<ExtendableThread> action = thread -> {};

    private boolean running = false;
    private boolean kill = false;

    @SafeVarargs
    public SynchronousSequence(Consumer<ExtendableThread>... actions) {setActionSequence(actions);}

    public boolean notRunning() {return !running;}

    public void kill() {kill = true;}

    public void run() {
        if (!running) {
            action = composeActions(
                    thread -> running = true,
                    composeActions(actionSequence),
                    thread -> running = false
            );
            executor.restart();
        }
    }
    @SafeVarargs // had warnings for this, not sure what this is, dont see ways it could go wrong
    public final void setActionSequence(Consumer<ExtendableThread>... actionSequence) {
        if (!running) {this.actionSequence.clear(); this.actionSequence.addAll(Arrays.asList(actionSequence));}
    }

    @SafeVarargs // had warnings for this, not sure what this is, dont see ways it could go wrong
    public final Consumer<ExtendableThread> composeActions(Consumer<ExtendableThread>... actions) {
        Consumer<ExtendableThread> res = thread -> {};
        for (Consumer<ExtendableThread> action : actions) {res = res.andThen(action);}
        return res;
    }
    public Consumer<ExtendableThread> composeActions(List<Consumer<ExtendableThread>> actions) {
        Consumer<ExtendableThread> res = thread -> {};
        for (Consumer<ExtendableThread> action : actions) {res = res.andThen(action);}
        return res;
    }
}
