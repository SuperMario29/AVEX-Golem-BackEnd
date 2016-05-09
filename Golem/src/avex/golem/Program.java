package avex.golem;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Program {

	public static String DATABASE_NAME = "";
	public static String DATABASE_CONNECTION = "";
	
	public static void main(String[] args) {

		Properties prop = new Properties();
		InputStream input = null;
		
		try{
		input = new FileInputStream("config.properties");

		// load a properties file
		prop.load(input);
		
            //get the property value and print it out
            DATABASE_NAME = prop.getProperty("databasename");
	        DATABASE_CONNECTION = prop.getProperty("databaseconnection");
		
			AthleteMaintenance athleteMaint = new AthleteMaintenance();
			athleteMaint.AthleticUpKeep(null);
			
			//CustomerMaintenance customerMaint = new CustomerMaintenance();
			//customerMaint.CustomerUpKeep(null);
			
			//OrderMaintenance orderMaint = new OrderMaintenance();
			//orderMaint.OrderUpKeep(null);
		}
		catch(Exception ex){
			
		}
		
		

	}

}
