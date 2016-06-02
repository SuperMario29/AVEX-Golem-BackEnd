package avex.golem;

import java.util.HashMap;
import java.util.Map;

import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;

public class PaymentsApi {
	
	private String STRIPE_API_KEY = "sk_test_7itp1Ch8S4JWgslVvG5qknUN";
	
	
	public CustomerCollection GetListofCustomers(){
		try {
			Stripe.apiKey = STRIPE_API_KEY;

			Map<String, Object> customerParams = new HashMap<String, Object>();
			//customerParams.put("limit", 3);
			CustomerCollection customer = Customer.list(customerParams);
			return customer;
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InvalidRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (APIConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public Customer GetCustomerBalance(String stripeaccount){
		Stripe.apiKey = STRIPE_API_KEY;
try{
		//Map<String, Object> customerParams = new HashMap<String, Object>();
		//customerParams.put("limit", 3);
		Customer customer = Customer.retrieve(stripeaccount);
		return customer;
	} catch (AuthenticationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	} catch (InvalidRequestException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	} catch (APIConnectionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	} catch (CardException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	} catch (APIException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
	}

public boolean UpdateCustomerBalance(String stripeaccount,int accountbalance){

	boolean results = false;
	
	
	return results;
	
	
}


}
