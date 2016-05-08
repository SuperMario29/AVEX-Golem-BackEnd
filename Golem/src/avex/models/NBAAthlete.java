package avex.models;
import java.util.List;

public class NBAAthlete extends Athlete {
	
	private List<NBABoxScoreStats> nbaStats;

	public List<NBABoxScoreStats> getNbaStats() {
		return nbaStats;
	}

	public void setNbaStats(List<NBABoxScoreStats> nbaStats) {
		this.nbaStats = nbaStats;
	}
	
}
