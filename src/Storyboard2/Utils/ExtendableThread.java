package Storyboard2.Utils;

/** subclass of Thread, allows implementation of a method to be ran on a new thread.
  * restartable using wait/notify syncing, usually used through anon declarations */
public abstract class ExtendableThread extends Thread {

    /**
     * run loop sync block to lock this thread to this action (only so the lock isnt lost)
     * runs while bool from condition is true
     * does execute method then checks if it should wait
     */
    @Override
    public final void run() {
        synchronized (this) {
            while (condition()) {
                try {
                    execute();
                    if (waitCondition()) {
                        wait();
                    }
                } catch (InterruptedException ignore) {
                }
            }
        }
    }

    /**
     * process that happens if a thread is to be restarted
     */
    public final void restart() {
        synchronized (this) {
            if (getState().equals(State.NEW)) {
                start();
            }
            executeOnRestart();
            notify();
        }
    }

    /**
     * wrapped wait
     */
    public final void pause(long ms) {
        try {
            wait(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * default code block to be run when the thread resumes, can be overridden
     */
    public void executeOnRestart() {
    }

    /**
     * default condition for which the thread should pause, can be overridden
     */
    public boolean waitCondition() {
        return false;
    }

    /**
     * the condition on which the thread should keep performing the task
     */
    public boolean condition() {
        return true;
    }

    /**
     * task this thread performs
     */
    public abstract void execute() throws InterruptedException;
}
