package skierFunctions;

public class SkierBodyInfo {
    private Integer skierId;
    private Integer liftNum;
    private Integer time;
    private Integer waitTime;

    public SkierBodyInfo(Integer skierId, Integer liftNum, Integer time, Integer waitTime) {
        this.skierId = skierId;
        this.liftNum = liftNum;
        this.time = time;
        this.waitTime = waitTime;
    }

    public Integer getSkierId() {
        return skierId;
    }

    public void setSkierId(Integer skierId) {
        this.skierId = skierId;
    }

    public Integer getLiftNum() {
        return liftNum;
    }

    public void setLiftNum(Integer liftNum) {
        this.liftNum = liftNum;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Integer waitTime) {
        this.waitTime = waitTime;
    }

    @Override
    public String toString() {
        return "SkierReqBody{" +
                "skierId=" + skierId +
                ", liftNum=" + liftNum +
                ", time=" + time +
                ", waitTime=" + waitTime +
                '}';
    }
}
