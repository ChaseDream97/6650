package Consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.util.Pool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsumerForSkierMicroservice {
//    private final static String QUEUE_NAME = "SkierServletQueue";
    private final static String QUEUE_NAME = "SkierQueue";
    private final static Integer NUMBER_OF_QUEUE = 512;

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        ConcurrentHashMap<Integer, List<JsonObject>> map = new ConcurrentHashMap<>();
        Gson gson = new Gson();

        factory.setHost("44.201.141.57");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);
        Connection connection = factory.newConnection();

        System.out.println("Skier Microservice Consumer start successful!");


        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(512);

        JedisPool pool = new JedisPool(poolConfig, "18.234.174.241", 6379, 2000, "yanghaokairedis1");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {

                final Channel channel = connection.createChannel();
                //channel.queueDeclare(QUEUE_NAME, false, false, false, null);

                channel.basicQos(1);

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    JsonObject json = gson.fromJson(message, JsonObject.class);

                    try(Jedis jedis = pool.getResource()) {
                        // service1, how many days skied this season
                        String key = "SkiedId:" + String.valueOf(json.get("skierId"));
                        String dayId = String.valueOf(json.get("dayID"));
                        String liftId = String.valueOf(json.get("liftNum"));
                        //System.out.println(key + " " + String.valueOf(json.get("dayID")));
                        jedis.sadd(key, dayId);

                        // service2
                        String service2Key = "VerticalTotals " + key + " " + "dayId:" + dayId;
                        jedis.rpush(service2Key, liftId);

                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }

                };

                //channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
                channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
                } catch (IOException e) {
                    System.out.println(e.toString());
                    e.printStackTrace();
                }
            }
        };

//        for(int i = 0; i < NUMBER_OF_QUEUE; i++) {
//            Thread thread = new Thread(runnable);
//            thread.start();
//        }

        List<Thread> ThreadList = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_QUEUE; i++){
            Thread thread = new Thread(runnable);
            thread.start();
            ThreadList.add(thread);
        }
        for (int i = 0; i < NUMBER_OF_QUEUE; i++){
            ThreadList.get(i).join();
        }

    }
}

