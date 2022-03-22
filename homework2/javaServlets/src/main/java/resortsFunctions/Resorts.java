package resortsFunctions;

public class Resorts {
    private String resortName;
    private int resortID;

    public Resorts() {
    }

    public Resorts(String resortName, int resortID) {
        this.resortName = resortName;
        this.resortID = resortID;
    }

    public String getResortName() {
        return resortName;
    }

    public void setResortName(String resortName) {
        this.resortName = resortName;
    }

    public int getResortID() {
        return resortID;
    }

    public void setResortID(int resortID) {
        this.resortID = resortID;
    }
}
