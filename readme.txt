This text documents guides the user on how do we run the programs on Hadoop and shared memory

with explanations of code, which file contains what, and the commands needed to compile and execute the different scenarios

SHARED MEMORY
a) Files Description
The source code under "src_shared_memory" directory contains 6 java files under src/com/iit/cs553/
-Chunck.java: 
 This file contains the the list of Record objects and this object represents one chunck of data, which is calulcate dbased on the ram size and threads.
-FileMerge.java:
 This file contains the logic required to merge the any two sorted files and create a bigger sorted file.
-Record.java:
 This file Represent one record from the input file.It is having bytes of data.
-SharedMemory.java
 This class is the driver class for executing shared memory.
-SortData.java
 This file is used to logically sort the data based on merge sort algorithm for each chunck in each thread.
-Utilty.java
 This files is used for all helper realted functions.This is internally used by above 5 files. 

b) Steps to compile the Shared memory
	1.Change directory to "src_shared_memory" directory
	cd src_shared_memory
	
	2.Compile all java files
	 javac com/iit/cs553/*.java
	 
	3.Create a jar file from the above class files 
	jar cf sharedmemory.jar *.class
	
	4.Execute the jar using 
	jar sharedmemory.jar SharedMemory
 
 c)Commands to execute shared memory
	1. Install java
	cd ~
	sudo apt-get update
	sudo apt-get upgrade
	sudo apt-get install ssh
	sudo apt-get install default-jdk
	export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
	
	2.change directory to "src_shared_memory" directory
	cd src_shared_memory
	
	3.for configuration 1 shared memory tera sort execution,we need to execute below script.
	shared_memory_config1.sh

	4.for configuration 2 shared memory tera sort execution, we need to execute
	shared_memory_config2.sh
	
	
HADOOP:
a)Files Description:
  The source code under "src_Hadoop" directory contains all thefiles realted to Hadoop.
  -CustomTerasort.java
   This file contains the map reduce code used to sort 1 TB of data.
  -Configs 
   This folder contains one bashrc file which need to be copied to ec2 user's bash file and also two folders under_etc_hadoop 
   which can be used for all the configs and another is another is under_hadoop folder which conatins all Custom Map related 
   code and gensort file and valsort file.

b) Steps to compile the Shared memory
	1.Change directory to "src_Hadoop" directory
	cd src_Hadoop
	
	2.Compile all java files
	 javac *.java
	 
	3.Create a jar file from the above class files 
	jar cf terasort_hadoop.jar *.class
   
 c)Commands to execute shared memory
   Initially ceate an amazon ec2 instance of type i3.large which is required for 128GB and i3.4xlarge for the same for 1TB
	Automation:
   1.For Config 1(Hadoop single node,128gb) use the shell script "run_config1_hadoop.sh" under scripts folder.This will run hadoop for single node and 
     whenever it prompts for user intervention like config files, copy the values from src_Hadoop folder.
	
   2.For Config 2(Hadoop single node 1TB) use the shell script "run_config2_hadoop.sh" under scripts folder.This will run hadoop for single node and 
     whenever it prompts for user intervention like config files, copy the values from src_Hadoop folder.	
	
   3.For config 3(Hadoop single node 1TB) use the shell script "run_config2_hadoop.sh" under scripts folder.This will run hadoop for single node and 
     whenever it prompts for user intervention like config files, copy the values from src_Hadoop folder.	 
	 
	Manual run:
	1.Create new public key and copy it to autirised keys
		ssh-keygen -t rsa -P ""
		cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys
	2.ssh localhost
	3.Download the mirror wget http://mirror.olnevhost.net/pub/apache/hadoop/common/hadoop-2.9.0/hadoop-2.9.0.tar.gz
	  and also extract the tar file
	4.tar xvzf hadoop-2.9.0.tar.gz
	5.Rename folder using
	  mv hadoop-2.9.0 hadoop
	6.Update the following files
	  - vi ~/.bashrc
	  - vi /usr/local/hadoop/etc/hadoop/hadoop-env.sh
	  - vi /usr/local/hadoop/etc/hadoop/core-site.xml
	  - vi /usr/local/hadoop/etc/hadoop/hdfs-site.xml
	  - vi /usr/local/hadoop/etc/hadoop/mapred-site.xml
	  - vi /usr/local/hadoop/etc/hadoop/yarn-site.xml
	Using the ones in src_Hadoop/under_etc_hadoop folder
	7.After modify the above files we will format the namenode by doing this
      hdfs namenode -format
	8.then execute start-all.sh
	
d)How to Run the program:
	1.First go to Hadoop folder and compile the java by doing this
	  ./bin/hadoop com.sun.tools.javac.Main CustomTerasort.java
	2.create a jar file
	  jar cf terasort_hadoop.jar CustomTerasort*.class
	3.Download gensort and run the following command
	  ./gensort -a <size of dataset> <input_text_file.txt>
	4.Now if we are running the dataset on hdfs then move into the local file system by doing this 
	  hdfs dfs  -put -p <onegb.txt or 128GB.txt> <input_folder>
	5.Then at the end run the program using the following command
	  hadoop jar terasort.jar CustomTerasort </input_folder> </output_folder>
	

