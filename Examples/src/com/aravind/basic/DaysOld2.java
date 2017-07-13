package com.aravind.basic;

import java.util.Calendar;

import com.javaranch.common.GDate;
import org.joda.time.Days;
import org.joda.time.DateTime;


public class DaysOld2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GDate gd = new GDate(args[0]);
		GDate td = new GDate();

		Calendar c=Calendar.getInstance();
		c.set(gd.getYear(), gd.getMonth(), gd.getDay());
		Calendar c1=Calendar.getInstance();
		c1.set(td.getYear(), td.getMonth(), td.getDay());
		
		DateTime dt1 = new DateTime(c);
		DateTime dt2 = new DateTime(c1);
		
		System.out.println((c1.getTimeInMillis()-(c.getTimeInMillis()))/(24*60*60*1000));
		
		System.out.print(Days.daysBetween(dt1, dt2).getDays() + " days, ");
		

	}

}
