package com.aravind.basic;

public class FindLeap {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length > 0) {
			int n = Integer.parseInt(args[0]);
			if (n % 400 == 0) {
				System.out.println("Not a leap year");
			} else if (n % 100 == 0)
				System.out.println("Not a leap year");
			else if (n % 4 == 0)
				System.out.println("Leap year");
			else
				System.out.println("Not a leap year");
		}

	}

}
