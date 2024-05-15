/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.scheduler;

/**
 *
 * @author bahar
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Comparator;
import java.io.File;
import java.io.FileNotFoundException;

class Process {
    int id;
    int arrivalTime;
    int burstTime;
    int endTime = 0;
    int TAT = 0;
    int waitingTime = 0;
    int originalBurstTime;

    Process(int id, int arrivalTime, int burstTime, int originalBurstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.originalBurstTime = originalBurstTime;
    }
}

public class Scheduler {

    public static void main(String[] args) {
        String filename = "file.txt";
        ArrayList<Process> processes = new ArrayList<>();
        readFile(filename, processes);

        int contextSwitch = 2;
        int quantum = 3;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please choose an option:\n0. First-Come, First-Served\n1. Shortest Remaining Time\n2. Round Robin\n3. Exit");
        int choice = scanner.nextInt();

        switch (choice) {
            case 0:
                System.out.println("Running First-Come, First-Served algorithm:");
                FCFS(processes, contextSwitch);
                break;
            case 1:
                System.out.println("Running Shortest Remaining Time algorithm:");
                SRT(processes, contextSwitch);
                break;
            case 2:
                System.out.println("Running Round Robin algorithm:");
                RR(processes, quantum, contextSwitch);
                break;
            case 3:
                System.out.println("Exiting...");
                return;
            default:
                System.out.println("Invalid choice!");
                return;
        }

        printResults(processes);
    }

    static void readFile(String filename, ArrayList<Process> processes) {
        try {
            File file = new File(filename);
            try (Scanner scanner = new Scanner(file)) {
                int n = scanner.nextInt();
                for (int i = 0; i < n; i++) {
                    int arrival = scanner.nextInt();
                    int burst = scanner.nextInt();
                    processes.add(new Process(i + 1, arrival, burst, burst));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Failed to open file!");
            System.exit(1);
        }
    }

    static void FCFS(ArrayList<Process> processes, int contextSwitch) {
        int currentTime = 0;
        for (Process p : processes) {
            if (currentTime < p.arrivalTime) {
                currentTime = p.arrivalTime;
            }
            p.waitingTime = currentTime - p.arrivalTime;
            currentTime += p.burstTime + contextSwitch;
            p.endTime = currentTime - contextSwitch;
            p.TAT = p.endTime - p.arrivalTime;
        }
    }

    static void SRT(ArrayList<Process> processes, int contextSwitch) {
        PriorityQueue<Process> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.burstTime));

        int currentTime = 0, idx = 0;
        while (!pq.isEmpty() || idx < processes.size()) {
            while (idx < processes.size() && processes.get(idx).arrivalTime <= currentTime) {
                pq.offer(processes.get(idx++));
            }

            if (!pq.isEmpty()) {
                Process current = pq.poll();
                currentTime += 1;
                current.burstTime--;

                while (idx < processes.size() && processes.get(idx).arrivalTime <= currentTime) {
                    pq.offer(processes.get(idx++));
                }

                if (current.burstTime > 0) {
                    pq.offer(current);
                } else {
                    current.endTime = currentTime;
                    current.TAT = currentTime - current.arrivalTime;
                    current.waitingTime = current.TAT - current.originalBurstTime;
                }

                currentTime += contextSwitch;
            } else {
                if (idx < processes.size()) {
                    currentTime = processes.get(idx).arrivalTime;
                }
            }
        }
    }

    static void RR(ArrayList<Process> processes, int quantum, int contextSwitch) {
        Queue<Process> q = new LinkedList<>();
        int currentTime = 0, idx = 0;
        while (!q.isEmpty() || idx < processes.size()) {
            while (idx < processes.size() && processes.get(idx).arrivalTime <= currentTime) {
                q.offer(processes.get(idx++));
            }

            if (!q.isEmpty()) {
                Process current = q.poll();

                int timeSlice = Math.min(quantum, current.burstTime);
                current.burstTime -= timeSlice;
                currentTime += timeSlice;

                while (idx < processes.size() && processes.get(idx).arrivalTime <= currentTime) {
                    q.offer(processes.get(idx++));
                }

                if (current.burstTime > 0) {
                    q.offer(current);
                } else {
                    current.endTime = currentTime;
                    current.TAT = currentTime - current.arrivalTime;
                    current.waitingTime = current.TAT - current.originalBurstTime;
                }

                currentTime += contextSwitch;
            } else {
                if (idx < processes.size()) {
                    currentTime = processes.get(idx).arrivalTime;
                }
            }
        }
    }

    static void printResults(ArrayList<Process> processes) {
        double totalTAT = 0, totalWaiting = 0;
        System.out.println("Process\tArrival\tBurst\tEnd\tWaiting\tTAT");
        for (Process p : processes) {
            System.out.println(p.id + "\t" + p.arrivalTime + "\t" + p.originalBurstTime + "\t" +
                    p.endTime + "\t" + p.waitingTime + "\t" + p.TAT);
            totalTAT += p.TAT;
            totalWaiting += p.waitingTime;
        }

        System.out.println("Average Waiting Time: " + totalWaiting / processes.size());
        System.out.println("Average Turnaround Time: " + totalTAT / processes.size());
    }
}

