package OSProject;
import java.util.*;

// Scheduler - Contains all three CPU scheduling algorithms
public class Scheduler {

private LinkedList<PCB> readyQ;       // Shared ready queue
private ArrayList<PCB> done;          // List of completed processes
private SystemCalls sys;              // For OS logging and memory release
private int total, time = 0;          // Total process count and simulation clock
public ArrayList<String> gantt = new ArrayList<>(); // Gantt chart entries

public Scheduler(LinkedList<PCB> r, ArrayList<PCB> d, SystemCalls s, int t) {
    this.readyQ = r; this.done = d; this.sys = s; this.total = t;
}

// SJF (Shortest Job First) - Non-preemptive
// Selects process with shortest burst time; ties broken by process ID (arrival order)
public void sjf() {
    while (done.size() < total) {
        PCB current = null;
        synchronized(readyQ) {
            if (!readyQ.isEmpty()) {
                current = readyQ.get(0);
                for (PCB p : readyQ) {
                    // Pick shortest burst; on tie, pick lower process ID
                    if (p.burstTime < current.burstTime ||
                       (p.burstTime == current.burstTime && p.processId < current.processId)) {
                        current = p;
                    }
                }
                readyQ.remove(current);
            }
        }
        if (current == null) { time++; continue; } // CPU idle
        run(current, current.burstTime);
    }
}

// Round Robin - Preemptive with time quantum = 5ms
public void rr() {
    while (done.size() < total) {
        PCB p = null;
        synchronized(readyQ) { if (!readyQ.isEmpty()) p = readyQ.remove(); }

        if (p == null) { time++; continue; } // CPU idle
        if (p.startTime == -1) p.startTime = time;
        p.state = PCB.ProcessState.RUNNING;

        // Execute for up to 5ms (or remaining time if less)
        int exec = Math.min(5, p.remainingTime);
        gantt.add(String.format("| P%d [t:%d, burst_left:%d] ", p.processId, time, p.remainingTime));

        time += exec;
        p.remainingTime -= exec;

        if (p.remainingTime == 0) finish(p); // Process completed
        else {
            // Process not done, return to back of ready queue
            p.state = PCB.ProcessState.READY;
            synchronized(readyQ) { readyQ.add(p); }
        }
    }
}

// Priority Scheduling - Non-preemptive
// Selects highest priority process (lowest number); ties broken by process ID
// Applies aging every 4ms and checks for starvation continuously
public void priority() {
    while (done.size() < total) {
        PCB current = null;
        synchronized(readyQ) {
            if (!readyQ.isEmpty()) {
                current = readyQ.get(0);
                for (PCB p : readyQ) {
                    // Pick highest priority; on tie, pick lower process ID
                    if (p.priority < current.priority ||
                       (p.priority == current.priority && p.processId < current.processId)) {
                        current = p;
                    }
                }
                readyQ.remove(current);
            }
        }
        if (current == null) { time++; continue; } // CPU idle
        runPriority(current, current.burstTime);
    }
}

// Standard run method for SJF (executes full burst at once)
private void run(PCB p, int burst) {
    if (p.startTime == -1) p.startTime = time;
    p.state = PCB.ProcessState.RUNNING;
    sys.create(p.processId);
    gantt.add(String.format("| P%d [t:%d, burst:%d] ", p.processId, time, burst));
    time += p.burstTime;
    finish(p);
}

// Special run method for Priority - simulates tick-by-tick to apply aging correctly
private void runPriority(PCB p, int burst) {
    if (p.startTime == -1) p.startTime = time;
    p.state = PCB.ProcessState.RUNNING;
    sys.create(p.processId);
    gantt.add(String.format("| P%d [t:%d, burst:%d] ", p.processId, time, burst));

    // Advance time one ms at a time to apply aging and check starvation
    for (int i = 0; i < burst; i++) {
        time++;
        synchronized(readyQ) {
            int qSize = readyQ.size(); // N = current ready queue size
            for (PCB w : readyQ) {
                // Aging: decrease priority number by 1 every 4ms (improves priority)
                if (time % 4 == 0 && w.priority > 1) w.priority--;
                // Starvation: process waited more than N*5ms without running
                if ((time - w.arrivalTime) > (qSize * 5)) w.starved = true;
            }
        }
    }
    finish(p);
}

// Calculates final stats and marks process as terminated
private void finish(PCB p) {
    p.completionTime = time;
    p.turnaroundTime = p.completionTime - p.arrivalTime;
    p.waitingTime = p.turnaroundTime - p.burstTime;
    p.state = PCB.ProcessState.TERMINATED;
    sys.setTime(time);
    sys.terminate(p.processId);
    sys.free(p.processId, p.memoryRequired);
    done.add(p);
}

}