package OSProject;
import java.util.LinkedList;

// Thread 2 - Continuously loads jobs from the job queue to the ready queue
// Only admits a process if enough memory is available
public class JobLoaderThread extends Thread {

private LinkedList<PCB> jobQ, readyQ; // Shared queues
private SystemCalls sys;              // Used for memory allocation
private volatile boolean active = true; // Controls the thread loop

public JobLoaderThread(LinkedList<PCB> j, LinkedList<PCB> r, SystemCalls s) {
    this.jobQ = j; this.readyQ = r; this.sys = s;
}

@Override
public void run() {
    while (active) {
        synchronized (jobQ) {
            if (jobQ.isEmpty()) {
                // Wait if no jobs are available yet
                try { jobQ.wait(100); } catch (Exception e) { active = false; }
            } else {
                PCB job = jobQ.peek(); // Look at next job without removing

                // Check if memory is available before admitting the process
                if (sys.malloc(job.processId, job.memoryRequired)) {
                    jobQ.remove();
                    job.state = PCB.ProcessState.READY;

                    // Move process to ready queue safely
                    synchronized (readyQ) { readyQ.add(job); }
                } else {
                    // Not enough memory, wait and retry
                    try { jobQ.wait(100); } catch (Exception e) { active = false; }
                }
            }
        }
    }
}

// Called by main thread to stop the loader after all processes are done
public void stopLoading() { active = false; }

}