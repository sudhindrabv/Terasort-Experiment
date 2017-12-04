package com.iit.cs553.sharedmem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileNameExtensionFilter;

import com.sun.management.OperatingSystemMXBean;

public class Utility {
	static final double TWOGB = 4294967296.0f;
	static final double ONEGB = 1073741824.0f;

	public static double getRamSIze() {
		double memorySize = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean())
				.getTotalPhysicalMemorySize();
		//double finalSize = memorySize - TWOGB;
		double finalSize = memorySize;
		return finalSize;
	}

	public static double getCeilValue(double fileSizePerThreadInBytes) {
		return Math.ceil(fileSizePerThreadInBytes);
	}

	public static void writeRecords(List<Record> recordList, long threadId, long counter) {

		String fileName = String.format("%d_%d_out.tmp", threadId, counter);
		try {
			File file = new File(fileName);
			PrintWriter pw = new PrintWriter(new FileOutputStream(file));
			FileOutputStream fo = new FileOutputStream(file);
			int datList = recordList.size();

			for (int i = 0; i < datList; i++) {
				pw.write(recordList.get(i).toString() + "\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String [] getIntermediateFilesInLocalDirectory(){
		File directory = new File(".");
		FilenameFilter filter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".tmp");
			}
		};
		String []fileNames = directory.list(filter);
		return fileNames;
	}
}
