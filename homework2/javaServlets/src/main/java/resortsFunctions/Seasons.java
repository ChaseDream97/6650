package resortsFunctions;

import java.util.ArrayList;
import java.util.List;

public class Seasons {
    List<String> seasons;

    public Seasons() {
        seasons = new ArrayList<>();
    }

    public List<String> getSeasons() {
        return seasons;
    }

    public void addSeasons(String s) {
        this.seasons.add(s);
    }
}
