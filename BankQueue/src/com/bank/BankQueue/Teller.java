package com.bank.BankQueue;

import java.util.Random;
import java.util.Scanner;
import java.io.*;
public class Teller
{

	Customer c=new Customer();

	int  runTime, customerArrival, transactionTime, processingTime;
	
	int customers=0;
	boolean free;
	public Teller(){
		free=true;
	}
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
	public static void main(String[]args) throws FileNotFoundException, IOException
	{

		File f=new File("BankQueue(1).txt");
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
		//simulation(numOfCustomers, 840, ); 
		
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
	
	
	
    /**
     * This methods performs a simulation of a Bank operation using a queue and
     * prints the results.
     * @param qCapacity The fixed capacity of the queue to be used.
     * @param simHours The number of hours that the simulation should run.
     * @param custPerHour The expected number of customers to arrive per hour.
     */
   /* private static void simulation(int qCapacity, int simHours, int custPerHour) {
        //Constant
        final int MIN_PER_HR=60;

        //A queue that will hold and manage objects of type Customer.
        Queue<Customer> line = new ArrayQueue<Customer>(qCapacity);

        //For how many cycles should the simulation run. We assume that each
        //cycle takes one minute.
        int cycleLimit = 840;

        //The average number of minutes that will pass until the next customer
        //arrives.
        float minPerCust = ((float)MIN_PER_HR)/custPerHour;

        //The number of customers that were turned away because the line (queue)
        //was full at the time they arrived.
        int turnAways = 0;

        //Number of customers that arrived.
        int customers = 0;

        //Number of customers that were served.
        int served = 0;

        //Total number of customers that entered the line (queue).
        int sumLine = 0;

        //Waiting time until the next customer is served.
        int waitTime = 0;

        //Total time that all the customers waited int he line.
        int lineWait = 0;

        //Simulation
        for (int cycle = 0; cycle < 840; cycle++) {
            if (newCustomer(minPerCust)) {
                if (line.isFull()) {
                    turnAways++;
                } else {
                    customers++;
                    Customer customer = new Customer();
                    customer.set(cycle);
                    line.enqueue(customer);
                }
            }

            if (waitTime <= 0 && !line.isEmpty()) {
                Customer customer = (Customer) line.dequeue();
                waitTime = customer.procTime();
                lineWait += cycle - customer.when();
                served++;
            }

            if (waitTime > 0) {
                waitTime--;
            }

            sumLine += line.size();
        }

        //Print the simulation results.
        if (customers > 0) {
            System.out.println("\nCustomers accepted: " + customers);
            System.out.println("  Customers served: " + served);
            System.out.println(" Customers waiting: " + line.size());
            System.out.println("         Turnaways: " + turnAways);
            System.out.println("Average queue size: " + (float) sumLine / cycleLimit);
            System.out.println(" Average wait time: " + (float) lineWait / served + " minutes");
        } else {
            System.out.println("No customers!");
        }
    }

    *//**
     * This method decides if a new customer has arrived at each time based on
     * the customer arrival rate.
     * @param minPerCust Number of minutes between two customer arrivals.
     * @return true if a new customer has arrived, otherwise false.
     *//*
    private static boolean newCustomer(float minPerCust) {
        return (Math.random() * minPerCust < 1);
    }
*/}