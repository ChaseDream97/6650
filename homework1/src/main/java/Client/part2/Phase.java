package Client.part2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Phase {
    private int numThreads;
    private int numSkiers;
    private Integer numLifts;
    private int startTime;
    private int endTime;
    private int numPostRequests;
    private CountDownLatch latch;
    private CountDownLatch latchTotal;
    private BlockingQueue<Record> records;
    private BlockingQueue<SkierThread> threadsInfo;

    public Phase(int numThreads, int numSkiers, Integer numLifts, int startTime, int endTime,
                 int numPostRequests, CountDownLatch latch, CountDownLatch latchTotal, BlockingQueue<Record> records, BlockingQueue<SkierThread> threadsInfo) {
        this.numThreads = numThreads;
        this.numSkiers = numSkiers;
        this.numLifts = numLifts;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numPostRequests = numPostRequests;
        this.latch = latch;
        this.latchTotal = latchTotal;
        this.records = records;
        this.threadsInfo = threadsInfo;
    }

    public void run() {
        for (int i = 0; i < this.numThreads; i++) {
            int skiersStartId = (i * (this.numSkiers / this.numThreads)) + 1;
            int skiersEndId = (i + 1) * (this.numSkiers / this.numThreads);

            SkierThread thread = new SkierThread(100, 1, 19, skiersStartId, skiersEndId, startTime, endTime, numPostRequests, numLifts, 0,0
            , 0, latch, latchTotal, records);
            thread.start();
            threadsInfo.add(thread);
        }
    }
}