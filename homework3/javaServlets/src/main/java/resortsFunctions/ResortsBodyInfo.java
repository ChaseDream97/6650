package resortsFunctions;

public class ResortsBodyInfo {
    private String year;

    public ResortsBodyInfo(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "ResortsBodyInfo{" +
                "year='" + year + '\'' +
                '}';
    }
}
