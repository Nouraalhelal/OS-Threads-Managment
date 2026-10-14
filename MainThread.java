package OSProject;

import java.util.*;

//MainThread - Entry point of the program, coordinates all threads and runs the scheduler
public class MainThread {

//Shared data structures accessible by all threads
static LinkedList<PCB> jobQ = new LinkedList<>();    // Jobs waiting to be admitted
static LinkedList<PCB> readyQ = new LinkedList<>();  // Jobs admitted and ready to run
static ArrayList<PCB> done = new ArrayList<>();      // Completed processes

public static void main(String[] args) throws Exception {

 Memory m = new Memory();       // System memory (2048 MB)
 SystemCalls s = new SystemCalls(m); // OS interface for logging and memory ops

 // Create Thread 1 (file reader) and Thread 2 (job loader)
 FileReaderThread fr = new FileReaderThread(jobQ);
 JobLoaderThread jl = new JobLoaderThread(jobQ, readyQ, s);

 // Start Thread 1 and wait for it to finish reading all processes
 fr.start();
 fr.join();

 // Count total processes after file is fully read
 int totalProcesses = jobQ.size();

 // Start Thread 2 to begin admitting processes to memory
 jl.start();

 // Wait until at least one process is admitted to the ready queue before scheduling
 while (readyQ.isEmpty() && !jobQ.isEmpty()) {
     Thread.sleep(10);
 }

 // Ask user to select a scheduling algorithm
 System.out.print("Select: 1.SJF | 2.Round Robin | 3.Priority: ");
 int choice = new Scanner(System.in).nextInt();

 // Run the selected scheduling algorithm (main thread acts as the scheduler)
 Scheduler sc = new Scheduler(readyQ, done, s, totalProcesses);
 if (choice == 1) sc.sjf();
 else if (choice == 2) sc.rr();
 else if (choice == 3) sc.priority();
 else System.out.println("Invalid choice.");

 // Stop the loader thread after scheduling is complete
 jl.stopLoading();

 // Display Gantt chart, results table, and performance metrics
 if (choice >= 1 && choice <= 3) {
     ResultsTable.display(done, sc.gantt, choice);
 }
}

}