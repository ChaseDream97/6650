package Client.part2;

public class ArgusProcess {

    public static final String NUM_THREADS = "numThreads";
    public static final String NUM_Skiers = "numSkiers";
    public static final String NUM_LIFTS = "numLifts";
    public static final String NUM_RUNS = "numRuns";

    private int numThreads;
    private int numSkiers;
    private int numLifts;
    private int numRuns;

    public ArgusProcess(int numThreads, int numSkiers, int numLifts, int numRuns) {
        this.numThreads = numThreads;
        this.numSkiers = numSkiers;
        this.numLifts = numLifts;
        this.numRuns = numRuns;
    }

    public static ArgusProcess process(String[] args) {
        int threadNum = 64;
        int skierNum = 1024;
        int liftNum = 40;
        int runNum = 10;
        String ip = null;
        String port = null;

        for(int i = 0; i < args.length; i = i + 2) {
            String currArguName = args[i];
            String currArguVal = args[i + 1];


            if(currArguName.equals("NUM_THREADS")) {
                threadNum = Integer.valueOf(currArguVal);
            }

            if(currArguName.equals("NUM_Skiers")) {
                skierNum = Integer.valueOf(currArguVal);
            }

            if(currArguName.equals("NUM_LIFTS")) {
                liftNum = Integer.valueOf(currArguVal);
            }

            if(currArguName.equals("NUM_RUNS")) {
                runNum = Integer.valueOf(currArguVal);
            }

        }

        return new ArgusProcess(threadNum, skierNum, liftNum, runNum);
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public int getNumSkiers() {
        return numSkiers;
    }

    public void setNumSkiers(int numSkiers) {
        this.numSkiers = numSkiers;
    }

    public int getNumLifts() {
        return numLifts;
    }

    public void setNumLifts(int numLifts) {
        this.numLifts = numLifts;
    }

    public int getNumRuns() {
        return numRuns;
    }

    public void setNumRuns(int numRuns) {
        this.numRuns = numRuns;
    }

}
