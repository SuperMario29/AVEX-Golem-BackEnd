package avex.golem;
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

	public void AthleticUpKeep(Date orderDate)
	{
		AVEXDB avexDB = new AVEXDB();
		AthleteDataApi athleteAPI = new AthleteDataApi();
		Map<Integer,BasicDBObject> athleteValueMap = new HashMap<Integer,BasicDBObject>();
		
		List<BasicDBObject> results = avexDB.GetAthletes(orderDate);
		if (results != null)
		{
		for(BasicDBObject athlete:results)
		{
			try
			{
			int athleteID =  (int) athlete.get("athleteid");
			String athleteName = String.valueOf(athlete.get("name"));
			String athletequote = String.valueOf(athlete.get("quote"));
			int teamID = (int)(athlete.get("teamid"));
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
			
			if(athleteIPO == false && recordstatus == 1)
			{
				avexDB.GetAthleteIPO(athleteID, athletequote,availableshares,orderseq);
			}
			
			avexDB.UpdateTeamByAthlete(athleteID, teamID);
			
			BasicDBList athleteStats = athleteAPI.GetNBAStatStatsDataByPlayer(athleteID,true);
			
			if (athleteStats != null && athleteStats.size() > 0)
			{
				BasicDBObject value = GetAthleteValue(athleteStats);
				
				if(!isresellable){
				avexDB.UpdateCurrentPrice(athleteID, value);
				}
				
				athleteValueMap.put(athleteID, value);
			}
			}
			catch(Exception ex){
	            System.out.println(ex.getMessage());
			}
		}
		
		if (athleteValueMap.size() > 0)
		{
			avexDB.SendAthleteValuetoDB(athleteValueMap);
		}
		}
	}
	
	private BasicDBObject GetAthleteValue(BasicDBList athleteStats)
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
               
           long date = new Date().getTime();
   		
   			BasicDBObject athleteValueRating = new BasicDBObject();
   			athleteValueRating.append("athletevalue", Math.floor((value / valueList.size()) * 100) / 100);
   			athleteValueRating.append("currentprice", Math.floor((price / valueList.size()) * 100) / 100);
   			athleteValueRating.append("recordstatusdate", date);           
		return athleteValueRating;
	}
	
	private AthleteValue CalculateValue(NBAAdvancedStats athleteStats){
		AthleteValue value = new AthleteValue();
		double athleteValue = 0.00;
		double price = 0.00;
		athleteValue = Double.valueOf(athleteStats.getPie());
		price = Double.valueOf(athleteStats.getPie());
		value.setAthletevalue(athleteValue);
		value.setPrice(price);
		return value;
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
	
}
