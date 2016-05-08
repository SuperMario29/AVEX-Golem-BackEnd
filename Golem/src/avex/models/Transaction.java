package avex.models;

public class Transaction {

    private String description,recordStatusDate;
    private boolean isdeposit,iswithdrawl,isrefund,isdividend;
    private double dollarvalue,commission;
    private int recordStatus;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRecordStatusDate() {
		return recordStatusDate;
	}
	public void setRecordStatusDate(String recordStatusDate) {
		this.recordStatusDate = recordStatusDate;
	}
	public boolean isIsdeposit() {
		return isdeposit;
	}
	public void setIsdeposit(boolean isdeposit) {
		this.isdeposit = isdeposit;
	}
	public boolean isIswithdrawl() {
		return iswithdrawl;
	}
	public void setIswithdrawl(boolean iswithdrawl) {
		this.iswithdrawl = iswithdrawl;
	}
	public boolean isIsrefund() {
		return isrefund;
	}
	public void setIsrefund(boolean isrefund) {
		this.isrefund = isrefund;
	}
	public boolean isIsdividend() {
		return isdividend;
	}
	public void setIsdividend(boolean isdividend) {
		this.isdividend = isdividend;
	}
	public double getDollarvalue() {
		return dollarvalue;
	}
	public void setDollarvalue(double dollarvalue) {
		this.dollarvalue = dollarvalue;
	}
	public double getCommission() {
		return commission;
	}
	public void setCommission(double commission) {
		this.commission = commission;
	}
	public int getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(int recordStatus) {
		this.recordStatus = recordStatus;
	}
	    
}
