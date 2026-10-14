package OSProject;
// PCB (Process Control Block) - Stores all information about a process
public class PCB {

// Possible states a process can be in during its lifecycle
public enum ProcessState { NEW, READY, RUNNING, TERMINATED }

public int processId;               // Unique process identifier
public ProcessState state = ProcessState.NEW; // Current state of the process
public int burstTime;               // Total CPU time needed (ms)
public int remainingTime;           // Remaining CPU time (used in Round Robin)
public int priority;                // Current priority (1=highest, 30=lowest)
public int originalPriority;        // Original priority saved for aging calculation
public int memoryRequired;          // Memory needed in MB

public int arrivalTime = 0;         // All processes arrive at time 0
public int startTime = -1;          // Time process first gets the CPU
public int completionTime = 0;      // Time process finishes execution
public int waitingTime = 0;         // Total time spent waiting in ready queue
public int turnaroundTime = 0;      // Total time from arrival to completion
public boolean starved = false;     // True if process suffered from starvation

// Constructor: initializes process with its core attributes
public PCB(int id, int burst, int prio, int memory) {
    this.processId = id;
    this.burstTime = burst;
    this.remainingTime = burst;
    this.priority = prio;
    this.originalPriority = prio;
    this.memoryRequired = memory;
}

}