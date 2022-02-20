package Client.part1;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;


public class SkierThread extends Thread {
    private static int RETRY = 5;
    private static final String PATH = "http://35.86.81.41:8080/javaServlets_war/skiers";
    private int resortID;
    private int seasonID;
    private int dayID;
    private int startSkierId;
    private int endSkierId;
    private int startTime;
    private int endTime;
    private int numPost;
    private int liftNumId;
    private int request = 0;
    private int success = 0;
    private int failure = 0;
    private CountDownLatch latchCur;
    private CountDownLatch latchTotal;
    // create manager to manage the clients
    public static PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    public static  CloseableHttpClient client = HttpClients.custom().setConnectionManager(connectionManager).build();
    static {
        connectionManager.setMaxTotal(150);
        connectionManager.setDefaultMaxPerRoute(35);
    }

    public SkierThread(int resortID, int seasonID, int dayID, int startSkierId,
                       int endSkierId, int startTime, int endTime, int numPost,
                       int liftNumId, int request, int success, int failure, CountDownLatch latchCur,
                       CountDownLatch latchTotal) {
        this.resortID = resortID;
        this.seasonID = seasonID;
        this.dayID = dayID;
        this.startSkierId = startSkierId;
        this.endSkierId = endSkierId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numPost = numPost;
        this.liftNumId = liftNumId;
        this.request = request;
        this.success = success;
        this.failure = failure;
        this.latchCur = latchCur;
        this.latchTotal = latchTotal;
    }

    @Override
    public void run() {
        // for loop to send post
        for(int i = 0; i < this.numPost; i++) {
            int skierId = ThreadLocalRandom.current().nextInt(startSkierId, endSkierId);
            int liftNum = ThreadLocalRandom.current().nextInt(this.liftNumId) + 1;
            int time = ThreadLocalRandom.current().nextInt(startTime, endTime);
            int waitTime = ThreadLocalRandom.current().nextInt(10);

            // configure the post parameters
            String body = createBody(skierId, liftNum, time, waitTime);
            String url = createUrl(resortID, seasonID, dayID, skierId);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(body, "UTF-8"));

            try {
                // get the start time
                long start = System.currentTimeMillis();

                // execute the post
                HttpResponse response = client.execute(httpPost);
                EntityUtils.consume(response.getEntity());
                // calculate the number of request
                request++;
                // calculate the latency
                long latency = System.currentTimeMillis() - start;
                // get the response code
                int status = response.getStatusLine().getStatusCode();
                // check the status code
                if(status / 100 == 2) {
                    success++;
                } else {
                    boolean flag = false;
                    // retry
                    for(int j = 0; j < RETRY; j++) {
                        EntityUtils.consume(response.getEntity());
                        latency = System.currentTimeMillis() - start;
                        status = response.getStatusLine().getStatusCode();
                        request++;
                        if(status / 100 == 2) {
                            flag = true;
                            break;
                        }
                    }

                    if(flag) {
                        success++;
                    } else {
                        failure++;
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }

        // latch count down
        latchCur.countDown();
        latchTotal.countDown();


    }

    private String createBody(int skierId, int liftNum, int time, int waitTime) {
        // set body info
        StringBuilder body = new StringBuilder();
        body.append("{").append("\n");
        body.append("\"skierId\"").append(": " + skierId + ",");
        body.append("\"liftNum\"").append(": " + liftNum + ",");
        body.append("\"time\"").append(": " + time + ",");
        body.append("\"waitTime\"").append(": " + waitTime);
        body.append("\n}");

        return body.toString();
    }

    private String createUrl(int resortID, int seasonID, int dayID, int skierId) {
        // build the url
        StringBuilder sb = new StringBuilder(SkierThread.PATH);
        sb.append("/resortID/").append(resortID);
        sb.append("/seasonID/").append(seasonID);
        sb.append("/dayID/").append(dayID);
        sb.append("/skierID/").append(skierId);
        return sb.toString();
    }


    public int getRequest() {
        return request;
    }

    public int getSuccess() {
        return success;
    }

    public int getFailure() {
        return failure;
    }
}
