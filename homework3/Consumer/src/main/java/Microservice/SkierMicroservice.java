package Microservice;

import redis.clients.jedis.Jedis;

import java.util.Set;

public class SkierMicroservice {
    static final Jedis jedis = new Jedis("18.234.219.169");

    public static void findDaysSkied(int skiedId) {
        String key = "SkiedId:" + skiedId;
        System.out.println(jedis.scard(key));

    }

    public static void findVerticalTotals(int skiedId) {
        String key = "SkiedId:" + skiedId;
        Set<String> days = jedis.smembers(key);
        for(String d : days) {
            String dayKey = "VerticalTotals " + key + " " + "dayId:" + d;
            // long num = jedis.scard(dayKey);
            System.out.println(dayKey + " " + jedis.llen(dayKey) * 10L);
        }
    }

    public static void showLiftRodeEachDay(int skiedId) {
        String key = "SkiedId:" + skiedId;
        Set<String> days = jedis.smembers(key);
        for(String d : days) {
            String dayKey = "VerticalTotals " + key + " " + "dayId:" + d;
            System.out.println(key + " ride all lift in day " + d + ": " + jedis.lrange(dayKey, 0, -1).toString());
        }
    }

    public static void main(String[] args) {
        jedis.auth("yanghaokairedis1");
        findDaysSkied(9260);
        findVerticalTotals(9260);
        showLiftRodeEachDay(9260);
    }
}
