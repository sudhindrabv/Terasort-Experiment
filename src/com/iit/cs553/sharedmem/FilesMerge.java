package com.iit.cs553.sharedmem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FilesMerge implements Runnable {
	private static long chunkSizeByRecords;
	private static long numOfThreads;
	private static int numberOfFiles;
	public static int phase = 0;

	private static int filesPerThread;
	private static int noRecordsPerThread;
	private static String[] fileNames;

	private List<String> filesToSort;
	private int threadId;
	//private int startIndex;
	//private int endIndex;
	private List<Record> recordsToSort1;
	private List<Record> recordsToSort2;

	public static long noOfTimesWritten = 0 ;
	public static long noOfTimesRead = 0;
	public FilesMerge(int threadId) {
		this.threadId = threadId;
		findFilesForThreadToHandle(threadId);

	}

	public static int getNumberOfFiles() {
		return numberOfFiles;
	}

	private void findFilesForThreadToHandle(int threadId) {

		int startIndex = threadId * filesPerThread;
		int endIndex = startIndex + filesPerThread;
		filesToSort = new ArrayList<String>();
		for (; startIndex < endIndex; startIndex++) {
			filesToSort.add(fileNames[startIndex]);
		}
	}

	public static void initData(int numOfThreads, int chunkSizeByRecords) {
		FilesMerge.numOfThreads = numOfThreads;
		FilesMerge.chunkSizeByRecords = chunkSizeByRecords;
		fileNames = Utility.getIntermediateFilesInLocalDirectory();
		numberOfFiles = fileNames.length;

		noRecordsPerThread = chunkSizeByRecords / numOfThreads;
		filesPerThread = numberOfFiles / numOfThreads;
	}

	private boolean loadRecordsOfFile(BufferedReader br1, BufferedReader br2) {
		boolean isFileReadComplete = false;
		int recordsToLoadfromEachFile = noRecordsPerThread / 2;
		recordsToSort1 = new ArrayList<Record>();
		recordsToSort2 = new ArrayList<Record>();
		String line1 = null;
		String line2 = null;
		for (int i = 0; i < recordsToLoadfromEachFile; i++) {
			try {
				line1 = br1.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (line1 == null) {
				isFileReadComplete = true;
				break;
			}
			Record record = new Record();
			record.setFirstTen(line1.substring(0, 10));
			record.setNextNinety(line1.substring(11));
			recordsToSort1.add(record);
		}
		for (int i = 0; i < recordsToLoadfromEachFile; i++) {
			try {
				line2 = br2.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (line2 == null) {
				isFileReadComplete = true;
				break;
			}
			Record record = new Record();
			record.setFirstTen(line2.substring(0, 10));
			record.setNextNinety(line2.substring(11));
			recordsToSort2.add(record);

		}
		if (line1 == null) {
			try {
				while ((line2 = br2.readLine()) != null) {
					Record record = new Record();
					record.setFirstTen(line2.substring(0, 10));
					record.setNextNinety(line2.substring(11));
					recordsToSort2.add(record);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (line2 == null) {
			try {
				while ((line1 = br1.readLine()) != null) {
					Record record = new Record();
					record.setFirstTen(line1.substring(0, 10));
					record.setNextNinety(line1.substring(11));
					recordsToSort1.add(record);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return isFileReadComplete;
	}

	private void mergeFiles() {
		FileReader currentFile1 = null;
		FileReader currentFile2 = null;
		BufferedReader br1 = null;
		BufferedReader br2 = null;
		File sortedFile = null;
		PrintWriter pw = null;
		int delta = filesToSort.size() % 2;

		for (int i = 0; i < filesToSort.size() - delta - 1; i = i + 2) {
			try {	noOfTimesRead++;
				String fileName = String.format("%d_%d_%d_out.tmp", threadId, i, phase);
				sortedFile = new File(fileName);
				pw = new PrintWriter(new FileOutputStream(sortedFile));
				System.out.println("Merging files " + filesToSort.get(i) + " and " + filesToSort.get(i + 1));
				currentFile1 = new FileReader(filesToSort.get(i));
				currentFile2 = new FileReader(filesToSort.get(i + 1));
				br1 = new BufferedReader(currentFile1);
				br2 = new BufferedReader(currentFile2);
			} catch (IOException e) {
				e.printStackTrace();
			}
			boolean fileMergeComplete = false;
			while (fileMergeComplete != true) {
				fileMergeComplete = loadRecordsOfFile(br1, br2);
				MergeRecordLists(pw);
			}
			try {
				br1.close();
				br2.close();
				currentFile1.close();
				currentFile2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			pw.close();
			File todelete1 = new File(filesToSort.get(i));
			todelete1.delete();
			File todelete2 = new File(filesToSort.get(i + 1));
			todelete2.delete();
		}
		phase++;
	}

	private void MergeRecordLists(PrintWriter pw) {
		noOfTimesWritten++;
		int i, j;
		i = 0;
		j = 0;
		int n1 = recordsToSort1.size() - 1;
		int n2 = recordsToSort2.size() - 1;
		/*
		 * Until we reach either end of either L or M, pick larger among
		 * elements L and M and place them in the correct position at A[p..r]
		 */
		while (i < n1 && j < n2) {
			if (recordsToSort1.get(i).getFirstTen().compareTo(recordsToSort2.get(j).getFirstTen()) <= 0) {
				pw.write(recordsToSort1.get(i).toString() + "\n");
				i++;
			} else {
				pw.write(recordsToSort2.get(j).toString() + "\n");
				j++;
			}
		}

		/*
		 * When we run out of elements in either L or M, pick up the remaining
		 * elements and put in A[p..r]
		 */
		while (i < n1) {
			pw.write(recordsToSort1.get(i).toString() + "\n");
			i++;
		}

		while (j < n2) {
			pw.write(recordsToSort2.get(j).toString() + "\n");
			j++;
		}
	}

	@Override
	public void run() {
		System.out.println("Merging Thread " + threadId + " Started");
		mergeFiles();
		System.out.println("Merging Thread " + threadId + " Completed");

	}
}
