package com.iit.cs553.sharedmem;

public class Record {
	String firstTen;
	String nextNinety;

	public void Record(String hundredByteRecord) {
		splitRecord(hundredByteRecord);
	}

	public String getFirstTen() {
		return firstTen;
	}

	public void setFirstTen(String firstTen) {
		this.firstTen = firstTen;
	}

	public String getNextNinety() {
		return nextNinety;
	}

	public void setNextNinety(String nextNinety) {
		this.nextNinety = nextNinety;
	}

	public void splitRecord(String record) {
		String tempFirstTen = "";
		String tempNextTen = "";
		// split logic

		this.setFirstTen(tempFirstTen);
		this.setNextNinety(tempNextTen);
	}
	
	@Override
	public String toString() {
		return getFirstTen()+getNextNinety();
	}
}
