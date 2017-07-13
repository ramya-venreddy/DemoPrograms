package com.aravind.basic;

public class EvenOrOdd {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length>0){
			int n=Integer.parseInt(args[0]);
			
			if(n%2==0){
				System.out.println("It is an even number");
				
			}else{
				System.out.println("It is an odd number");
				
			}
		}

	}

}
