package com.alterego.androidbound.zzzztoremove;

public class Tuple<T, T1> {
	private T item1;
	private T1 item2;
	
	public Tuple(T item1, T1 item2) {
		this.item1 = item1;
		this.item2 = item2;
	}
	
	public T getItem1() { return item1; }
	public T1 getItem2() { return item2; }
	
	public String toString() {
		return item1.toString() + ", " + item2.toString();
	}
}
