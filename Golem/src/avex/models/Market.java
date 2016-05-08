package avex.models;

import java.util.Date;

public class Market {
	
	private double marketprice;
	private Date recordstatusdate;
	private int recordstatus;
	
	public int getRecordstatus() {
		return recordstatus;
	}
	public void setRecordstatus(int recordstatus) {
		this.recordstatus = recordstatus;
	}
	public Date getRecordstatusdate() {
		return recordstatusdate;
	}
	public void setRecordstatusdate(Date recordstatusdate) {
		this.recordstatusdate = recordstatusdate;
	}
	public double getMarketprice() {
		return marketprice;
	}
	public void setMarketprice(double marketprice) {
		this.marketprice = marketprice;
	}
}
