package com.aravind.basic;

import java.util.Date;

public class GeekWatch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Date d = new Date();
		int i = Integer.parseInt(args[0]);
		switch (i) {
		case 0:
			System.out.println(d.getTime());
			break;
		case 1:
			System.out.println((d.getTime() / 1000));
			break;
		case 2:
			System.out.println(d.getTime() / (24 * 60 * 60 * 1000));
			break;
		case 3:
			System.out.println(d.toString());
			break;

		}

	}

}
