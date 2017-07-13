package com.hibernate.chap2;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import com.hibernate.chap1.Employee;

public class TestCustomer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		AnnotationConfiguration config=new AnnotationConfiguration();
		config.addAnnotatedClass(Customer.class);
		config.configure("hibernate.cfg.xml");
		
		new SchemaExport(config).create(true, true);
		
		SessionFactory factory=config.buildSessionFactory();
		Session session=factory.getCurrentSession();
		
		session.beginTransaction();
		
		
		Customer alex= new Customer();
		alex.setCreditScore(780);
		alex.setCustomerName("Aravind Red");
		alex.setCustomerAddress("9 abbey drive");
		alex.setRewardPoints(1000);
		
		session.save(alex);
		session.getTransaction().commit();
		

	}

}
