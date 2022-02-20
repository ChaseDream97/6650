package Client.part1;

public class LatencyTest {
    public static void main(String[] args) throws InterruptedException {
//        long startTime = System.currentTimeMillis();
//        BlockingQueue<SkierThread> phases = new ArrayBlockingQueue<>(10000);
//        BlockingQueue<Record> records = new ArrayBlockingQueue<Record>(10000);
//        CountDownLatch latchTotal = new CountDownLatch(1);
//        CountDownLatch latch = new CountDownLatch(0);
//        SkierThread thread = new SkierThread(100, 1, 19, 1, 100,
//                10, 10000, 10000, 10
//                , 0,0
//                , 0, latch, latchTotal, records);
//        thread.start();
//        latchTotal.await();
//        long endTime = System.currentTimeMillis();
//        System.out.println("latency: " + (endTime - startTime) / 10000.0);

        System.out.println("phase1 predicted throughput=" + (16) / (62.86 / 1000));
        System.out.println("phase2 predicted throughput=" + (64) / (62.86 / 1000));
        System.out.println("phase3 predicted throughput=" + (6) / (62.86 / 1000));
    }
}
