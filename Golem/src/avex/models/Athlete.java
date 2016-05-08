package avex.models;
import java.util.List;

public class Athlete {

	private String name,position,sport,imageUrl;
	private int numberOfShares;
	private long player_id;
	private List<WinRating> winRatings;
	private List<Team> teamList;
	private List<Price> priceList;
		
	public long getPlayer_id() {
		return player_id;
	}
	public void setPlayer_id(long player_id) {
		this.player_id = player_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSport() {
		return sport;
	}
	public void setSport(String sport) {
		this.sport = sport;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public List<WinRating> getWinRatings() {
		return winRatings;
	}
	public void setWinRatings(List<WinRating> winRatings) {
		this.winRatings = winRatings;
	}
	public List<Team> getTeamList() {
		return teamList;
	}
	public void setTeamList(List<Team> teamList) {
		this.teamList = teamList;
	}
	public int getNumberOfShares() {
		return numberOfShares;
	}
	public void setNumberOfShares(int numberOfShares) {
		this.numberOfShares = numberOfShares;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public List<Price> getPriceList() {
		return priceList;
	}
	public void setPriceList(List<Price> priceList) {
		this.priceList = priceList;
	}
		
}
