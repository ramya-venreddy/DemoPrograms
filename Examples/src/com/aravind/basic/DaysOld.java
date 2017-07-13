package com.aravind.basic;

import com.javaranch.common.*;

public class DaysOld {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if (args.length > 0) {

			int sum = 0;
			GDate gd = new GDate(args[0]);
			int year = gd.getYear();
			int month = gd.getMonth();
			int day = gd.getDay();

			GDate td = new GDate();
			int year1 = td.getYear();
			int month1 = td.getMonth();
			int day1 = td.getDay();
			int temp = year;
			int temp2 = month;
			if (temp2 < month1) {
				if (temp < year1) {
					if (temp % 400 == 0) {
						// this is definitely a leap year
						sum = sum + 365;
					} else if (year % 4 == 0) {
						// this is probably a leap year
						if (year % 100 == 0) {
							// this is definitely not a leap year
							// do nothing
							sum = sum + 365;
						} else {
							// this is definitely a leap year
							sum = sum + 366;
						}
					}

					temp++;
				}
				for(int k=temp2;k<month1;k++){
					if(k==1){
						sum=sum+31;
					}
					if(k==2){
						sum=sum+28;
					}
				}
				
			}else if(temp2>month1){
				if (temp < (year1-1)) {
					if (temp % 400 == 0) {
						// this is definitely a leap year
						sum = sum + 365;
					} else if (year % 4 == 0) {
						// this is probably a leap year
						if (year % 100 == 0) {
							// this is definitely not a leap year
							// do nothing
							sum = sum + 365;
						} else {
							// this is definitely a leap year
							sum = sum + 366;
						}
					}

					temp++;
				}
			}
		}

	}

}
