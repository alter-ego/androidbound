package com.alterego.androidbound.zzzztoremove;

import java.util.ArrayList;

public class GroupedList<TKey, T> extends ArrayList<T> {
	private static final long serialVersionUID = -6232127810394489391L;
	
	public TKey Key;
	public GroupedList(TKey key) {
		super();
		this.Key = key;
	}
}
