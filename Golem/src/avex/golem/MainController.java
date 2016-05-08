package avex.golem;

import java.sql.Date;

public class MainController {

	public boolean isInitialLoad,isDailyMaint,isCustomerMaint,isOrderMaint,hasOrderDate,isAthleteMaint;
	public Date orderDate;
	
	public void GetArguments(String[] arguments)
	{
		for (int i = 0; i < arguments.length; i++) {
			//String value = arguments[i];
		    if(arguments[i].toLowerCase() == "/initialload")
		    {
		    	isInitialLoad = true;
		    }
		    else if (arguments[i].toLowerCase() == "/dailymaint")
		    {
		    	isDailyMaint = true;
		    }
		    else if (arguments[i].toLowerCase() == "/customermaint")
		    {
		    	isCustomerMaint = true;
		    }
		    else if (arguments[i].toLowerCase() == "/ordermaint")
		    {
		    	isOrderMaint = true;
		    }
		    else if (arguments[i].toLowerCase() == "/athletemaint")
		    {
		    	isAthleteMaint = true;
		    }
		    else if (arguments[i].toLowerCase().contains("/orderdate:"))
		    {
		    	orderDate = Date.valueOf(arguments[i].split(":")[1]);
		    	hasOrderDate = true;
		    }
		}
	}
	
}
