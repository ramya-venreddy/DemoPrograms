package com.hibernate.chap1;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="EmployeeInfo")
public class Employee {
	

	@Id
	@TableGenerator(name="empid",table="emppktb",pkColumnName="empkey",pkColumnValue="empvalue",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.TABLE,generator="empid")
	@Column(name="EmployeeId")
	private int empId;
	private String empName;
	
	@Transient
	private String empPassword;
	
	@Column(nullable=false)
	private String empEmailAddress;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date empLoginTime;
	
	@Temporal(TemporalType.DATE)
	private Calendar empJoinDate;
	
	@Basic
	private boolean isPermanent;
	
	public String getEmpPassword() {
		return empPassword;
	}
	public void setEmpPassword(String empPassword) {
		this.empPassword = empPassword;
	}
	public String getEmpEmailAddress() {
		return empEmailAddress;
	}
	public void setEmpEmailAddress(String empEmailAddress) {
		this.empEmailAddress = empEmailAddress;
	}
	public Date getEmpLoginTime() {
		return empLoginTime;
	}
	public void setEmpLoginTime(Date empLoginTime) {
		this.empLoginTime = empLoginTime;
	}
	public Calendar getEmpJoinDate() {
		return empJoinDate;
	}
	public void setEmpJoinDate(Calendar empJoinDate) {
		this.empJoinDate = empJoinDate;
	}
	public boolean isPermanent() {
		return isPermanent;
	}
	public void setPermanent(boolean isPermanent) {
		this.isPermanent = isPermanent;
	}

	
	
	public int getEmpId() {
		return empId;
	}
	public void setEmpId(int empId) {
		this.empId = empId;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	
}
