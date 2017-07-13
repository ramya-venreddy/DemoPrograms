

public class Teller {
	
	private int idleTime;
	private int customerAtTellerTime;
	private int tellerId;
	private Customer customerInLine;
	private int timeServed;

	public int getIdleTime() {
		return idleTime;
	}

	public int getTellerId() {
		return tellerId;
	}

	public Teller(int ServicingTime,int tellerId) {
		this.idleTime = 0;
		this.customerInLine = null;
		this.timeServed = ServicingTime;
		this.customerAtTellerTime = 0;
		this.tellerId = tellerId;
	}
	
	public void setCustomerAtTellerTime(int startTime){
		this.customerAtTellerTime = startTime;
	}
	
	
	public int getCustomerAtTellerTime(){
		return this.customerAtTellerTime;
	}
	
	public boolean isFinished(int currentTime){
		if(currentTime <= (this.customerAtTellerTime+this.customerInLine.getProcessedTime())){
			return true;
		}
		return false;
	}
	public int getTimeServed() {
		return timeServed;
	}
	public void setTimeServed(int timeServed) {
		this.timeServed = timeServed;
	}
	
	public void addIntoTotalIdelTime(int idleTime) {
		this.idleTime += idleTime;
	}
	
	
	public boolean isIdle(){
		if(this.customerInLine == null){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public Customer getCustomerInLine() {
		return customerInLine;
	}
	
	public void setCustomerInLine(Customer customerInLine) {
		this.customerInLine = customerInLine;
	}
	

	
	
}
