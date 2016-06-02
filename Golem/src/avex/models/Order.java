package avex.models;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBObject;

public class Order extends BasicDBObject  implements Comparable<Order> {
	
    private String quote,actiontype,ordertype,athleteID,customerID,orderid;
    private double commission, cost,price;
    private List<Transaction> transactionList;
    private int recordStatus,quantity;
    private Date recordstatusdate;
    private Boolean ispending;

    
	public String getActiontype() {
		return actiontype;
	}
	public void setActiontype(String actiontype) {
		this.actiontype = actiontype;
	}
	public String getOrdertype() {
		return ordertype;
	}
	public void setOrdertype(String ordertype) {
		this.ordertype = ordertype;
	}

	public List<Transaction> getTransactionList() {
		return transactionList;
	}
	public void setTransactionList(List<Transaction> transactionList) {
		this.transactionList = transactionList;
	}
	public int getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(int recordStatus) {
		this.recordStatus = recordStatus;
	}
	
	public Date getRecordstatusdate() {
		return recordstatusdate;
	}
	public void setRecordstatusdate(Date recordstatusdate) {
		this.recordstatusdate = recordstatusdate;
	}
	
	  @Override
	  public int compareTo(Order o) {
	    return getRecordstatusdate().compareTo(o.getRecordstatusdate());
	  }
	public String getQuote() {
		return quote;
	}
	public void setQuote(String quote) {
		this.quote = quote;
	}
	public String getAthleteID() {
		return athleteID;
	}
	public void setAthleteID(String athleteID) {
		this.athleteID = athleteID;
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	public double getCommission() {
		return commission;
	}
	public void setCommission(double commission) {
		this.commission = commission;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public Boolean getIspending() {
		return ispending;
	}
	public void setIspending(Boolean ispending) {
		this.ispending = ispending;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

    
}
