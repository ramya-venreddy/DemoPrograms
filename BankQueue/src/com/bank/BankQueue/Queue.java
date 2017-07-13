package com.bank.BankQueue;

public class Queue<T> {
	private Node<T> first;
	private Node<T> last;
	T newOne;
	private int count;
	public Queue () {
	   last = new Node<T>();
	   first = new Node<T>(null,last);
	   count=0;
	}

	public String toString() {
		String ret = "";
		Node<T> r = first.getNext();
	    for (; r!=last; r=r.getNext())
		    ret += r.getData() + " ";
		return "first " + ret + "last";
	}

	public T dequeue() {
		T ret;
		if (count==0) return null;
		ret = first.getNext().getData();
		first = first.getNext();
		count--;
		return ret;
	}

	public void enqueue(T newData) {
		last.setData(newData);
		last.setNext(new Node<T>());
		last = last.getNext();
		count++;
	}
	public boolean empty() { return count==0; }

	public int length() { return count; }

}

