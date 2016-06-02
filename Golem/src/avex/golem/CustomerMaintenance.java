package avex.golem;

import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;

public class CustomerMaintenance {
	
	public void CustomerUpKeep()
	{
		PaymentsApi paymentsAPI = new PaymentsApi();
		CustomerCollection customerList = paymentsAPI.GetListofCustomers();
		 
		for(Customer customer:customerList.autoPagingIterable())
		{
			GetDeliquencies(customer);
			SendStatements(customer);
			SendNewCustomerEmails(customer);
			SendWithdrawalRequest(customer);
			ProcessTransferRequest(customer);
		}
	}
	
	private void GetDeliquencies(Customer customer)
	{
		if(customer.getDelinquent()){
			
		}
	}
	
	private void SendStatements(Customer customer)
	{
		
		
	}
	
	private void SendNewCustomerEmails(Customer customer)
	{


	}
	
	private void SendWithdrawalRequest(Customer customer)
	{
	
	}
	
	private void ProcessTransferRequest(Customer customer)
	{

	}
}
