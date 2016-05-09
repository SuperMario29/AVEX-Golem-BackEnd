package avex.golem;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mongodb.MongoClient;

import avex.models.Customer;
import avex.models.Order;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import org.json.simple.parser.JSONParser;



public class AVEXDB {

	@SuppressWarnings("deprecation")
	public List<BasicDBObject> GetAthletes(Date orderDate)
	{
        int i = 1;
		try
		{
		List<BasicDBObject> athleteList = new ArrayList<>();
		
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , 27017 );
    	
        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollection = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
			
        DBCursor cursor = athleteCollection.find();
			
        while (cursor.hasNext()) { 
           BasicDBObject athlete = (BasicDBObject) cursor.next();
           athleteList.add(athlete); 
           i++;
        	}
        System.out.println("Received "+ i + " athletes"); 

        mongoClient.close();

		return athleteList;
		}
		catch(Exception ex)
		{
            System.out.println("Exception: " + ex.toString()); 
			return null;
		}
	}
	
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public void SendAthleteValuetoDB(Map<Integer,BasicDBObject> athletes)
	{
		try
		{
        // To connect to mongodb server
	        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , 27017 );
	    	
	        // Now connect to your databases
			DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollect = db.getCollection("athletes");
        System.out.println("Collection mycol selected successfully");
		
        Iterator it = athletes.entrySet().iterator();
        while (it.hasNext()) {
        	BasicDBObject query = new BasicDBObject();
        	
            Map.Entry pair = (Map.Entry)it.next();
        	query.append("athleteid", pair.getKey());

            BasicDBObject setNewValue = new BasicDBObject().append("$push", new BasicDBObject().append("athletevalues", pair.getValue()));

            athleteCollect.update(query, setNewValue);
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
			
        mongoClient.close();
		
		}
		catch(Exception ex)
		{
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void GetAthleteQuote(int athleteID,String playerName)
	{
		try
		{
        // To connect to mongodb server
	        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , 27017 );
	    	
	        // Now connect to your databases
			DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollect = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
		

    	BasicDBObject queryFindAthlete = new BasicDBObject();
    	queryFindAthlete.append("athleteid", athleteID);
        
    	boolean findQuote = true;
    	String name = playerName.replaceAll("[-+.^:,'\\s+]","");

    	while (findQuote)
    	{    		
        	BasicDBObject queryQuote = new BasicDBObject();
        	String quote = Shuffle(name).substring(0, 4).toUpperCase();
        	queryQuote.append("quote", quote);
        	
    	if (athleteCollect.find(queryQuote).count() <= 0)
    	{
            BasicDBObject setNewQuote = new BasicDBObject().append("$set", new BasicDBObject().append("quote", quote));

            athleteCollect.update(queryFindAthlete, setNewQuote);
            BasicDBObject findInfo = (BasicDBObject) athleteCollect.findOne(queryFindAthlete);
            System.out.println(findInfo.toString());
            break;
    	}
    	}       
			
        mongoClient.close();
		
		}
		catch(Exception ex)
		{
		}
	}
	
	@SuppressWarnings("deprecation")
	public void UpdateCurrentPrice(int athleteID, DBObject value){
		try{
        // To connect to mongodb server
	        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , 27017 );
	    	
	        // Now connect to your databases
			DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollect = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
        
        DBCollection collect = db.getCollection("athletes");
        System.out.println("Collection teams selected successfully");
		
    	BasicDBObject queryFindAthlete = new BasicDBObject();
    	queryFindAthlete.append("athleteid", athleteID);
        
    	BasicDBObject athleteObject = (BasicDBObject) collect.findOne(queryFindAthlete);
    	
    	if (athleteObject != null)
    	{
			double currentPrice = (double)(value.get("currentprice"));

            BasicDBObject setPrice = new BasicDBObject().append("$set", new BasicDBObject().append("currentprice", currentPrice));
            BasicDBObject priceHistory = new BasicDBObject().append("price",currentPrice);
            priceHistory.append("isathletevalueprice", true);
            priceHistory.append("recordstatusdate", new Date());
            priceHistory.append("recordstatus", 1);
            setPrice.append("$push", new BasicDBObject().append("pricehistory", priceHistory));
            athleteCollect.update(queryFindAthlete, setPrice);
    	}   
			
        mongoClient.close();
		
		}
		catch(Exception ex)
		{
		}
	}
	
	@SuppressWarnings("deprecation")
	public void UpdateTeamByAthlete(int athleteID, int teamID)
	{
		try
		{
        // To connect to mongodb server
	        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , 27017 );
	    	
	        // Now connect to your databases
			DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollect = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
        
        DBCollection teamCollect = db.getCollection("teams");
        System.out.println("Collection teams selected successfully");
		
    	BasicDBObject queryFindAthlete = new BasicDBObject();
    	queryFindAthlete.append("athleteid", athleteID);
        
    	BasicDBObject queryFindTeam = new BasicDBObject();
    	queryFindTeam.append("teamid", teamID);
    	
    	BasicDBObject teamObject = (BasicDBObject) teamCollect.findOne(queryFindTeam);
    	
    	if (teamObject != null)
    	{
            BasicDBObject setTeam = new BasicDBObject().append("$set", new BasicDBObject().append("team", teamObject));
            athleteCollect.update(queryFindAthlete, setTeam);
    	}   
			
        mongoClient.close();
		
		}
		catch(Exception ex)
		{
		}
	}
		
	@SuppressWarnings("deprecation")
	public void GetAthleteIPO(int athleteID,String quote,int numberofshares, int orderseq){
		try
		{
        // To connect to mongodb server
	        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , 27017 );
	    	
	        // Now connect to your databases
			DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollect = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");

        DBCollection orderCollect = db.getCollection("orders");
        System.out.println("Collection orders selected successfully");
        
    	BasicDBObject queryFindAthlete = new BasicDBObject();
    	queryFindAthlete.append("athleteid", athleteID);
    	queryFindAthlete.append("listorders", new BasicDBObject("$exists", false));

    	
    	if (athleteCollect.find(queryFindAthlete).count() >= 0)
    	{
        	queryFindAthlete = new BasicDBObject();
        	queryFindAthlete.append("athleteid", athleteID);
        	
            BasicDBObject athleteIPO = new BasicDBObject().append("orderid",orderseq);
            athleteIPO.append("actiontype", "sell");
            athleteIPO.append("quantity", numberofshares);
            athleteIPO.append("recordstatusdate", new Date());
            athleteIPO.append("recordstatus", 1);
            athleteIPO.append("extathleteid", athleteID);
            BasicDBObject setOrders = new BasicDBObject().append("$push", new BasicDBObject().append("listorders", athleteIPO));
            BasicDBObject setAthleteValue = new BasicDBObject();
            setAthleteValue.append("availableshares", numberofshares);
            setAthleteValue.append("isavailable", true);
            setAthleteValue.append("isresellable", false);
            setOrders.append("$inc", new BasicDBObject().append("orderseq", 1));
            setOrders.append("$set", setAthleteValue);
            athleteCollect.update(queryFindAthlete, setOrders);
            orderCollect.insert(athleteIPO);
            BasicDBObject findInfo = (BasicDBObject) athleteCollect.findOne(queryFindAthlete);
            System.out.println(findInfo.toString());
    	}
			
        mongoClient.close();
		
		}
		catch(Exception ex)
		{
		    System.out.println(ex.toString());
		}
	}
	
	@SuppressWarnings("deprecation")
	public List<Customer> GetCustomer(Date orderDate)
	{
		try
		{
			List<Customer> customerList = new ArrayList<>();
		
        // To connect to mongodb server
	        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , 27017 );
	    	
	        // Now connect to your databases
			DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection coll = db.getCollection("customers");
        System.out.println("Collection mycol selected successfully");
			
        DBCursor cursor = coll.find();
        int i = 1;
			
        while (cursor.hasNext()) { 
           JSONParser parser = new JSONParser();
           Customer customer = (Customer) parser.parse(cursor.toString());
           customerList.add(customer);
           System.out.println("Inserted Document: "+i); 
           System.out.println(cursor.next()); 
           i++;
        }
        mongoClient.close();
		
		return customerList;
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public List<Order> GetOrders(Date orderDate)
	{
		try
		{
		
		List<Order> orderList = new ArrayList<>();
		
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , 27017 );
    	
        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection coll = db.getCollection("orders");
        System.out.println("Collection mycol selected successfully");
			
        DBCursor cursor = coll.find();
        int i = 1;
			
        while (cursor.hasNext()) { 
            JSONParser parser = new JSONParser();
            Order order = (Order) parser.parse(cursor.toString());
            orderList.add(order);
           System.out.println("Inserted Document: "+i); 
           System.out.println(cursor.next()); 
           i++;
        }
        mongoClient.close();
		
		return orderList;
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
	private String Shuffle(String input){
        List<Character> characters = new ArrayList<Character>();
        for(char c:input.toCharArray()){
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while(characters.size()!=0){
            int randPicker = (int)(Math.random()*characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }
	
}
