package avex.golem;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mongodb.MongoClient;
import com.stripe.model.CustomerCollection;

import avex.models.Customer;
import avex.models.Order;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;

import org.bson.types.ObjectId;
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
        
        System.out.println("Got Athlete List Successfully");

		return athleteList;
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
			return null;
		}
	}
	
	public BasicDBObject GetUser(String customerid){
		BasicDBObject results = new BasicDBObject();
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		
		try
		{		
	     System.out.println("Get User: " + customerid);	
        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection customerCollection = db.getCollection("customers");
        System.out.println("Collection customers selected successfully");
		        
        results = (BasicDBObject) customerCollection.findOne(new BasicDBObject().append("_id", new ObjectId(customerid)));
			
        mongoClient.close();
        
        System.out.println("Got Customer Information Successfully");

		return results;
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
			return null;
		}
	}
	
	public boolean SaveUser(BasicDBObject user,BasicDBObject updatePosition, BasicDBObject transactionHistory){
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
    	queryFind.append("_id", new ObjectId(user.getString("_id")));
    	
    	if(!updatePosition.isEmpty()){    	customersCollect.update(queryFind, updatePosition);	}
    	if(!transactionHistory.isEmpty()){    	customersCollect.update(queryFind, transactionHistory);	}    	
        mongoClient.close();
        
        System.out.println("Saved Customer Successfully");
		
        return true;
        
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
			return false;
		}
	}
	
	public boolean UpdateOrder(String orderid,BasicDBObject updateOrder){
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
    	queryFind.append("_id", new ObjectId(orderid));
    	
    	if(!updateOrder.isEmpty()){    
    		BasicDBObject order = new BasicDBObject();
    		order.append("$set", updateOrder);
    		ordersCollect.update(queryFind, order);	}
    	
        System.out.println("Saved Order Successfully");
    	
        mongoClient.close();
		
        return true;
        
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
			return false;
		}
	}
		
	public boolean CreateNewOrder(BasicDBObject newOrder){
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
		    	
    	ordersCollect.insert(newOrder);
    	
        mongoClient.close();
        
        System.out.println("Created Order Successfully");
		
        return true;
        
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
			return false;
		}
	}
	
	public void CompleteAthleteQueue(String athleteID){
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{	
		System.out.println("Get Athlete Queue: " + athleteID);		
        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollection = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
		                
        athleteCollection.update(new BasicDBObject().append("_id",  new ObjectId(athleteID)), new BasicDBObject().append("$inc", new BasicDBObject().append("currentqueue", 1)));
	
        System.out.println("Update CurrentQueue successfully");
        
        mongoClient.close();

		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
		}
		
		
	}
	
	public boolean SaveAthlete(BasicDBObject athlete,BasicDBObject price, BasicDBObject shares, BasicDBObject order, BasicDBObject pricehistory){		
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{
		     System.out.println("Save Athlete Data: " + athlete);
		// Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollect = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
		

    	BasicDBObject queryFindAthlete = new BasicDBObject();
    	queryFindAthlete.append("_id",  new ObjectId(athlete.getString("_id")));
    	
    	if(!price.isEmpty()){    	athleteCollect.update(queryFindAthlete, price);    	}
    	if(!shares.isEmpty()){    	athleteCollect.update(queryFindAthlete, shares);    	}
    	if(!order.isEmpty()){    	athleteCollect.update(queryFindAthlete, order);    	}
    	if(!pricehistory.isEmpty()){	athleteCollect.update(queryFindAthlete, pricehistory);    	}
    	
        mongoClient.close();
        
        System.out.println("Saved Athlete successfully");
		
        return true;
        
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
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
    	
        System.out.println("Received Settings Successfully");
    	 	
        mongoClient.close();
		
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
		}
		
		return results;
	}
	
	public BasicDBObject GetAthleteByID(String athleteID){
		BasicDBObject results = new BasicDBObject();
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{
		System.out.println("Get Athlete By ID: " + athleteID);	
	        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollect = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
		
    	BasicDBObject queryFindAthlete = new BasicDBObject();
    	queryFindAthlete.append("_id",  new ObjectId(athleteID));
        
    	results = (BasicDBObject) athleteCollect.findOne(queryFindAthlete);
    	 
        mongoClient.close();
		
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
		}
		
        System.out.println("Got Athlete By Athlete ID successfully");
		
		return results;		
	}
	
	public List<BasicDBObject> GetOrdersByAthleteId(int actiontype, String athleteID,int extathleteID){
		List<BasicDBObject> results = new ArrayList<>();
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
        
		try
		{	
			List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
			List<BasicDBObject> obj1 = new ArrayList<BasicDBObject>();
			
			obj1.add(new BasicDBObject().append("athleteid", athleteID));
			obj1.add(new BasicDBObject().append("extathleteid", extathleteID));
			BasicDBObject athleteFind = new BasicDBObject();
			athleteFind.put("$or",obj1);
			
			obj.add(athleteFind);
			obj.add(new BasicDBObject().append("recordstatus", new BasicDBObject().append("$ne", 3)));
			obj.add(new BasicDBObject().append("recordstatus", new BasicDBObject().append("$ne", 4)));
			
			//ActionType 1 equals Buy Order
			if(actiontype == 1){
				obj.add(new BasicDBObject().append("actiontype", "sell"));
			     System.out.println("Get Sell Orders for AthleteID: " + athleteID);	
			}
			//ActionType 2 equals Sell Order
			else if(actiontype == 2){
				obj.add(new BasicDBObject().append("actiontype", "buy"));
				 System.out.println("Get Buy Orders for AthleteID: " + athleteID);	
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
        
        System.out.println("Received orders by athlete successfully"); 

        mongoClient.close();

		return results;
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
			return null;
		}
	}
		
	public long GetUserQueuePosition(String athleteID){
		long results = 0;
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );		
		
		try
		{
	    System.out.println("Get Customer Queue Position for AthleteID: " + athleteID);	
        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollection = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
		                
        BasicDBObject x = (BasicDBObject) athleteCollection.findAndModify(new BasicDBObject().append("_id",  new ObjectId(athleteID)), new BasicDBObject().append("$inc", new BasicDBObject().append("nextqueue", 1)));
		
        results = x.getLong("nextqueue");

		System.out.println("CUSTOMER POSITION: " + results);
        
        mongoClient.close();

		return results;
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
			return -1;
		}	
	}
	
	public Boolean CurrentQueue(long userposition,String athleteID){		
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{
		System.out.println("Get Current Queue Position for AthleteID: " + athleteID);	
        // Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollection = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
		                
        BasicDBObject x = (BasicDBObject) athleteCollection.findOne(new BasicDBObject().append("_id",  new ObjectId(athleteID)));
		
        System.out.println("CURRENT QUEUE:" + x.getLong("currentqueue"));
        System.out.println("USER QUEUE:" + userposition);
        
        if(userposition == x.getLong("currentqueue")){
        	System.out.println("READY FOR PROCESSING");	
            mongoClient.close();
        	return true;
        }
        else{
        	System.out.println("NOT READY YET FOR PROCESSING");	
            mongoClient.close();
        	return false;
        }
        
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Class: AVEXDB - Method: CurrentQueue- Exception: " + ex.getMessage());
			System.out.println("Class: AVEXDB - Method: CurrentQueue- Stack Trace: " + ex.getStackTrace());
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
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
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
            BasicDBObject availablity = new BasicDBObject().append("$set", new BasicDBObject().append("isavailable", true));

            athleteCollect.update(query, setNewValue);
            athleteCollect.update(query, availablity);
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
			
        mongoClient.close();
		
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
		}
	}
	
	@SuppressWarnings("deprecation")
	public void GetAthleteQuote(int athleteID,String playerName)
	{
		System.out.println("Get Athlete Quote for AthleteID: " + athleteID);	
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
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
		}
	}
	
	@SuppressWarnings("deprecation")
	public void UpdateCurrentPrice(int athleteID, DBObject value){
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try{
			System.out.println("Update Current Price for AthleteID: " + athleteID);	
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
			Double currentPrice = Double.valueOf(String.valueOf(value.get("currentprice")));

            BasicDBObject setPrice = new BasicDBObject().append("$set", new BasicDBObject().append("currentprice", currentPrice));
            BasicDBObject priceHistory = new BasicDBObject().append("price",currentPrice);
            priceHistory.append("isathletevalueprice", true);
            priceHistory.append("recordstatusdate", new Date());
            priceHistory.append("recordstatus", 1);
            BasicDBObject setHistory = new BasicDBObject().append("$push", new BasicDBObject().append("pricehistory", priceHistory));
            athleteCollect.update(queryFindAthlete, setPrice);
            athleteCollect.update(queryFindAthlete, setHistory);
    	}   
			
        mongoClient.close();
		
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Method:UpdateCurrentPrice Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
		}
	}
	
	@SuppressWarnings("deprecation")
	public void AthleteUnavailable(int athleteID){
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try{
			System.out.println("Update Current Price for AthleteID: " + athleteID);	
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
            BasicDBObject availablity = new BasicDBObject().append("$set", new BasicDBObject().append("isavailable", false));
            athleteCollect.update(queryFindAthlete, availablity);
    	}   
			
        mongoClient.close();
		
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Method:UpdateCurrentPrice Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
		}
	}
	
	@SuppressWarnings("deprecation")
	public void UpdateTeamByAthlete(int athleteID, int teamID)
	{
		System.out.println("Update Team By Athlete for AthleteID: " + athleteID);	
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
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
		}
	}
		
	@SuppressWarnings("deprecation")
	public void GetAthleteIPO(int athleteID,String quote,int numberofshares, int orderseq){
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{
			System.out.println("Get Athlete IPO for AthleteID: " + athleteID);	
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
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
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
	
	public void UpdateAthleteImage(String athlete, BasicDBObject value){
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient(Program.DATABASE_CONNECTION , Program.DATABASE_PORT );
		try
		{
		     System.out.println("Save Athlete Data: " + athlete);
		// Now connect to your databases
		DB db = mongoClient.getDB(Program.DATABASE_NAME);
        System.out.println("Connect to database successfully");
			
        //boolean auth = db.authenticate(myUserName, myPassword);
        //System.out.println("Authentication: "+auth);         
			
        DBCollection athleteCollect = db.getCollection("athletes");
        System.out.println("Collection athletes selected successfully");
		
    	BasicDBObject queryFindAthlete = new BasicDBObject();
    	queryFindAthlete.append("_id",  new ObjectId(athlete));
    	
    	if(!value.isEmpty()){    	athleteCollect.update(queryFindAthlete, value);    	}
    	
        mongoClient.close();
        
        System.out.println("Saved Athlete successfully");        
		}
		catch(Exception ex)
		{
	        mongoClient.close();
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("Stack Trace: " + ex.getStackTrace());
		}
	}
}
