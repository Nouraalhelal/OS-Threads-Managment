package OSProject;
import java.io.*;
import java.util.LinkedList;

// Thread 1 - Reads job.txt, creates PCBs, and fills the job queue, then terminates
public class FileReaderThread extends Thread {

private LinkedList<PCB> jobQ; // Shared job queue

public FileReaderThread(LinkedList<PCB> jq) { this.jobQ = jq; }

@Override
public void run() {
    try (BufferedReader br = new BufferedReader(new FileReader("job.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();

            // Skip empty lines and header/footer tags like [Begin] and [End]
            if (!line.isEmpty() && !line.startsWith("[")) {

                // Parse format: ID:burst:priority;memory
                String[] p = line.split(";");
                String[] info = p[0].split(":");

                PCB pcb = new PCB(
                    Integer.parseInt(info[0].trim()),  // Process ID
                    Integer.parseInt(info[1].trim()),  // Burst time
                    Integer.parseInt(info[2].trim()),  // Priority
                    Integer.parseInt(p[1].trim())      // Memory (MB)
                );

                // Add PCB to job queue safely and notify the loader thread
                synchronized (jobQ) {
                    jobQ.add(pcb);
                    jobQ.notify();
                }
            }
        }
    } catch (IOException e) { System.err.println("Error: " + e.getMessage()); }
    // Thread terminates naturally after all lines are read
}

}