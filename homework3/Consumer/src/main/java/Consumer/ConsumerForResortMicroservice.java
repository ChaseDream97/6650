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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerForResortMicroservice {
    private final static String QUEUE_NAME = "ResortQueue";
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

        System.out.println("Resort Microservice Consumer start successful!");

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(512);

        JedisPool pool = new JedisPool(poolConfig, "54.163.163.14", 6379, 2000, "yanghaokairedis1");

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

                        try (Jedis jedis = pool.getResource()) {
                            String resortID = String.valueOf(json.get("resortID"));
                            String seasonID = String.valueOf(json.get("seasonID"));
                            String dayID = String.valueOf(json.get("dayID"));
                            String skierId = String.valueOf(json.get("skierId"));
                            String time = String.valueOf(json.get("time"));
                            String liftNum = String.valueOf(json.get("liftNum"));
                            String waitTime = String.valueOf(json.get("waitTime"));

                            // service1, “How many unique skiers visited resort X on day N?”
                            String service1Key = "resortID:" + resortID + " " + "dayID:" + dayID;
                            jedis.sadd(service1Key, skierId);

                            // service2, “How many rides on lift N happened on day N?”
                            String service2Key = "liftNum:" + liftNum + " " + "dayID:" + dayID;
                            jedis.rpush(service2Key, skierId);

                            // service3, “On day N, show me how many lift rides took place in each hour of the ski day”
                            int hour = Integer.valueOf(time) / 60 + 9;
                            String service3Key = "dayID:" + dayID + " " + "time:" + hour;
                            jedis.rpush(service3Key, liftNum);



                        } catch (Exception e) {
                            System.out.println(e.toString());
                        }

                    };

                    channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
                } catch (IOException e) {
                    System.out.println(e.toString());
                    e.printStackTrace();
                }
            }
        };

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
