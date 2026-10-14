package OSProject;
// Memory - Manages the system RAM allocation (2048 MB total)
public class Memory {

public final int MAX = 2048;  // Total system memory in MB
private int free = MAX;       // Available memory remaining

// Allocates memory to a process if enough space is available (thread-safe)
public synchronized boolean allocate(int size) {
    if (free >= size) { free -= size; return true; }
    return false; // Not enough memory
}

// Releases memory back to the pool when a process finishes (thread-safe)
public synchronized void deallocate(int size) { free += size; }

// Returns current free memory (thread-safe)
public synchronized int getFree() { return free; }

}