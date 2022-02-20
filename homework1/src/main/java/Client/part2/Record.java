package Client.part2;

public class Record {
    private long start;
    private String requestType;
    private long latency;
    private int statusCode;

    public Record(long start, String requestType, long latency, int statusCode) {
        this.start = start;
        this.requestType = requestType;
        this.latency = latency;
        this.statusCode = statusCode;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "Record{" +
                "start=" + start +
                ", requestType='" + requestType + '\'' +
                ", latency=" + latency +
                ", statusCode=" + statusCode +
                '}';
    }
}
