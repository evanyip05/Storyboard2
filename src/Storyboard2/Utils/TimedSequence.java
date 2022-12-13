package Storyboard2.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class TimedSequence {
    private final ArrayList<TimedExecutable> actionSequence = new ArrayList<>();
    private final ExtendableThread executor = new ExtendableThread() {
        @Override public void execute() {action.execute(executor);}
        @Override public boolean waitCondition() {return !running;}
    };

    private TimedExecutable action = thread -> {};

    private boolean running = false;

    public boolean notRunning() {return !running;}

    public void setActionSequence(TimedExecutable... actionSequence) {if (!running) {this.actionSequence.clear(); this.actionSequence.addAll(Arrays.asList(actionSequence));}}
    public void addAction(TimedExecutable action) {if (!running) {actionSequence.add(action);}}
    public void run() {
        if (!running) {
            TimedExecutable res = thread -> {};
            for (TimedExecutable animation : actionSequence) {res = res.andThen(animation);}

            running = true;
            action = res.andThen(thread -> running = false);
            executor.restart();
        }
    }


}
