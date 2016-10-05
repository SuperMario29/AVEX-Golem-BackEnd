package avex.models;

import java.util.HashMap;

public class AthleteWINTeamStats {

	private HashMap<String,AthleteWINS> AthleteWINTeamStatsDictionary;
	
	public HashMap<String,AthleteWINS> AthleteWINSTeamStatsDictionary() {
		if(AthleteWINTeamStatsDictionary == null){AthleteWINTeamStatsDictionary = new HashMap<String,AthleteWINS>();}
		return AthleteWINTeamStatsDictionary;
	}

	public void setAthleteWINSTeamStatsDictionary(HashMap<String,AthleteWINS> AthleteWINTeamStatsDictionary) {
		this.AthleteWINTeamStatsDictionary = AthleteWINTeamStatsDictionary;
	}
	
}
