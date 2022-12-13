package Storyboard2.Utils;

public interface TimedExecutable {
    void execute(ExtendableThread thread);
    default TimedExecutable andThen(TimedExecutable after) {return thread1 -> {execute(thread1);after.execute(thread1);};}
}