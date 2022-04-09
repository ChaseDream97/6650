package resortsFunctions;

public class Validate {

    public static boolean resortInfoSeasonDay(String info) {
        String[] urlPath = info.split("/");
        if(urlPath.length != 6 && urlPath.length != 2) {
            return false;
        }

        if(urlPath.length == 6 && urlPath[1].equals("seasons") && urlPath[3].equals("day")
        && urlPath[5].equals("skiers")) {
            return true;
        } else if(urlPath.length == 2 && urlPath[1].equals("seasons")) {
            return true;
        }

        return false;
    }

    public static boolean resortBodyInfoValid(ResortsBodyInfo resortsBodyInfo) {
        if (resortsBodyInfo.getYear() != null) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean resortInfoSeasonDayPost(String info) {
        String[] urlPath = info.split("/");
        if(urlPath.length != 2) {
            return false;
        }

        return urlPath[1].equals("seasons");
    }




}
