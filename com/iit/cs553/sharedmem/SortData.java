package com.iit.cs553.sharedmem;

import java.awt.image.BufferedImageFilter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SortData {

	private FileReader rawData;
	private BufferedReader br;
	private long threadId;
	private double startChunckPerThread;
	private double endChunckPerThread;
	private static double chunkSizeByRecordsInRAMPerThread;
	

	private static double chunkSizeInRAMPerThreadInBytes;
	private static double numChunksPerThreadInBytes;
	private static String fileName;
	private static double noOfRecordsPerChunk;

	
	public SortData(long threadId) {
		setThreadId(threadId);
		setNoOfRecordsPerChunk(0);
		setRawData(getRawData(fileName));
		setStartChunckPerThread(threadId);
		setEndChunckPerThread(threadId);
		Double doubleVal = new Double(startChunckPerThread);
		try {
			if (br == null) {
				br = new BufferedReader(rawData);
			}
			br.skip(doubleVal.longValue());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private FileReader getRawData(String fileName) {
		FileReader fr = null;
		try {
			fr = new FileReader(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fr;
	}
	
	public static double getChunkSizeByRecordsInRAMPerThread() {
		return chunkSizeByRecordsInRAMPerThread;
	}

	public static void setChunkSizeByRecordsInRAMPerThread(double chunkSizeByRecordsInRAMPerThread) {
		SortData.chunkSizeByRecordsInRAMPerThread = chunkSizeByRecordsInRAMPerThread;
	}
	
	public static double getNoOfRecordsPerChunk() {
		return noOfRecordsPerChunk;
	}

	public static void setNoOfRecordsPerChunk(double noOfRecordsPerChunk) {
		SortData.noOfRecordsPerChunk = noOfRecordsPerChunk;
	}
	
	private void setRawData(FileReader rawData) {
		this.rawData = rawData;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public double getStartChunckPerThread() {
		return startChunckPerThread;
	}

	public void setStartChunckPerThread(long threadId) {
		this.startChunckPerThread = threadId * numChunksPerThreadInBytes * chunkSizeInRAMPerThreadInBytes;
	}

	public static double getNumChunksPerThreadInBytes() {
		return numChunksPerThreadInBytes;
	}

	public double getEndChunckPerThread() {
		return endChunckPerThread;
	}

	public void setEndChunckPerThread(long threadId) {
		this.endChunckPerThread = (threadId + 1) * numChunksPerThreadInBytes * chunkSizeInRAMPerThreadInBytes;
	}

	public List<Record> getNextRawChunk() {
		// all the chunks are read for thread
		if (startChunckPerThread >= endChunckPerThread) {
			try {
				br.close();
				rawData.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		}
		double numberOfRecords=0;
		List<Record> chunck = new ArrayList<Record>();
		try {
			double numOfLinesToRead = Utility.getCeilValue(chunkSizeInRAMPerThreadInBytes / 100);
			while (numOfLinesToRead > 0) {
				String line = br.readLine();
				if (line == null)
					return chunck;
				Record record = new Record();
				record.setFirstTen(line.substring(0, 10));
				record.setNextNinety(line.substring(11));
				chunck.add(record);
				numberOfRecords++;
				numOfLinesToRead--;
			}
			if(getNoOfRecordsPerChunk() < numberOfRecords){
				setNoOfRecordsPerChunk(numberOfRecords);
			}
				

			startChunckPerThread = startChunckPerThread + chunkSizeInRAMPerThreadInBytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return chunck;
	}

	public static void initData(int numOfThreads, String fileName1, int fileSizeInGb) {
		double availableRAMSize =  Utility.getRamSIze();//536870912;
		System.out.println("Ram Size is "+availableRAMSize);
		chunkSizeInRAMPerThreadInBytes = availableRAMSize / numOfThreads;
		chunkSizeInRAMPerThreadInBytes = Utility.getCeilValue(chunkSizeInRAMPerThreadInBytes);
		System.out.println("/n chunkSizeInRAMPerThreadInBytes"+chunkSizeInRAMPerThreadInBytes);
		System.out.println("/nFile size GB "+fileSizeInGb);		
		double fileSizeInBytes = fileSizeInGb * 1024 * 1024 * 1024;
		System.out.println("/nFile size in bytes "+fileSizeInBytes);
		/*chunkSizeInRAMPerThreadInBytes = (chunkSizeInRAMPerThreadInBytes < fileSizeInBytes)
				? chunkSizeInRAMPerThreadInBytes : fileSizeInBytes;*/
		
		System.out.println("/n chunkSizeInRAMPerThreadInBytes"+chunkSizeInRAMPerThreadInBytes);
		double fileSizePerThreadInBytes = fileSizeInBytes / numOfThreads;
		fileSizePerThreadInBytes = Utility.getCeilValue(fileSizePerThreadInBytes);

		numChunksPerThreadInBytes = fileSizePerThreadInBytes / chunkSizeInRAMPerThreadInBytes;
		System.out.println("Number of chuncks per thread "+numChunksPerThreadInBytes);
		// numChunksPerThreadInBytes =
		// Utility.getCeilValue(numChunksPerThreadInBytes);

		fileName = fileName1;
		double numRecordsOnRam = chunkSizeInRAMPerThreadInBytes / 100;
		chunkSizeByRecordsInRAMPerThread = numRecordsOnRam / numOfThreads;
		// fileName = fileName1;

		// double totalRecordsInFile = fileSizeInBytes/100;
		// double totalChuncksInHdd =
		// totalRecordsInFile/chunkSizeInRAMPerThread;
	}



}
