package Storyboard2.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SynchronousSequence {
    private final ArrayList<SynchronousExecutable> actionSequence = new ArrayList<>();
    private final ExtendableThread executor = new ExtendableThread() {
        @Override public void execute() {action.execute(executor);}
        @Override public boolean waitCondition() {return !running;}
    };

    private SynchronousExecutable action = thread -> {};

    private boolean running = false;

    public boolean notRunning() {return !running;}

    public void setActionSequence(SynchronousExecutable... actionSequence) {
        if (!running) {
            this.actionSequence.clear(); this.actionSequence.addAll(Arrays.asList(actionSequence));
        }
    }
    public void run() {
        if (!running) {
            action = ((SynchronousExecutable) thread -> running = true).compose(actionSequence).andThen(thread -> running = false);
            executor.restart();
        }
    }

    public interface SynchronousExecutable {
        void execute(ExtendableThread thread);
        default SynchronousExecutable andThen(SynchronousExecutable after) {return thread1 -> {execute(thread1);after.execute(thread1);};}
        default SynchronousExecutable compose(List<SynchronousExecutable> actions) {
            SynchronousExecutable res = this;
            for (SynchronousExecutable action : actions) {res = res.andThen(action);}
            return res;
        }
        default SynchronousExecutable compose(SynchronousExecutable... actions) {
            SynchronousExecutable res = this;
            for (SynchronousExecutable action : actions) {res = res.andThen(action);}
            return res;
        }
    }
}
