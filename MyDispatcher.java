/* Implement this class. */

import java.util.List;
class RoundRobin {
    private static RoundRobin single_instance = null;
    private int counter;
    private RoundRobin() {
        counter = 0;
    };
    public static RoundRobin getInstance()
    {
        if (single_instance == null) {
            single_instance = new RoundRobin();
        }
        return single_instance;
    }

    public int getCounter() {
        return counter;
    }
    public void setCounter(int counter) {
        this.counter = counter;
    }
}


public class MyDispatcher extends Dispatcher {

    private final int noHosts = hosts.size();
    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    public void checkToNotify(MyHost host) {
        Task task = host.getCurrentTask();
        Task nextTask = host.nextTask();
        if (task != nextTask) {
            synchronized (host) {
                host.notify();
            }
        }
    }
    @Override
    public void addTask(Task task) {
        if (algorithm == SchedulingAlgorithm.ROUND_ROBIN) {
            synchronized (TaskGenerator.class) {
                RoundRobin roundRobin = RoundRobin.getInstance();
                hosts.get(roundRobin.getCounter() % noHosts).addTask(task);
                checkToNotify((MyHost) hosts.get(roundRobin.getCounter() % noHosts));
                roundRobin.setCounter((roundRobin.getCounter() + 1) % noHosts);
            }
        }
        if (algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) {
            int hostID = 0;
            int minQSize = hosts.get(0).getQueueSize();
            for (int i = 1; i < noHosts; i++) {
                if (hosts.get(i).getQueueSize() < minQSize) {
                    hostID = i;
                    minQSize = hosts.get(i).getQueueSize();
                }
            }
            hosts.get(hostID).addTask(task);
            checkToNotify((MyHost) hosts.get(hostID));
        }
        if (algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) {
            int hostID = -1;
            if (task.getType() == TaskType.SHORT) {
                hostID = 0;
            }
            if (task.getType() == TaskType.MEDIUM) {
                hostID = 1;
            }
            if (task.getType() == TaskType.LONG) {
                hostID = 2;
            }
            hosts.get(hostID).addTask(task);
            checkToNotify((MyHost) hosts.get(hostID));
        }
        if (algorithm == SchedulingAlgorithm.LEAST_WORK_LEFT) {
            int hostID = 0;
            long minLeftWork = hosts.get(0).getWorkLeft();
            for (int i = 1; i < noHosts; i++) {
                if (hosts.get(i).getWorkLeft() < minLeftWork) {
                    hostID = i;
                    minLeftWork = hosts.get(i).getWorkLeft();
                }
            }
            hosts.get(hostID).addTask(task);
            checkToNotify((MyHost) hosts.get(hostID));
        }
    }
}
