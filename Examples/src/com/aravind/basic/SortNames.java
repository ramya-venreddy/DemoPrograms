package com.aravind.basic;

import java.io.*;
import java.util.*;

public class SortNames {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader("/Users/Aravind/Desktop/testing.txt"));
			String sCurrentLine;
			List nameList=new ArrayList();
			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
				nameList.add(sCurrentLine);
				
			}
			System.out.println("Displaying the list in order");
			Collections.sort(nameList);
			Iterator it=nameList.iterator();
			while(it.hasNext()){
				System.out.println(it.next());
			}
			
 
		} catch (IOException e) {
			e.printStackTrace();
		} 
 

	}

}
