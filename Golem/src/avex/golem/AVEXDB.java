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
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import org.json.simple.parser.JSONParser;

public class AVEXDB {

	@SuppressWarnings("deprecation")
	public List<BasicDBObject> GetAthletes()
	{
        int i = 1;
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{
		List<BasicDBObject> athleteList = new ArrayList<>();
    	
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
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
			return null;
		}
	}
	
	public BasicDBObject GetUser(String customerid){
		BasicDBObject results = new BasicDBObject();
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		
		try
		{		    	
        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection customerCollection = db.getCollection("customers");
        System.out.println("Collection customers selected successfully");
		        
        results = (BasicDBObject) customerCollection.findOne(new BasicDBObject().append("_id", customerid));
			

        mongoClient.close();

		return results;
		}
		catch(Exception ex)
		{
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
			return null;
		}
	}
	
	public boolean SaveUser(BasicDBObject user){
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{	    	
	        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection customersCollect = db.getCollection("customers");
        System.out.println("Collection customers selected successfully");
		
    	BasicDBObject queryFind = new BasicDBObject();
    	queryFind.append("_id", user.getString("_id"));
    	
    	customersCollect.update(queryFind, user);
    	
        mongoClient.close();
		
        return true;
        
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			return false;
		}
	}
	
	public boolean SaveOrder(Order order){
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{	    	
	    // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection ordersCollect = db.getCollection("orders");
        System.out.println("Collection orders selected successfully");
		

    	BasicDBObject queryFind = new BasicDBObject();
    	queryFind.append("_id", order.getString("_id"));
    	
    	ordersCollect.update(queryFind, order);
    	
        mongoClient.close();
		
        return true;
        
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			return false;
		}
	}
	
	public void CompleteAthleteQueue(String athleteid){
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{		
        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollection = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
		                
        athleteCollection.update(new BasicDBObject().append("_id", athleteid), new BasicDBObject().append("$inc", new BasicDBObject().append("currentqueue", 1)));
	
        mongoClient.close();

		}
		catch(Exception ex)
		{
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
		}
		
		
	}
	
	public boolean SaveAthlete(BasicDBObject athlete){		
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{	    	
	        // Now connect to your databases
			DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollect = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
		

    	BasicDBObject queryFindAthlete = new BasicDBObject();
    	queryFindAthlete.append("_id", athlete.getString("_id"));
    	
    	athleteCollect.update(queryFindAthlete, athlete);
    	
        mongoClient.close();
		
        return true;
        
		}
		catch(Exception ex)
		{
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
			return false;
		}
	}
	
	public BasicDBObject GetSettings(){
		BasicDBObject results = new BasicDBObject();
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{    	
	        // Now connect to your databases
			DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection settingsCollect = db.getCollection("settings");
        System.out.println("Collection settings selected successfully");
		
    	results = (BasicDBObject) settingsCollect.findOne();
    	 	
        mongoClient.close();
		
		}
		catch(Exception ex)
		{
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
		}
		
		return results;
	}
	
	public BasicDBObject GetAthleteByID(String athleteID){
		BasicDBObject results = new BasicDBObject();
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{
	        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollect = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
		
    	BasicDBObject queryFindAthlete = new BasicDBObject();
    	queryFindAthlete.append("athleteid", athleteID);
        
    	results = (BasicDBObject) athleteCollect.findOne(queryFindAthlete);
    	 
        mongoClient.close();
		
		}
		catch(Exception ex)
		{
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
		}
		
		
		return results;		
	}
	
	public List<BasicDBObject> GetOrderByAthleteId(int actiontype, String athleteID){
		List<BasicDBObject> results = new ArrayList<>();
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
        
		try
		{	
			List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
			obj.add(new BasicDBObject().append("athleteid", athleteID));
			obj.add(new BasicDBObject().append("recordstatus", new BasicDBObject().append("$ne", 3)));
			obj.add(new BasicDBObject().append("recordstatus", new BasicDBObject().append("$ne", 4)));
			
			//ActionType 1 equals Buy Order
			if(actiontype == 1){
				obj.add(new BasicDBObject().append("actiontype", "sell"));
			}
			//ActionType 2 equals Sell Order
			else if(actiontype == 2){
				obj.add(new BasicDBObject().append("actiontype", "buy"));	
			}
			
			BasicDBObject andQuery = new BasicDBObject();
			andQuery.put("$and", obj);	
			
    	
        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection ordersCollection = db.getCollection("orders");
        System.out.println("Collection orders selected successfully");
        		
        DBCursor cursor = ordersCollection.find(andQuery).sort(new BasicDBObject().append("recordstatusdate", -1));
			
        while (cursor.hasNext()) { 
           BasicDBObject order = (BasicDBObject) cursor.next();
           results.add(order); 
        }
        
        System.out.println("Received orders"); 

        mongoClient.close();

		return results;
		}
		catch(Exception ex)
		{
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
			return null;
		}
	}
	
	
	public long GetUserQueuePosition(String athleteid){
		long results = 0;
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );		
		
		try
		{		    	
        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollection = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
		                
        BasicDBObject x = (BasicDBObject) athleteCollection.findAndModify(new BasicDBObject().append("_id", athleteid), new BasicDBObject().append("$inc", new BasicDBObject().append("nextqueue", 1)));
		
        results = x.getLong("nextqueue");

        mongoClient.close();

		return results;
		}
		catch(Exception ex)
		{
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
			return -1;
		}	
	}
	
	public Boolean CurrentQueue(long userposition,String athleteid){		
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{		    	
        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollection = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
		                
        BasicDBObject x = (BasicDBObject) athleteCollection.findOne(new BasicDBObject().append("_id", athleteid));
		
        if(userposition == x.getLong("currentqueue")){
            mongoClient.close();
        	return true;
        }
        else{
            mongoClient.close();
        	return false;
        }
        
		}
		catch(Exception ex)
		{
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
			return false;
		}	
	}
	
	
	@SuppressWarnings("deprecation")
	public List<BasicDBObject> GetOrders()
	{
        int i = 1;
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{
		List<BasicDBObject> ordersList = new ArrayList<>();
    	
        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection ordersCollection = db.getCollection("orders");
        System.out.println("Collection orders selected successfully");
		
    	BasicDBObject queryFindOrders = new BasicDBObject();
    	queryFindOrders.append("recordstatus", new BasicDBObject("$ne", 3));
    	queryFindOrders.append("recordstatus", new BasicDBObject("$ne", 4));
        
        DBCursor cursor = ordersCollection.find(queryFindOrders);
			
        while (cursor.hasNext()) { 
           BasicDBObject order = (BasicDBObject) cursor.next();
           ordersList.add(order); 
           i++;
        }
        System.out.println("Received "+ i + " orders"); 

        mongoClient.close();

		return ordersList;
		}
		catch(Exception ex)
		{
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
			return null;
		}
	}
	
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public void SendAthleteValuetoDB(Map<Integer,BasicDBObject> athletes)
	{
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{    	
	        // Now connect to your databases
			DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollect = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
		
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
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
		}
	}
	
	@SuppressWarnings("deprecation")
	public void GetAthleteQuote(int athleteID,String playerName)
	{
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{
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
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
		}
	}
	
	@SuppressWarnings("deprecation")
	public void UpdateCurrentPrice(int athleteID, DBObject value){
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try{
	    	
	        // Now connect to your databases
			DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollect = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
       
    	BasicDBObject queryFindAthlete = new BasicDBObject();
    	queryFindAthlete.append("athleteid", athleteID);
        
    	BasicDBObject athleteObject = (BasicDBObject) athleteCollect.findOne(queryFindAthlete);
    	
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
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
		}
	}
	
	@SuppressWarnings("deprecation")
	public void UpdateTeamByAthlete(int athleteID, int teamID)
	{
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{
	    	
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
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
		}
	}
		
	@SuppressWarnings("deprecation")
	public void GetAthleteIPO(int athleteID,String quote,int numberofshares, int orderseq){
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{
	    	
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
	        mongoClient.close();
		    System.out.println(ex.getLocalizedMessage());
		}
	}
	
	@SuppressWarnings("deprecation")
	public List<Customer> GetCustomer(Date orderDate)
	{
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{
			List<Customer> customerList = new ArrayList<>();
	    	
	        // Now connect to your databases
			DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection coll = db.getCollection("customers");
        System.out.println("Collection customers selected successfully");
			
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
	        mongoClient.close();
            System.out.println("Exception: " + ex.getLocalizedMessage()); 
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
