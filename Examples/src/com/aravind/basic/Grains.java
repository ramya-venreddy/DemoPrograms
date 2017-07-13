package com.aravind.basic;

import java.math.BigDecimal;
import java.math.BigInteger;


public class Grains {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long grains=1;
		for (int i = 1; i <= 64; i++) {
			if(i==64){
				BigDecimal bdgrains=new BigDecimal(grains*2);
				
				System.out.println("square " + i + " : " + bdgrains + " grain");
			}
			if (i > 1 && i < 64) {
				grains = grains * 2;
			}
			if (i > 1 && i < 64) {
				System.out.println("square " + i + " : " + grains + " grain");
			}
		}

	}

}
