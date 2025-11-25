/* Implement this class. */
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
public class MyHost extends Host {
    private final PriorityQueue<Task> pq = new PriorityQueue<>(new TaskComparator());
    private volatile boolean stop;
    private volatile long workLeft;
    private volatile long timeBeforeExecTask;
    private volatile Task currentTask;
    private final Semaphore info;

    public Task nextTask() {
        return pq.peek();
    }
    public Task getCurrentTask() {
        return currentTask;
    }
    public MyHost() {
        stop = false;
        workLeft = 0;
        currentTask = null;
        timeBeforeExecTask = 0;
        info = new Semaphore(1);
    }


    private class TaskComparator implements Comparator<Task> {
        @Override
        public int compare(Task t1, Task t2) {
            if (t1.getPriority() > t2.getPriority()) {
                return -1;
            } else if (t1.getPriority() < t2.getPriority()) {
                return 1;
            } else if (t1.getStart() < t2.getStart()) {
                return -1;
            } else if (t1.getStart() > t2.getStart()) {
                return 1;
            }
            return 0;
        }
    }

    @Override
    public void run() {
        while (!stop) {
            if (!pq.isEmpty()) {
                try {
                    Task task = pq.peek();
                    info.acquire();
                    timeBeforeExecTask = System.currentTimeMillis();
                    currentTask = task;
                    info.release();
                    synchronized (this) {
                        if (task.isPreemptible()) {
                            long before, timeLeft, after;
                            before = System.currentTimeMillis();
                            this.wait(task.getLeft());
                            after = System.currentTimeMillis();
                            timeLeft = task.getLeft() - (after - before);
                            if (timeLeft > 0) {
                                info.acquire();
                                workLeft -= (after - before);
                                currentTask = null;
                                info.release();
                                task.setLeft(timeLeft);
                                continue;
                            }
                        }
                    }
                    if (!task.isPreemptible()) {
                        sleep(task.getLeft());
                    }
                    task.finish();
                    info.acquire();
                    workLeft -= task.getLeft();
                    currentTask = null;
                    pq.remove(task);
                    info.release();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    @Override
    public void addTask(Task task) {
        workLeft += task.getDuration();
        pq.add(task);
    }

    @Override
    public int getQueueSize() {
        while (info.availablePermits() == 0);
        return pq.size();
    }

    @Override
    public long getWorkLeft() {
        while (info.availablePermits() == 0);
        if (currentTask == null) {
            return workLeft;
        }
        return (workLeft - (System.currentTimeMillis() - timeBeforeExecTask));
    }

    @Override
    public void shutdown() {
        stop = true;
    }
}
