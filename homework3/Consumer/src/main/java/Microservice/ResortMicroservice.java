package Microservice;

import redis.clients.jedis.Jedis;

public class ResortMicroservice {
    static final Jedis jedis = new Jedis("18.234.219.169");

    public static void getNumberOfUniqueSkierInResortXonDayN(int resortID, int dayID) {
        String key = "resortID:" + resortID + " " + "dayID:" + dayID;
        System.out.println(jedis.scard(key));
    }

    public static void getRidesOnLiftNonDayN(int liftNum, int dayID) {
        String key = "liftNum:" + liftNum + " " + "dayID:" + dayID;
        System.out.println(jedis.llen(key));
    }

    public static void liftInDayN(int dayID) {
        for(int i = 9; i <= 16; i++) {
            String key = "dayID:" + dayID + " " + "time:" + i;
            System.out.println("Day:" + dayID + " Time:" + i + " hour" + " " + "Number:" + jedis.llen(key));
        }
    }

    public static void main(String[] args) {
        jedis.auth("yanghaokairedis1");
        getNumberOfUniqueSkierInResortXonDayN(100, 275);
        getRidesOnLiftNonDayN(18, 230);
        liftInDayN(310);
    }
}
