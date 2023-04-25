package Storyboard2.Utils;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Queue extends ExtendableThread {
    private final ArrayList<Consumer<ExtendableThread>> queue = new ArrayList<>();

    private Consumer<ExtendableThread> currentTask = null;

    private volatile boolean done = true;

    private int limit = -1;

    public Queue() {}
    public Queue(int limit) {this.limit = limit-1;}

    public boolean isActive() {
        return !done;
    }

    public void pause() {
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        synchronized (this) {
            notify();
        }
    }

    public void add(Consumer<ExtendableThread> task) {
        if (done) {
            done = false;
            currentTask = task;
            restart();
        } else {
            if (queue.size()<limit||limit<=-1) {
                queue.add(task);
            }
        }
    }

    @Override
    public void task() throws InterruptedException {
        if (currentTask!=null) {currentTask.accept(this);}

        if (queue.size() > 0) {currentTask = queue.remove(0);}

        else {
            currentTask = null;
            done = true;
            this.wait();
        }
    }
}
// falling out of reality