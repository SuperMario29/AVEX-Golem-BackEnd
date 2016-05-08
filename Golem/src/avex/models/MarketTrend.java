package avex.models;

import java.util.Date;
import java.util.List;

public class MarketTrend {
	
	private List<Market> todayTrends;
	private List<Athlete> todayTopGainers,todayTopLosers;
	private Date recordstatusdate;
	private int recordstatus;
			
	public List<Athlete> getTodayTopGainers() {
		return todayTopGainers;
	}
	public void setTodayTopGainers(List<Athlete> todayTopGainers) {
		this.todayTopGainers = todayTopGainers;
	}
	public List<Athlete> getTodayTopLosers() {
		return todayTopLosers;
	}
	public void setTodayTopLosers(List<Athlete> todayTopLosers) {
		this.todayTopLosers = todayTopLosers;
	}
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
	public List<Market> getTodayTrends() {
		return todayTrends;
	}
	public void setTodayTrends(List<Market> todayTrends) {
		this.todayTrends = todayTrends;
	}
	
}
