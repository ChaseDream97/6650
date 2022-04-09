package Consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Consumer {
    private final static String QUEUE_NAME = "SkierServletQueue";
    private final static Integer NUMBER_OF_QUEUE = 32;

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        ConcurrentHashMap<Integer, List<JsonObject>> map = new ConcurrentHashMap<>();
        Gson gson = new Gson();

        factory.setHost("44.201.141.57");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);
        Connection connection = factory.newConnection();

        System.out.println("Consumer start successful!");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Channel channel = connection.createChannel();
                    channel.queueDeclare(QUEUE_NAME, false, false, false, null);

                    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                        JsonObject json = gson.fromJson(message, JsonObject.class);
                        int skierId = Integer.valueOf(String.valueOf(json.get("skierId")));
                        map.putIfAbsent(skierId, new ArrayList<>());
                        map.get(skierId).add(json);
                        System.out.println(skierId + "   " + json.toString());
                    };

                    channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
                } catch (IOException e) {
                    System.out.println(e.toString());
                    e.printStackTrace();
                }
            }
        };

        for(int i = 0; i < NUMBER_OF_QUEUE; i++) {
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
}
