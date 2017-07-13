

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



public class BankQueue {

	private QueueL<Customer> customerLine;
	private int systemTime;
	
	public BankQueue(){
		customerLine = new QueueL<Customer>();
		try{
		
		    BufferedReader br = new BufferedReader(new FileReader(new File("BankQueue(1).txt")));
		    String line;
		    int custID=1;
		    while((line = br.readLine()) != null){
		       if(line.trim().length()>3){
		    	   String []val = line.split(" ");
		    	   try{
		    		   if(val.length == 2){
			    		   int minutes = this.toMins(val[0]);
			    		   int ProcessedTime = Integer.parseInt(val[1]);
			    		   Customer cust = new Customer(custID, minutes, ProcessedTime);
			    		   this.customerLine.enqueue(cust);
			    		   custID++;
		    		   }
		    	   }catch(Exception ex){}
		       }
		    }
		    br.close();
		   
		}catch (Exception e){
			System.out.println("File Not found"+e);
		}
		this.systemTime = this.customerLine.first().getTimeArrived();
	}
	
	
	
	

	private int toMins(String s) {
		String[] hourMin = s.split(":");
		int hour = Integer.parseInt(hourMin[0]);
		int mins = Integer.parseInt(hourMin[1]);
		int hoursInMins = hour * 60;
		return hoursInMins + mins;
	}

	private String toHours(int mins) {
		int hours = mins / 60; //since both are ints, you get an int
		int minutes = mins % 60;
		if(minutes<10){
			return hours+":0"+minutes;
		}

		return hours+":"+minutes;
	}

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BankQueue bqueue = new BankQueue();
		bqueue.simulateQueue();
	}
	public void simulateQueue(){
		
		int tellerMin=Integer.MAX_VALUE, lowestTime=Integer.MAX_VALUE;
		Teller []tellers = new Teller[6];
		for(int i=0;i<tellers.length;i++){
			tellers[i] = new Teller(this.systemTime,(i+1));
		}
		System.out.printf("Customer   ArrivalTime  TellerNumber CompletionTime   Processing Time    WaitTime");
		System.out.println();
		int dispWaitTime=0;
		while(!customerLine.isEmpty()){
			Customer temp = customerLine.dequeue();
			
			
			ArrayList<Teller> tellerCompleteObj = new ArrayList<Teller>();
			this.systemTime = temp.getTimeArrived();
			for(int i=0;i<tellers.length;i++){
				if(!tellers[i].isIdle()){
					int finishTime = (tellers[i].getCustomerAtTellerTime()+tellers[i].getCustomerInLine().getProcessedTime());
					if(temp.getTimeArrived()>= finishTime){
						tellers[i].getCustomerInLine().setProcessedTime(finishTime);
						tellers[i].setTimeServed(finishTime);
						tellerCompleteObj.add(tellers[i]);
					}
				}
			}
			
			
			
			Collections.sort(tellerCompleteObj,new CustomComp());
			for(int i=0;i<tellerCompleteObj.size();i++) {
				Teller t = tellerCompleteObj.get(i);
				Customer obj = t.getCustomerInLine();
				if(obj.getTimeWaitedinQueue()<0){
					dispWaitTime=obj.getTimeWaitedinQueue()*-1;
				}
				System.out.printf(obj.getcustomerToken()+"           "+this.toHours(obj.getTimeArrived())+"            "+t.getTellerId()+"           "+this.toHours(obj.getProcessedTime())+"            "+obj.getProcessedTime()+"               "+dispWaitTime);
				System.out.println();
				t.setCustomerInLine(null);
				
			}
			
			for(int i=0;i<tellers.length;i++){
				if(tellers[i].isIdle()){
					tellerMin = i;
					break;
				}else{
					if(lowestTime>=(tellers[i].getCustomerAtTellerTime()+tellers[i].getCustomerInLine().getProcessedTime())){
						lowestTime = tellers[i].getCustomerAtTellerTime()+tellers[i].getCustomerInLine().getProcessedTime();
						tellerMin = i;
					}
				}
			}
			
			if(tellers[tellerMin].isIdle()){
				tellers[tellerMin].setCustomerInLine(temp);
				tellers[tellerMin].setCustomerAtTellerTime(this.systemTime);
				tellers[tellerMin].addIntoTotalIdelTime(this.systemTime - tellers[tellerMin].getTimeServed());
			}else{
				
				Customer obj = tellers[tellerMin].getCustomerInLine();
				this.systemTime = tellers[tellerMin].getCustomerAtTellerTime() + obj.getProcessedTime()+1;
				obj.setProcessedTime(this.systemTime-1);
				if(obj.getTimeWaitedinQueue()<0){
					dispWaitTime=obj.getTimeWaitedinQueue()*-1;
				}
				System.out.printf(obj.getcustomerToken()+"           "+this.toHours(obj.getTimeArrived())+"            "+tellerMin+"           "+this.toHours(obj.getProcessedTime())+"            "+obj.getProcessedTime()+"               "+dispWaitTime);
				System.out.println();
				
				
				tellers[tellerMin].setCustomerInLine(temp);
				tellers[tellerMin].setCustomerAtTellerTime(this.systemTime);
				tellers[tellerMin].addIntoTotalIdelTime(0);
			}
		}
		
		
		for(int i=0;i<tellers.length;i++){
			if(!tellers[i].isIdle()){
				int finishTime = (tellers[i].getCustomerAtTellerTime()+tellers[i].getCustomerInLine().getProcessedTime());
				tellers[i].getCustomerInLine().setProcessedTime(finishTime);
				tellers[i].setTimeServed(finishTime);
				Customer obj = tellers[i].getCustomerInLine();
				if(obj.getTimeWaitedinQueue()<0){
					dispWaitTime=obj.getTimeWaitedinQueue()*-1;
				}
				System.out.printf(obj.getcustomerToken()+"           "+this.toHours(obj.getTimeArrived())+"            "+tellers[i].getTellerId()+"           "+this.toHours(obj.getProcessedTime())+"            "+obj.getProcessedTime()+"               "+dispWaitTime);
				System.out.println();
				tellers[i].setCustomerInLine(null);
			}
		}
		System.out.println();
		int idleTime = 0;
		for(int i=0;i<tellers.length;i++){
			System.out.println("Teller "+tellers[i].getTellerId()+" Wait Time  is "+tellers[i].getIdleTime()+" minutes");
			idleTime+=tellers[i].getIdleTime();
		}
		System.out.println("Average time : "+idleTime/(double)tellers.length);
	}
	
	class CustomComp implements Comparator<Teller> {
	    public int compare(Teller t1, Teller t2) {
	        if(t1.getCustomerInLine().getProcessedTime()<t2.getCustomerInLine().getProcessedTime() ){
	        	return -1;
	        }else if(t1.getCustomerInLine().getProcessedTime()>t2.getCustomerInLine().getProcessedTime() ){
	        	return 1;
	        }else{
	        	if(t1.getCustomerInLine().getcustomerToken()<t2.getCustomerInLine().getcustomerToken()){
	        		return -1;
	        	}else{
	        		return 1;
	        	}
	        }
	    }
	}
}

