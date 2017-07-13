package com.aravind.findAvgMed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
 
public class InputConsole {
  public static void main(String[] args) {
 
	System.out.println("Enter something here : ");
	boolean input=false;
 
	while(input==false){
	   
	    String s;
		try {
			
			while(input==false)
			{
				BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
				s = bufferRead.readLine();
				if(s.length()==5)
				{
					Integer i=Integer.parseInt(s);
					for(int j=0;j<s.length();j++){
						if((i%10)!=0){
							input=true;
						}
						else{
							input=false;
						}
					}
					if(input==true)
					System.out.println("You have entered a valid 5 digit number "+i);
					else
					System.out.println("Please enter a valid 5 digit number");
				}
				else{
					System.out.println("Please enter a valid 5 digit number");
		    	
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	}
	
  }
}