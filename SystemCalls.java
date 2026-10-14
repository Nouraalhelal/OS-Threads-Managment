package OSProject;
// SystemCalls - Simulates OS-level operations for process and memory management
public class SystemCalls {

private Memory mem;   // Reference to system memory
private int time = 0; // Current simulation time (updated by scheduler)

public SystemCalls(Memory m) { this.mem = m; }

// Updates the simulation clock to match current scheduler time
public void setTime(int t) { this.time = t; }

// Logs process creation event
public void create(int id) {
    System.out.println("[Time " + time + "ms] OS: P" + id + " Created");
}

// Logs process termination event
public void terminate(int id) {
    System.out.println("[Time " + time + "ms] OS: P" + id + " Terminated");
}

// Allocates memory to a process via the Memory class and logs the result
public boolean malloc(int id, int size) {
    if (mem.allocate(size)) {
        System.out.println("[Time " + time + "ms] OS: Allocated " + size + "MB to P" + id);
        return true;
    }
    return false; // Allocation failed due to insufficient memory
}

// Frees memory when a process terminates and logs the event
public void free(int id, int size) {
    mem.deallocate(size);
    System.out.println("[Time " + time + "ms] OS: Deallocated " + size + "MB from P" + id);
}

}