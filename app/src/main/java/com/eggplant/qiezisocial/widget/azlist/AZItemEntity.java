package com.eggplant.qiezisocial.widget.azlist;


public class AZItemEntity<T> {

	private T      mValue;
	private String mSortLetters;

	public T getValue() {
		return mValue;
	}

	public void setValue(T value) {
		mValue = value;
	}

	public String getSortLetters() {
		return mSortLetters;
	}

	public void setSortLetters(String sortLetters) {
		mSortLetters = sortLetters;
	}

	@Override
	public boolean equals(Object obj) {
		return mValue.equals(((AZItemEntity)obj).mValue);
	}
}
