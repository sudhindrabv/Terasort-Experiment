package com.iit.cs553.sharedmem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SharedMemory {

	public static void main(String[] args) {
		int numOfThreads = 4;

		SortData.initData(numOfThreads, "out", 30);
		ExecutorService es = Executors.newFixedThreadPool(numOfThreads);

		for (int i = 0; i < numOfThreads; i++) {
			Runnable worker = new SortThreadExecutor(i);
			es.execute(worker);
		}
		es.shutdown();
		while (!es.isTerminated()) {

		}

		int numberOfFiles = Utility.getIntermediateFilesInLocalDirectory().length;
		while (numberOfFiles > 1) {
			// numOfThreads = numberOfFiles/2;
			double chunkSizeByRecord = SortData.getChunkSizeByRecordsInRAMPerThread();
			// mergeFiles();
			Double db = new Double(chunkSizeByRecord);
			FilesMerge.initData(numOfThreads, db.intValue());
			es = Executors.newFixedThreadPool(numOfThreads);

			for (int i = 0; i < numOfThreads; i++) {
				Runnable worker = new FilesMerge(i);
				es.execute(worker);
			}
			es.shutdown();
			while (!es.isTerminated()) {

			}
			numberOfFiles = Utility.getIntermediateFilesInLocalDirectory().length;
		}

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
		System.out.println("Record List size"+recordList.size());
		for (long counter = 0; counter < SortData.getNumChunksPerThreadInBytes(); counter++) {
			mergeSort(recordList, 0, recordList.size() - 1); // sort chunck
			Utility.writeRecords(recordList, threadId, counter);
			recordList = a.getNextRawChunk();
		}

		System.out.println("Thread " + threadId + " completed");
	}
}
