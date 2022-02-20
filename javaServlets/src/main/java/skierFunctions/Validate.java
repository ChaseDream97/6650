package skierFunctions;

public class Validate {

    public static boolean skierInfoSeasonDay(String info) {
        String[] urlPath = info.split("/");
        if(urlPath.length != 7) {
            return false;
        }

        if(urlPath[1].equals("seasons") && urlPath[3].equals("days")
        && urlPath[5].equals("skiers")) {
            return true;
        }

        return false;
    }
}
