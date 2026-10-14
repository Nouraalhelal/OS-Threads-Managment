package OSProject;

import java.util.ArrayList;

//ResultsTable - Displays the Gantt chart, statistics table, and starvation report
public class ResultsTable {

public static void display(ArrayList<PCB> done, ArrayList<String> gantt, int choice) {

 // Print Gantt chart timeline
 System.out.println("\n--- GANTT CHART TIMELINE ---");
 gantt.forEach(System.out::print);
 System.out.println("|\n");

 boolean isPriority = (choice == 3);

 // Show Starved column only for Priority scheduling as required
 if (isPriority)
     System.out.println("PID | Burst | Start | End | Turnaround | Waiting | Starved");
 else
     System.out.println("PID | Burst | Start | End | Turnaround | Waiting");

 double tw = 0, tt = 0;
 ArrayList<Integer> starvedList = new ArrayList<>(); // Tracks starved process IDs

 // Print one row per process
 for (PCB p : done) {
     if (isPriority) {
         System.out.printf("%-3d | %-5d | %-5d | %-3d | %-10d | %-7d | %s\n",
             p.processId, p.burstTime, p.startTime, p.completionTime,
             p.turnaroundTime, p.waitingTime, (p.starved ? "YES" : "NO"));
         if (p.starved) starvedList.add(p.processId);
     } else {
         System.out.printf("%-3d | %-5d | %-5d | %-3d | %-10d | %-7d\n",
             p.processId, p.burstTime, p.startTime, p.completionTime,
             p.turnaroundTime, p.waitingTime);
     }
     tw += p.waitingTime;
     tt += p.turnaroundTime;
 }

 // Print performance metrics
 System.out.printf("\nAvg Waiting: %.2f ms | Avg Turnaround: %.2f ms\n",
     (tw / done.size()), (tt / done.size()));

 // Print starvation summary for Priority only
 if (isPriority) {
     if (!starvedList.isEmpty())
         System.out.println("\nSTARVATION ALERT: The following processes suffered from starvation: " + starvedList);
     else
         System.out.println("\nSTARVATION REPORT: No processes suffered from starvation.");
 }
}


}
