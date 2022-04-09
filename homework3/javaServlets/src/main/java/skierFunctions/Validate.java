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

    public static boolean skierBodyInfoValid(SkierBodyInfo bodyInfo) {
        if(bodyInfo.getSkierId() != null && bodyInfo.getLiftNum() != null && bodyInfo.getTime() != null && bodyInfo.getWaitTime() != null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean urlInfoCheck(String[] urlPath) {
        if(urlPath.length == 8) {
            return isDigit(urlPath[1]) && urlPath[2].equals("seasons") && isDigit(urlPath[3]) && urlPath[4].equals("days")
                    && isDigit(urlPath[5]) && urlPath[6].equals("skiers") && isDigit(urlPath[7]);
        } else if(urlPath.length == 3){
            if(isDigit(urlPath[1]) && urlPath[2].equals("vertical")) {
                return true;
            }
            return false;
        }
        return false;
    }

    public static boolean isDigit(String s) {
        try {
            Integer.valueOf(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//    private boolean isBodyValid(SkierBodyInfo skierReqBody) {
////        if(skierReqBody.getLiftID() != null &&
////                skierReqBody.getTime() != null &&
////                skierReqBody.getWaitTime() != null){
////            return true;
////        }
////        return false;
//        return true;
//    }
}
