package com.hibernate.chap1;

import java.sql.Date;
import java.util.GregorianCalendar;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class TestEmployee {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AnnotationConfiguration config=new AnnotationConfiguration();
		config.addAnnotatedClass(Employee.class);
		config.configure("hibernate.cfg.xml");
		
		new SchemaExport(config).create(true, true);

		SessionFactory factory=config.buildSessionFactory();
		Session session=factory.getCurrentSession();
		
		session.beginTransaction();
		
		
		{
		Employee alex=new Employee();
		//alex.setEmpId(100);
		alex.setEmpName("alex berry");
		alex.setEmpEmailAddress("aravind@siu.edu");
		alex.setEmpPassword("alexpass");
		alex.setPermanent(true);
		alex.setEmpJoinDate(new GregorianCalendar(2012,11,25));
		alex.setEmpLoginTime(Date.valueOf("2012-11-25"));
		
		session.save(alex);
		}

		{
		Employee alex=new Employee();
	
		alex.setEmpName("Sachin Jain");
		alex.setEmpEmailAddress("sachin@gmail.com");
		alex.setEmpPassword("sachinpass");
		alex.setPermanent(true);
		alex.setEmpJoinDate(new GregorianCalendar(2012,11,25));
		alex.setEmpLoginTime(Date.valueOf("2012-11-25"));
		
		session.save(alex);
		}

		{
		Employee alex=new Employee();
		//alex.setEmpId(100);
		alex.setEmpName("Rama Krishna");
		alex.setEmpEmailAddress("aarkay@gmail.com");
		alex.setEmpPassword("aarkaypass");
		alex.setPermanent(true);
		alex.setEmpJoinDate(new GregorianCalendar(2012,11,25));
		alex.setEmpLoginTime(Date.valueOf("2012-11-25"));
		
		session.save(alex);
		}

		{
		Employee alex=new Employee();
		//alex.setEmpId(100);
		alex.setEmpName("Saradindu dolui");
		alex.setEmpEmailAddress("sadi@gmail.com");
		alex.setEmpPassword("sadipass");
		alex.setPermanent(true);
		alex.setEmpJoinDate(new GregorianCalendar(2012,11,25));
		alex.setEmpLoginTime(Date.valueOf("2012-11-25"));
		
		session.save(alex);
		}
		

		{
		Employee alex=new Employee();
		//alex.setEmpId(100);
		alex.setEmpName("Aravind Sama");
		alex.setEmpEmailAddress("aravind@gmail.com");
		alex.setEmpPassword("aravindpass");
		alex.setPermanent(true);
		alex.setEmpJoinDate(new GregorianCalendar(2012,11,25));
		alex.setEmpLoginTime(Date.valueOf("2012-11-25"));
		
		session.save(alex);
		}
		
		
		session.getTransaction().commit();

	}

}
