package com.aravind.basic;



public class PrintSum {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double sum=0.0;
		
		
		for(int i=0;i<1000;i++){
			sum= sum +0.1;
			System.out.println("sum is "+sum);
			if(sum==100.0){
				System.out.println(" hurray sum is 100");
			}
		}
		

	}

}
