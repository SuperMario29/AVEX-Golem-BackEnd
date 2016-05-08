package avex.athletedata;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.mongodb.BasicDBList;
import com.mongodb.util.JSON;

public class AthleteDataApi {

	private final String USER_AGENT = "Mozilla/5.0";
	
public BasicDBList GetNBAStatStatsDataByPlayer(int playerid,Boolean isAdvancedStats,String season){
		
		BasicDBList results = new BasicDBList();
		
		String api_key = "o0Q26qMPmn3az74dyGLhFItWCR5ZicHj";
		String query = "api_key=" + api_key+"&player_id="+playerid;
		if (season != null){
			query += "&season" + season; 
		}
		String url = "http://api.probasketballapi.com/boxscore/player";
		
		try
		{
			if(isAdvancedStats){
				url = "http://api.probasketballapi.com/advanced/player";
			}
			
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(query);
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
			results = (BasicDBList) JSON.parse(inputLine);
		}
		in.close();		
		return results;
		}
		catch(Exception ex)
		{
			return null;
		}
	}

public BasicDBList GetNBAStatStatsDataByPlayer(int playerid,Boolean isAdvancedStats){

	BasicDBList results = new BasicDBList();
		results =	GetNBAStatStatsDataByPlayer(playerid,isAdvancedStats,null);
	
	return results;
}
	
	
	public BasicDBList GetAllNBAAthletes(){
		
		BasicDBList results = new BasicDBList();
		
		String api_key = "o0Q26qMPmn3az74dyGLhFItWCR5ZicHj";
		String query = "api_key=" + api_key;
		String url = "http://api.probasketballapi.com/player";
		
		try
		{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(query);
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			results = (BasicDBList) JSON.parse(inputLine);
		}
		in.close();		
		return results;
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
	public BasicDBList GetNBATeams()
	{
		BasicDBList results = new BasicDBList();
		
		String api_key = "o0Q26qMPmn3az74dyGLhFItWCR5ZicHj";
		String query = "api_key=" + api_key;
		String url = "http://api.probasketballapi.com/team";
		
		try
		{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(query);
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
			results = (BasicDBList) JSON.parse(inputLine);
		}
		in.close();		
		return results;
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
	public BasicDBList GetGameInfo(String gameid)
	{
		BasicDBList results = new BasicDBList();
		
		String api_key = "o0Q26qMPmn3az74dyGLhFItWCR5ZicHj";
		String query = "api_key=" + api_key+"&game_id="+gameid;
		String url = "http://api.probasketballapi.com/team";
		
		try
		{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(query);
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
			results = (BasicDBList) JSON.parse(inputLine);
		}
		in.close();		
		return results;
		}
		catch(Exception ex)
		{
			return null;
		}
	}
}
