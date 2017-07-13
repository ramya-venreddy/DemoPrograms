package com.bank.BankQueue;

public class Node<T> {
	private T data;
	private Node<T> next;

	public Node(T d, Node<T> n) { data=d; next=n; }
	public Node(T d) { data = d; next = null; }
	public Node() { data = null; next = null; }

	public T getData() {return data;}
	public Node<T> getNext() {return next;}

	public void setData(T d) { data = d; }
	public void setNext(Node<T> n) { next = n; }

	public String toString() { return "" + data;
	}
}