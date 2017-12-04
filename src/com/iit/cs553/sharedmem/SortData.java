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
//	private static double chunkSizeByRecordsInRAMPerThread;
	private static double chunksPerThread;
	public static long noOfTimesRead= 0;
	
	

//	private static double chunkSizeInRAMPerThreadInBytes;
//	private static double numChunksPerThreadInBytes;
	private static String fileName;
//	private static double noOfRecordsPerChunk;
	private static double chunkSize;

	
	public SortData(long threadId) {
		setThreadId(threadId);
		//setNoOfRecordsPerChunk(0);
		setRawData(getRawData(fileName));
		setStartChunckPerThread(threadId);
		setEndChunckPerThread(threadId);
		//moves the bufferedreader to thread specific start address
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
		return chunkSize;
	}
//
//	public static void setChunkSizeByRecordsInRAMPerThread(double chunkSizeByRecordsInRAMPerThread) {
//		SortData.chunkSizeByRecordsInRAMPerThread = chunkSizeByRecordsInRAMPerThread;
//	}
	
//	public static double getNoOfRecordsPerChunk() {
//		return noOfRecordsPerChunk;
//	}

//	public static void setNoOfRecordsPerChunk(double noOfRecordsPerChunk) {
//		SortData.noOfRecordsPerChunk = noOfRecordsPerChunk;
//	}
	
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
//		this.startChunckPerThread = threadId * numChunksPerThreadInBytes * chunkSizeInRAMPerThreadInBytes;
		this.startChunckPerThread = threadId * chunksPerThread * chunkSize * 100;
	}

//	public static double getNumChunksPerThreadInBytes() {
//		return numChunksPerThreadInBytes;
//	}
	
	public static double getNumChunksPerThread() {
	return chunksPerThread;
}

	public double getEndChunckPerThread() {
		return endChunckPerThread;
	}

	public void setEndChunckPerThread(long threadId) {
		//this.endChunckPerThread = (threadId + 1) * numChunksPerThreadInBytes * chunkSizeInRAMPerThreadInBytes;
		this.endChunckPerThread = (threadId + 1) * chunksPerThread * chunkSize * 100;
	}

	public List<Record> getNextRawChunk() {
		noOfTimesRead++;
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
//		double numberOfRecords=0;
		List<Record> chunck = new ArrayList<Record>();
		try {
			double numOfLinesToRead = chunkSize;//chunk size in recordsUtility.getCeilValue(chunkSizeInRAMPerThreadInBytes / 100);
			while (numOfLinesToRead > 0) {
				String line = br.readLine();
				if (line == null)
					return chunck;
				Record record = new Record();
				record.setFirstTen(line.substring(0, 10));
				record.setNextNinety(line.substring(11));
				chunck.add(record);
//				numberOfRecords++;
				numOfLinesToRead--;
			}
//			if(getNoOfRecordsPerChunk() < numberOfRecords){
//				setNoOfRecordsPerChunk(numberOfRecords);
//			}
				

//			startChunckPerThread = startChunckPerThread + chunkSizeInRAMPerThreadInBytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return chunck;
	}

	public static void initData(int numOfThreads, String fileName1, double fileSizeInBytes) {
		//double fileSizeInBytes = fileSizeInGb * 1024 * 1024 * 1024;
		double fileSizeInRecords = Math.ceil(fileSizeInBytes /100);//in the most unlikely case of a decimal take larger value
		System.out.println("/nFile size in bytes "+fileSizeInBytes);
		System.out.println("/nFile size in 100 byte records "+fileSizeInRecords);
		
		double availableRAMSizeInBytes =  Utility.getRamSIze();//536870912; in bytes
		System.out.println("Ram Size in bytes "+availableRAMSizeInBytes);
		double availableRAMSizeInRecords = Math.floor(availableRAMSizeInBytes /100);
		System.out.println("/n availableRAMSizeInRecords "+availableRAMSizeInRecords);
		
//		TODO: need to handle the case where number of ramfits < 1
//		while # ram fits < 1
//		reduce ram size by 500MB
//		compute # ram fits
		double numberOfRamFits = fileSizeInRecords/availableRAMSizeInRecords;
		while(numberOfRamFits<1){
			availableRAMSizeInBytes = availableRAMSizeInBytes - (Utility.TWOGB/4);
			availableRAMSizeInRecords = Math.floor(availableRAMSizeInBytes /100);
			numberOfRamFits = fileSizeInRecords/availableRAMSizeInRecords;
		}
		numberOfRamFits = Math.ceil(numberOfRamFits);
		System.out.println("/n numberOfRamFits "+numberOfRamFits);
		
		chunkSize = Math.ceil(availableRAMSizeInRecords / numOfThreads);
		System.out.println("/n chunkSize "+chunkSize);
		
		double numOfChunksAvailable = Math.ceil(fileSizeInRecords/chunkSize);
		System.out.println("/n numOfChunksAvailable "+numOfChunksAvailable);
		
		chunksPerThread = Math.ceil(numOfChunksAvailable/numOfThreads);
		System.out.println("/n chunksPerThread "+chunksPerThread);
		
		
//		chunkSizeInRAMPerThreadInBytes = availableRAMSize / numOfThreads;
//		chunkSizeInRAMPerThreadInBytes = Utility.getCeilValue(chunkSizeInRAMPerThreadInBytes);
//		System.out.println("/n chunkSizeInRAMPerThreadInBytes"+chunkSizeInRAMPerThreadInBytes);
//		System.out.println("/nFile size GB "+fileSizeInGb);		
		
		/*chunkSizeInRAMPerThreadInBytes = (chunkSizeInRAMPerThreadInBytes < fileSizeInBytes)
				? chunkSizeInRAMPerThreadInBytes : fileSizeInBytes;*/
		
		//System.out.println("/n chunkSizeInRAMPerThreadInBytes"+chunkSizeInRAMPerThreadInBytes);
//		double fileSizePerThreadInBytes = fileSizeInBytes / numOfThreads;
//		fileSizePerThreadInBytes = Utility.getCeilValue(fileSizePerThreadInBytes);

//		numChunksPerThreadInBytes = fileSizePerThreadInBytes / chunkSizeInRAMPerThreadInBytes;
//		System.out.println("Number of chuncks per thread "+numChunksPerThreadInBytes);
		// numChunksPerThreadInBytes =
		// Utility.getCeilValue(numChunksPerThreadInBytes);

		fileName = fileName1;
//		double numRecordsOnRam = chunkSizeInRAMPerThreadInBytes / 100;
//		chunkSizeByRecordsInRAMPerThread = numRecordsOnRam / numOfThreads;
		// fileName = fileName1;

		// double totalRecordsInFile = fileSizeInBytes/100;
		// double totalChuncksInHdd =
		// totalRecordsInFile/chunkSizeInRAMPerThread;
	}



}
