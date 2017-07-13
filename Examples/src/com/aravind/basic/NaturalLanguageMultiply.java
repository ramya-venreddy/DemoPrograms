package com.aravind.basic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NaturalLanguageMultiply {

	private static Map<Integer, String> hmap;
	static {
		hmap = new HashMap<Integer, String>(28);
		hmap.put(1, "one");
		hmap.put(2, "two");
		hmap.put(3, "three");
		hmap.put(4, "four");
		hmap.put(5, "five");
		hmap.put(6, "six");
		hmap.put(7, "seven");
		hmap.put(8, "eight");
		hmap.put(9, "nine");
		hmap.put(10, "ten");

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int array[] = new int[args.length];
		int result = 1;
		try {
			for (int i = 0; i < args.length; i++) {
				array[i] = toInt(args[i]);
				result = result * array[i];

			}
		} catch (Exception e) {
			System.out.println("Unknown Element."
					+ " Please enter correct number from 1 to 10");
		}

		System.out.println(result);

	}

	private static int toInt(String s) throws Exception {
		int i = 0;

		Iterator iter = hmap.entrySet().iterator();
		 
		while (iter.hasNext()) {
			Map.Entry mEntry = (Map.Entry) iter.next();
			if(s.equals(mEntry.getValue())){
				i++;
				return (Integer)mEntry.getKey();
				
			}
		}
		if(i<2){
			throw new Exception();
		}

		return i;
	}

}
