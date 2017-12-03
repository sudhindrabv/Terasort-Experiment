package com.iit.cs553.sharedmem;

import java.util.List;

public class Chunk {
	List<Record> chunk;

	public List<Record> getChunk() {
		return chunk;
	}

	public void setChunk(List<Record> chunk) {
		this.chunk = chunk;
	}

	public void readRecord(int index) {
		// return record
	}

	public void writeRecords() {
		// write records to file
	}
}
