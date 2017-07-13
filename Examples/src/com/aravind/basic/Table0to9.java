package com.aravind.basic;

public class Table0to9 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (int i = 0; i <= 9; i++) {
			System.out.print(i + " ");
		}
		for (int j = 0; j <= 9; j++) {
			System.out.println();
			System.out.print(j + " ");
			for (int k = 0; k <= 9; k++) {
				System.out.print(k * j + " ");

			}
		}

	}
}
