package avex.models;
import java.util.List;

public class Order {
	
    private String playerquote,actiontype,ordertype,quantity,recordStatusDate;
    private long athleteID,customerID,customerPositionId;
    private double estimatecommission, estimatedCost;
    private List<Transaction> transactionList;
    private int recordStatus;
	public String getPlayerquote() {
		return playerquote;
	}
	public void setPlayerquote(String playerquote) {
		this.playerquote = playerquote;
	}
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
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getRecordStatusDate() {
		return recordStatusDate;
	}
	public void setRecordStatusDate(String recordStatusDate) {
		this.recordStatusDate = recordStatusDate;
	}
	public long getAthleteID() {
		return athleteID;
	}
	public void setAthleteID(long athleteID) {
		this.athleteID = athleteID;
	}
	public long getCustomerID() {
		return customerID;
	}
	public void setCustomerID(long customerID) {
		this.customerID = customerID;
	}
	public long getCustomerPositionId() {
		return customerPositionId;
	}
	public void setCustomerPositionId(long customerPositionId) {
		this.customerPositionId = customerPositionId;
	}
	public double getEstimatecommission() {
		return estimatecommission;
	}
	public void setEstimatecommission(double estimatecommission) {
		this.estimatecommission = estimatecommission;
	}
	public double getEstimatedCost() {
		return estimatedCost;
	}
	public void setEstimatedCost(double estimatedCost) {
		this.estimatedCost = estimatedCost;
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
    
}
