package avex.golem;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import avex.athletedata.AthleteDataApi;
import avex.models.*;

public class AthleteMaintenance {
	
	HashMap<String,HashMap<String,AthleteWINS>> athleteDictionary = new HashMap<String,HashMap<String,AthleteWINS>>();
	HashMap<Integer,String> athleteImages;

	public void AthleticUpKeep()
	{
		AVEXDB avexDB = new AVEXDB();
		AthleteDataApi athleteAPI = new AthleteDataApi();
		Map<Integer,BasicDBObject> athleteValueMap = new HashMap<Integer,BasicDBObject>();
		
		athleteDictionary = GetWinsFile();
		athleteImages = GetAthleteImages();
		
		List<BasicDBObject> results = avexDB.GetAthletes();		
		if (results != null)
		{
		for(BasicDBObject athlete:results)
		{
			try
			{
			int athleteID =  (int) athlete.get("athleteid");
			String ID = athlete.getString("_id");
			String athleteName = String.valueOf(athlete.get("name"));
			BasicDBObject athleteTeam = (BasicDBObject) athlete.get("team");
			String athleteTeamAbb = String.valueOf(athleteTeam.get("abbreviation"));
			String athletequote = String.valueOf(athlete.get("quote"));
			int teamID = (int)(athleteTeam.get("teamid"));
			int orderseq = (int) athlete.get("orderseq");
			boolean athleteIPO = false;
			boolean isresellable = false;
			if (athlete.get("isresellable") != null){
				if (athlete.getBoolean("isresellable")){
				isresellable = true;}
			}
			if (athlete.get("availableshares") != null){
				athleteIPO = true;
			}
			int recordstatus = (int) athlete.get("recordstatus");
			int availableshares = (int) athlete.get("totalshares");
			
			if(athletequote == null || athletequote == "null")
			{
				avexDB.GetAthleteQuote(athleteID, athleteName);
			}
			
			if(athleteImages != null && athleteImages.containsKey(athleteID)){
				avexDB.UpdateAthleteImage(ID,new BasicDBObject().append("$set", new BasicDBObject().append("imageurl", athleteImages.get(athleteID))));
			}
			
			if(athleteIPO == false && recordstatus == 1)
			{
				avexDB.GetAthleteIPO(athleteID, athletequote,availableshares,orderseq);
			}
			
			avexDB.UpdateTeamByAthlete(athleteID, teamID);
			
			//BasicDBList athleteStats = athleteAPI.GetNBAStatStatsDataByPlayer(athleteID,true);
			
			//if (athleteStats != null && athleteStats.size() > 0)
			//{
				//BasicDBObject value = GetAthleteValue(athleteStats);
				BasicDBObject value = GetWinsValue(athleteName,athleteTeamAbb);			
			
				if(!isresellable && !value.isEmpty()){
						avexDB.UpdateCurrentPrice(athleteID, value);
				}
				
				if(!value.isEmpty()){
				athleteValueMap.put(athleteID, value);
				}
				else{
					avexDB.AthleteUnavailable(athleteID);
				}
			//}
			}
			catch(Exception ex){
				System.out.println("Exception: " + ex.getMessage());
				System.out.println("Stack Trace: " + ex.getStackTrace());
			}
		}
		
		if (athleteValueMap.size() > 0)
		{
			avexDB.SendAthleteValuetoDB(athleteValueMap);
		}
		}
	}
	
	private BasicDBObject GetAthleteValue(BasicDBList athleteStats)
	{	try
	{
		List<AthleteValue> valueList = new ArrayList<AthleteValue>();
		double value = 0.00;
		double price = 0.00;

        for (int i=0; i < athleteStats.size(); i++) {
           BasicDBObject athlete = (BasicDBObject) athleteStats.get(i); 
           NBAAdvancedStats nbaStats = BuildNBAStatsObject(athlete);
           AthleteValue athleteValue = CalculateValue(nbaStats);
           valueList.add(athleteValue);
        }
        
        for(AthleteValue v : valueList){
        	value = value + v.getAthletevalue();
        	price = price + v.getPrice();
        }
        
        value = Math.floor((value / valueList.size()) * 100) / 100;
        price =  Math.floor((price / valueList.size()) * 100) / 100;
                
        if (value < 0){
        	value *= -1;
        	price *= -1;
        	if (value >= 1){
        		int getValue = (int) value;
        		if (getValue < 10){
        			value = Math.floor((value * .01) * 100) / 100;
        			price = Math.floor((price * .01) * 100) / 100;
        		}
        		else{
        			value = Math.floor((value * .001) * 100) / 100;
        			price = Math.floor((price * .001) * 100) / 100;
        		}
        	}
        }
        else if (value == 0){
        	value = 1.00;
        	price = 1.00;
        }
        else{
        	value = Math.floor((value * 10000) * 100) / 100;
        	price = Math.floor((price * 10000) * 100)/ 100;
        }       
               
           Date date = new Date();
   		
   			BasicDBObject athleteValueRating = new BasicDBObject();
   			athleteValueRating.append("athletevalue", Math.floor((value / valueList.size()) * 100) / 100);
   			athleteValueRating.append("currentprice", Math.floor((price / valueList.size()) * 100) / 100);
   			athleteValueRating.append("recordstatusdate", date);   
   			return athleteValueRating;
	}
	catch(Exception ex){
		System.out.println("Exception: " + ex.getMessage());
		System.out.println("Stack Trace: " + ex.getStackTrace());
		return null;
	}
	}

	private BasicDBObject GetWinsValue(String athleteName, String teamNameAbb){
		BasicDBObject athleteValueRating = new BasicDBObject();
			if(athleteDictionary.containsKey(athleteName))
			{
				if(athleteDictionary.get(athleteName).containsKey(teamNameAbb))
				{
					AthleteWINS wins = athleteDictionary.get(athleteName).get(teamNameAbb);
					athleteValueRating.append("athletevalue", wins.getWins());
					athleteValueRating.append("currentprice", wins.getWins());
					athleteValueRating.append("recordstatusdate", new Date());   
				}
			}
		return athleteValueRating;
	}
	
	private AthleteValue CalculateValue(NBAAdvancedStats athleteStats){
		try{
			AthleteValue value = new AthleteValue();
			double athleteValue = 0.00;
			double price = 0.00;
			athleteValue = Double.valueOf(athleteStats.getPie());
			price = Double.valueOf(athleteStats.getPie());
			value.setAthletevalue(athleteValue);
			value.setPrice(price);
			return value;
		}
		catch (Exception ex){
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
			return null;
		}
	}
		
	private NBAAdvancedStats BuildNBAStatsObject(BasicDBObject as)
	{
		NBAAdvancedStats nbaStats = new NBAAdvancedStats();
	
		//nbaStats.setBox_minutes((int) as.get("box_minutes"));
		//nbaStats.setBox_pts((int) as.get("box_pts"));
		//nbaStats.setAdv_pace((String) as.get("adv_pace"));
		nbaStats.setPie((String) as.get("pie"));
		//nbaStats.setPie((double) as.get("pie"));
		//nbaStats.setAdv_off_rating((String) as.get("adv_off_rating"));
		//nbaStats.setAdv_def_rating((String) as.get("adv_def_rating"));	
		
		return nbaStats;
		
	}
	
	private HashMap<String,HashMap<String,AthleteWINS>> GetWinsFile(){
		HashMap<String,HashMap<String,AthleteWINS>> results = new HashMap<String,HashMap<String,AthleteWINS>>();
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
		
        try {
			br = new BufferedReader(new FileReader(Program.WINS_FILE_PATH));
	        while ((line = br.readLine()) != null) {
	            // use comma as separator
	            String[] athleteInfo = line.split(cvsSplitBy);
	            String athleteName = athleteInfo[1];
	            if(athleteName != null) {athleteName = athleteName.trim();}
	            String teamName = athleteInfo[4];
	            if(teamName != null) {teamName = teamName.trim();}
	            String WINS = athleteInfo[22];
	            if(WINS != null) {WINS = WINS.trim();}
	            String wins48 = athleteInfo[23];
	            if(wins48 != null) {wins48 = wins48.trim();}
            	AthleteWINS w = new AthleteWINS();
            	w.setWins(WINS);
            	w.setWins48(wins48);
	            
	            if (results.containsKey(athleteName))
	            {
	            	if(!results.get(athleteName).containsKey(teamName)){
	            		results.get(athleteName).put(teamName, w);
	            	}
	            }
	            else{
	            	HashMap<String,AthleteWINS> ats = new HashMap<String,AthleteWINS>();
	            	ats.put(teamName, w);
	            	results.put(athleteName, ats);
	            }
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new HashMap<String,HashMap<String,AthleteWINS>>();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new HashMap<String,HashMap<String,AthleteWINS>>();
		}
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
	}
	
	private AthleteWINSDictionary GetWinsFileOLD(){
		AthleteWINSDictionary results = new AthleteWINSDictionary();
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
		
        try {
			br = new BufferedReader(new FileReader(Program.WINS_FILE_PATH));
	        while ((line = br.readLine()) != null) {
	            // use comma as separator
	            String[] athleteInfo = line.split(cvsSplitBy);
	            String athleteName = athleteInfo[1];
	            if(athleteName != null) {athleteName = athleteName.trim();}
	            String teamName = athleteInfo[4];
	            if(teamName != null) {teamName = teamName.trim();}
	            String WINS = athleteInfo[22];
	            if(WINS != null) {WINS = WINS.trim();}
	            String wins48 = athleteInfo[23];
	            if(wins48 != null) {wins48 = wins48.trim();}
            	AthleteWINS w = new AthleteWINS();
            	w.setWins(WINS);
            	w.setWins48(wins48);
	            
	            if (results.getAthleteWINSDictionary().containsKey(athleteName))
	            {
	            	if(!results.getAthleteWINSDictionary().get(athleteName).AthleteWINSTeamStatsDictionary().containsKey(teamName)){
	            		results.getAthleteWINSDictionary().get(athleteName).AthleteWINSTeamStatsDictionary().put(teamName, w);
	            	}
	            }
	            else{
	            	AthleteWINTeamStats ats = new AthleteWINTeamStats();
	            	ats.AthleteWINSTeamStatsDictionary().put(teamName, w);
	            	results.getAthleteWINSDictionary().put(athleteName, ats);
	            }
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new AthleteWINSDictionary();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new AthleteWINSDictionary();
		}
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
	}

	private HashMap<Integer,String> GetAthleteImages(){
	
		HashMap<Integer,String> results = new HashMap<Integer,String>();
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
		
        try {
			br = new BufferedReader(new FileReader(Program.IMAGE_FILE_PATH));
	        while ((line = br.readLine()) != null) {
	            // use comma as separator
	            String[] athleteInfo = line.split(cvsSplitBy);
	            String athleteid = athleteInfo[0];
	            if(athleteid != null && tryParseInt(athleteid)) {athleteid = athleteid.trim();}
	            else{continue;}
	            String athleteName = athleteInfo[1];
	            String imageurl = athleteInfo[2];
	            if(imageurl != null) {imageurl = imageurl.trim();}
	            
	            if (!results.containsKey(Integer.parseInt(athleteid)))
	            {
	            	results.put(Integer.parseInt(athleteid), imageurl);
	            }
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new HashMap<Integer,String>();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new HashMap<Integer,String>();
		}
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		
		return results;		
	}
	
	private boolean tryParseInt(String value) {  
	     try {  
	         Integer.parseInt(value);  
	         return true;  
	      } catch (NumberFormatException e) {  
	         return false;  
	      }  
	}
}
