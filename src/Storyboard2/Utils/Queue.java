package Storyboard2.Utils;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Queue extends ExtendableThread {
    private Consumer<ExtendableThread> currentTask = thread -> {};
    private boolean done = true;
    private int limit = -1; // unlimited

    private boolean handlingQueue1 = false;
    private boolean handlingQueue2 = false;
    private final ArrayList<Consumer<ExtendableThread>> queue1 = new ArrayList<>();
    private final ArrayList<Consumer<ExtendableThread>> queue2 = new ArrayList<>();

    private static int count = 0;

    public Queue() {setName("queue#"+count);++count;}
    public Queue(int sizeLimit) {this.limit = sizeLimit;setName("queue#"+count);++count;}

    public boolean roomInQueue() {
        return limit==-1 || queue1.size()+queue2.size() < limit;
    }

    public void add(Consumer<ExtendableThread> task) {
        if (done) {
            done = false;
            currentTask = task;
            restart();
        } else if (roomInQueue()){
            while (true) {
                if (!handlingQueue1) {
                    queue1.add(task);
                    break;
                }

                if (!handlingQueue2) {
                    queue2.add(task);
                    break;
                }
            }
        }
    }

    @Override
    public void task() throws InterruptedException {
        currentTask.accept(this);

        if (queue1.size() > 0) {
            handlingQueue1 = true;
            queue1.forEach(task -> {
                currentTask = task;
                currentTask.accept(this);
            });
            queue1.clear();
            handlingQueue1 = false;
        }

        if (queue2.size() > 0) {
            handlingQueue2 = true;
            queue2.forEach(task -> {
                currentTask = task;
                currentTask.accept(this);
            });
            queue2.clear();
            handlingQueue2 = false;
        }


        currentTask = thread -> {};
        done = true;
        this.wait();
    }
}
