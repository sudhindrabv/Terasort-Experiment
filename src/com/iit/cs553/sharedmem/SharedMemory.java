package com.iit.cs553.sharedmem;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SharedMemory {

	//fileSizeInBytes 124GB = 133143986200 1 TB = 1099511627800 1GB = 1073741800 30GB = 32212254700 8GB = 8589934500 128GB = 137438953400
	public static void main(String[] args) {
		long start  = System.currentTimeMillis();
		long end = 0;
		long MapPhaseFiles = 0;
		int numOfThreads = 4;
		double fileSizeInBytes = 137438953400.0;
		SortData.initData(numOfThreads, "out", fileSizeInBytes);
		ExecutorService es = Executors.newFixedThreadPool(numOfThreads);

		for (int i = 0; i < numOfThreads; i++) {
			Runnable worker = new SortThreadExecutor(i);
			es.execute(worker);
		}
		es.shutdown();
		while (!es.isTerminated()) {

		}

		int numberOfFiles = Utility.getIntermediateFilesInLocalDirectory().length;
		MapPhaseFiles = numberOfFiles;
		numOfThreads = numberOfFiles/2;
		while (numberOfFiles > 1) {
			
			double chunkSizeByRecord = SortData.getChunkSizeByRecordsInRAMPerThread();
			// mergeFiles();
			Double db = new Double(chunkSizeByRecord);
			try{
			FilesMerge.initData(numOfThreads, db.intValue());
			}catch (Exception e){
				break;
			}
			es = Executors.newFixedThreadPool(numOfThreads);

			for (int i = 0; i < numOfThreads; i++) {
				Runnable worker = new FilesMerge(i);
				es.execute(worker);
			}
			es.shutdown();
			while (!es.isTerminated()) {

			}
//			if(numberOfFiles == Utility.getIntermediateFilesInLocalDirectory().length)
//			numOfThreads--;
			if( Utility.getIntermediateFilesInLocalDirectory().length == 2)
			{
				numOfThreads=1;
			}else
			{
				numOfThreads = numOfThreads/2;	
			}
			
		}
	end  = System.currentTimeMillis();
	long timeTaken = end - start ;
	long minutes = TimeUnit.MILLISECONDS.toMinutes(timeTaken);
	
	System.out.println("Time taken in minutes : "+minutes);
	System.out.println("Initial Map Phase number of times read :"+SortData.noOfTimesRead+"written :"+SortThreadExecutor.noOfTimesWritten);
	System.out.println("Number of intermediate files created during map phase :"+MapPhaseFiles);
	System.out.println("Available Ram size "+Utility.getRamSIze());
	System.out.println("File size under consideration in bytes :"+fileSizeInBytes);
	System.out.println("Number of times merge happened :"+FilesMerge.phase);
	System.out.println("Number of times read :"+FilesMerge.noOfTimesRead+"written :"+FilesMerge.noOfTimesWritten +" during merge phase");

	}
}

class MergeThreadExecutor implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}

class SortThreadExecutor implements Runnable {
	private int threadId;
	public static long noOfTimesWritten;

	public SortThreadExecutor(int i) {
		threadId = i;
	}

	public void mergeSort(List<Record> recordList, int lowIndex, int highIndex) {
		if (lowIndex == highIndex)
			return;
		else {
			int midIndex = (lowIndex + highIndex) / 2;
			mergeSort(recordList, lowIndex, midIndex);
			mergeSort(recordList, midIndex + 1, highIndex);
			merge(recordList, lowIndex, midIndex, highIndex);
		}
	}

	void merge(List<Record> recordList, int low, int mid, int high) {
		int n1 = mid - low + 1;
		int n2 = high - mid;

		List<Record> leftRecordList = new ArrayList<Record>(n1);
		List<Record> rightRecordList = new ArrayList<Record>(n2);

		for (int i = 0; i < n1; i++)
			// leftRecordList.set(i, recordList.get(low + i));
			leftRecordList.add(recordList.get(low + i));
		for (int j = 0; j < n2; j++)
			// rightRecordList.set(j, recordList.get(mid + 1 + j));
			rightRecordList.add(recordList.get(mid + 1 + j));

		/* Maintain current index of sub-arrays and main array */
		int i, j, k;
		i = 0;
		j = 0;
		k = low;

		/*
		 * Until we reach either end of either L or M, pick larger among
		 * elements L and M and place them in the correct position at A[p..r]
		 */
		while (i < n1 && j < n2) {
			if (leftRecordList.get(i).getFirstTen().compareTo(rightRecordList.get(j).getFirstTen()) <= 0) {
				recordList.set(k, leftRecordList.get(i));
				i++;
			} else {
				recordList.set(k, rightRecordList.get(j));
				j++;
			}
			k++;
		}

		/*
		 * When we run out of elements in either L or M, pick up the remaining
		 * elements and put in A[p..r]
		 */
		while (i < n1) {
			recordList.set(k, leftRecordList.get(i));
			i++;
			k++;
		}

		while (j < n2) {
			recordList.set(k, rightRecordList.get(j));
			j++;
			k++;
		}
	}

	@Override
	public void run() {
		System.out.println("Thread " + threadId + " started");
		SortData a = new SortData(threadId);
		List<Record> recordList = a.getNextRawChunk();
		if(recordList.size()== 0 )
		{
			System.out.println("Thread " + threadId + " completed without writing anything");
			return;
		}
		System.out.println("Record List size"+recordList.size());
		for (long counter = 0; counter < SortData.getNumChunksPerThread(); counter++) {
			mergeSort(recordList, 0, recordList.size() - 1); // sort chunck
			noOfTimesWritten++;
			Utility.writeRecords(recordList, threadId, counter);
			recordList = a.getNextRawChunk();
		}

		System.out.println("Thread " + threadId + " completed");
	}
}
