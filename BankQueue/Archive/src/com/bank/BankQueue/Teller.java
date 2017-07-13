package com.bank.BankQueue;

import java.util.Random;
import java.util.Scanner;
import java.io.*;
public class Teller
{

	Customer c=new Customer();

	int  runTime, customerArrival, transactionTime, processingTime;
	
	
	boolean free;
	public Teller(){
		free=true;
	}
	public static void main(String[]args) throws FileNotFoundException, IOException
	{

		File f=new File("BankQueue.txt");
		BufferedReader br=new BufferedReader(new FileReader(f));
		int numOfCustomers=Integer.parseInt(br.readLine());
		Customer[] cust=new Customer[numOfCustomers];
		String[] arr=new String[numOfCustomers];
		String[] custArrivalTime=new String[numOfCustomers];
		int[] custProcessingTime=new int[numOfCustomers];
		for(int i=0;i<numOfCustomers;i++){
			arr[i]=br.readLine();
			String[] tmp=arr[i].split(" ");
			custArrivalTime[i]=tmp[0];
			custProcessingTime[i]=Integer.parseInt(tmp[1]);

		}
		for(int cA=0;cA<111;cA++){
			cust[cA]=new Customer();
			cust[cA].clock=toMins(custArrivalTime[cA]);

		}


		Teller[] t=new Teller[6];
		for(int x=0;x<6;x++){
			t[x]=new Teller();
		}

		int telNum=0,count=0;
		System.out.println(" CustomerNumber  ArrivalTime  TellerNumber CompletionTime ProcessingTime ");
		for(int i=0;i<numOfCustomers;i++){
			StringBuilder strout=new StringBuilder();
			if(telNum<6){
				if(t[telNum].free){

					strout.append(i+1+"                  ");

					strout.append(custArrivalTime[i]+"               ");
					t[telNum].free=processCustomer(cust[i],custProcessingTime[i],t[telNum],telNum,strout);


					telNum++;		   				
				}
			}
			if(telNum>=6){

				telNum=0;

			}

		}




	}//end main
	private static int toMins(String s) {
		String[] hourMin = s.split(":");
		int hour = Integer.parseInt(hourMin[0]);
		int mins = Integer.parseInt(hourMin[1]);
		int hoursInMins = hour * 60;
		return hoursInMins + mins;
	}
	private static String toHours(int mins) {
		int hours = mins / 60; //since both are ints, you get an int
		int minutes = mins % 60;
		if(minutes<10){
			return hours+":0"+minutes;
		}

		return hours+":"+minutes;
	}

	private static boolean processCustomer(Customer c, int processingTime, Teller t, int tellerNumber,StringBuilder strout){
		c.processedTime=c.clock+processingTime;
		strout.append(tellerNumber+"                 ");
		strout.append(toHours(c.processedTime)+"                  ");
		strout.append(processingTime+"                ");

		String str=strout.toString();
		System.out.print(str);


		System.out.println();
		//System.out.println("Wait Time "+processingTime);


		return true;
	}
}