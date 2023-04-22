package Storyboard2.Utils;

/** subclass of Thread, allows implementation of a method to be ran on a new thread.
  * restartable using wait/notify syncing, usually used through anon declarations */
public abstract class ExtendableThread extends Thread {

    /** run loop sync block to lock this thread to this action (only so the lock isnt lost)
     *  runs while bool from condition is true
     *  does execute method then checks if it should wait */
    @Override
    public final void run() {
        synchronized (this) {
            while (shouldRun()) {
                try {task();}
                catch (InterruptedException ignore) {}
            }
        }
        System.out.println("thread died");
    }

    /** process that happens if a thread is to be restarted */
    public final void restart() {
        synchronized (this) {
            if (getState().equals(State.NEW)) {start();}
            else {notify();}
        }
    }

    /** task this thread performs. thread param is to give access to this thread w/o decloration */
    public abstract void task() throws InterruptedException;

    /** the condition on which the thread should keep performing the task */
    public boolean shouldRun() {return true;}
}


