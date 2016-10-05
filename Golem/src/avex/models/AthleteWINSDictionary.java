package avex.models;

import java.util.HashMap;

public class AthleteWINSDictionary {

	public HashMap<String, AthleteWINTeamStats> getAthleteWINSDictionary() {
		return AthleteWINSDictionary;
	}

	public void setAthleteWINSDictionary(HashMap<String, AthleteWINTeamStats> athleteWINSDictionary) {
		AthleteWINSDictionary = athleteWINSDictionary;
	}

	private HashMap<String,AthleteWINTeamStats> AthleteWINSDictionary;
	
	
}
