package Client.part2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Client {

    private static final int DEFAULT_POST_MAX_CAPACITY = 100000;
    private static final int DEFAULT_THREAD_MAX_CAPACITY = 1024;
    private static final double INTERNET_LATENCY = 62.86;
    private static int numRequest = 0;
    private static int numSuccess = 0;
    private static int numFailure = 0;

    public static void main(String[] args) throws Exception{

        // receive arguments from main or default
        ArgusProcess input = ArgusProcess.process(args);
        int numThreads = input.getNumThreads();
        int numSkiers = input.getNumSkiers();
        int numLift = input.getNumLifts();
        int numRuns = input.getNumRuns();

        // output input argument
        inputArgumentInformationOutput((input));

        long startTime = System.currentTimeMillis();

        BlockingQueue<SkierThread> threadsInfo = new ArrayBlockingQueue<>(DEFAULT_THREAD_MAX_CAPACITY);
        // the records are the information that we need output it into csv
        BlockingQueue<Record> records = new ArrayBlockingQueue<Record>(DEFAULT_POST_MAX_CAPACITY);

        // total latch calculate
        int phase1Threads = (int)Math.round(numThreads / 4.0);
        int phase2Threads = numThreads;
        int phase3Threads = (int) Math.round(numThreads * 0.1);
        CountDownLatch latchTotal = new CountDownLatch(phase1Threads + phase2Threads + phase3Threads);

        // phase 1
        int p1RequestsNum = (int) Math.round((numRuns * 0.2) * (numSkiers /  (phase1Threads * 1.0)));
        CountDownLatch latch1 = new CountDownLatch((int)Math.ceil(phase1Threads * 0.2));
        Phase phase1 = new Phase(phase1Threads, numSkiers, numLift, 1, 90, p1RequestsNum, latchTotal, latch1, records, threadsInfo);
        //System.out.println("phase1 start");
        phase1.run();
        latch1.await();

        // phase 2
        int p2RequestsNum = (int) Math.round((numRuns * 0.6) * (numSkiers / (numThreads * 1.0)));
        CountDownLatch latch2 = new CountDownLatch((int)Math.ceil(numThreads * 0.2));
        Phase phase2 = new Phase(phase2Threads, numSkiers, numLift, 91, 360, p2RequestsNum, latch2, latchTotal, records, threadsInfo);
        //System.out.println("phase2 start");
        phase2.run();
        latch2.await();

        // phase 3
        CountDownLatch latch3 = new CountDownLatch(0);
        int p3RequestsNum = (int) Math.round(numRuns * 0.1);
        Phase phase3 = new Phase(phase3Threads, numSkiers, numLift, 361, 420, p3RequestsNum, latch3, latchTotal, records, threadsInfo);
        //System.out.println("phase3 start");
        phase3.run();

        // wait for all phase to finish
        latchTotal.await();

        // count the number of success and failure during process
        count(threadsInfo);

        //calculate wall time
        long endTime = System.currentTimeMillis();
        long wallTime = endTime - startTime;

        // calculate the predicted throughput
        calculatePredictedThroughput(phase1Threads, phase2Threads, phase3Threads);

        // part1
        // output the information required
        calculatePart1Info(wallTime);

        // part2
        // out put the post info
        writeRecordsIntoCsv(records);
        calculatePart2Info(records, wallTime);
    }

    private static void inputArgumentInformationOutput(ArgusProcess input) {
        System.out.println("------------------------------------------");
        System.out.println("Input Argument Information Output!");
        System.out.println("numThreads:" + input.getNumThreads());
        System.out.println("numSkiers:" + input.getNumSkiers());
        System.out.println("numLift:" + input.getNumLifts());
        System.out.println("numRuns:" + input.getNumRuns());
    }

    private static void calculatePredictedThroughput(int p1Threads, int p2Threads, int p3Threads) {
        System.out.println("\n------------------------------------------");
        System.out.println("Predict Throughput Output!");
        System.out.println("phase1 predicted throughput=" + (int)((p1Threads) / (INTERNET_LATENCY / 1000)));
        System.out.println("phase2 predicted throughput=" + (int)((p2Threads) / (INTERNET_LATENCY / 1000)));
        System.out.println("phase3 predicted throughput=" + (int)((p3Threads) / (INTERNET_LATENCY / 1000)));
    }

    private static void calculatePart1Info(long wallTime) {
        System.out.println("\n------------------------------------------");
        System.out.println("Client Part1 Required Information Output!");
        System.out.println("number of successful requests: " + numSuccess);
        System.out.println("number of unsuccessful requests: " + numFailure);
        System.out.println("the total run time (wall time): " + wallTime + "ms");
        System.out.println("the total throughput in requests per second:  " + 1000L * numRequest / wallTime);
    }

    private static void calculatePart2Info(BlockingQueue<Record> records, long wallTime) {
        long totalResponseTime = 0;
        List<Long> list = new ArrayList<>();

        for(Record r : records) {
            totalResponseTime += r.getLatency();
            list.add(r.getLatency());
        }

        Collections.sort(list);
        long median = 0;
        if(list.size() % 2 == 0) {
            median = (list.get(list.size() / 2) + list.get(list.size() / 2 - 1)) / 2;
        } else {
            median = list.get(list.size() / 2);
        }

        System.out.println("\n------------------------------------------");
        System.out.println("Client Part2 Required Information Output!");
        System.out.println("mean response time: " + totalResponseTime / records.size() + "ms");
        System.out.println("median response time: " + median + "ms");
        System.out.println("the total throughput in requests per second: " + 1000L * numRequest / wallTime);
        System.out.println("p99 (99th percentile) response time: " + list.get((int)(list.size() * 0.99)) + "ms");
        System.out.println("max response time: " + list.get(list.size() - 1) + "ms");
        System.out.println("min response time: " + list.get(0) + "ms");

    }

    private static void writeRecordsIntoCsv(BlockingQueue<Record> records) {
        File csv = new File("src/part2Records.csv");
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csv, true));
            bufferedWriter.write("start time" + "," + "request type" + "," + "latency" + "," + "response code");
            bufferedWriter.newLine();
            for(Record record : records) {
                String r = record.getStart() + "," + record.getRequestType() + "," + record.getLatency() + "," + record.getStatusCode();
                bufferedWriter.write(r);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void count(BlockingQueue<SkierThread> threadsInfo) {
        for(SkierThread thread : threadsInfo) {
            numFailure += thread.getFailure();
            numRequest += thread.getRequest();
            numSuccess += thread.getSuccess();
        }
    }
}
