package com.bank.BankQueue;

public class Customer
{
	int clock, processedTime;
	public Customer()
	{}
	public Customer(int theTime)
	{
		clock=theTime;
	}

	public int getCurrent(int current)
	{
		 processedTime=clock-current;
		 return processedTime;
	}
}