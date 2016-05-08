package avex.golem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import avex.models.*;

public class CustomerMaintenance {
	
	public void CustomerUpKeep(Date orderDate)
	{
		AVEXDB avexDB = new AVEXDB();
		List<Customer> customerList = new ArrayList<>();
		customerList = avexDB.GetCustomer(null);
		
		for(Customer customer:customerList)
		{
			customer = GetDeliquencies(customer);
			customer = SendStatements(customer);
			customer = SendNewCustomerEmails(customer,orderDate);
			customer = SendWithdrawalRequest(customer);
			customer = ProcessTransferRequest(customer);
		}
	}
	
	private Customer GetDeliquencies(Customer customer)
	{
		if(customer.getRecordStatus() == 4)
		{
			
			
		}
		return customer;
	}
	
	private Customer SendStatements(Customer customer)
	{
		return customer;
		
	}
	
	private Customer SendNewCustomerEmails(Customer customer,Date orderDate)
	{
	   if (customer.getRecordStatus() == 1)
		{
		
		   
		   
		}
	   return customer;
	}
	
	private Customer SendWithdrawalRequest(Customer customer)
	{
		if (customer.getRecordStatus() == 2)
		{
			
		}		
		return customer;
	}
	
	private Customer ProcessTransferRequest(Customer customer)
	{
		if (customer.getRecordStatus() == 3)
		{
			
		}
		return customer;
	}
}
