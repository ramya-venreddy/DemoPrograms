


public class Customer {
	private int customerToken;
	private int timeArrived;
	private int processedTime;
	private int processingTime;
	
	public Customer(){
		
	}
	
	public Customer(int customerToken, int timeArrived, int processingTime,int processedTime) {
		this.customerToken = customerToken;
		this.timeArrived = timeArrived;
		this.processingTime = processingTime;
		this.processedTime=processedTime;
	}
	
	public Customer(int customerToken, int timeArrived, int processingTime) {
		this.customerToken = customerToken;
		this.timeArrived = timeArrived;
		this.processingTime = processingTime;
	}
	
	public int getcustomerToken() {
		return customerToken;
	}
	public void setcustomerToken(int customerToken) {
		this.customerToken = customerToken;
	}
	public int getTimeArrived() {
		return timeArrived;
	}
	public void setProcessingTime(int processingTime) {
		this.processingTime = processingTime;
	}
	
	public int getTimeWaitedinQueue(){
		return (this.processedTime - timeArrived - processingTime);
	}
	public void setTimeArrived(int timeArrived) {
		this.timeArrived = timeArrived;
	}
	public int getProcessedTime() {
		return processedTime;
	}
	public void setProcessedTime(int watingTime) {
		this.processedTime = watingTime;
	}
	public int getProcessingTime() {
		return processingTime;
	}

}
