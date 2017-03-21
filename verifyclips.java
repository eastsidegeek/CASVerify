import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;

import com.filepool.fplibrary.*;

public class verifyclips {

	public static void main(String[] args) throws IOException {
		
		final ConcurrentStack<String> stack = new ConcurrentStack<>();
		
		int exitCode = 0;
		String appName="HCA CAS Verify";
	    String appVersion="3.1";
	    String poolAddress = "";
	    BufferedReader bufferedReader;
	    
	    FileWriter fw;
	    File outfile;
	    File file;
	    FileReader fileReader;
	    SynchronizedCounter iExistCount = new SynchronizedCounter();
	    SynchronizedCounter iMissingCount = new SynchronizedCounter();
	    SynchronizedCounter iExceptionCount = new SynchronizedCounter();
		
		InputStreamReader inputReader = new InputStreamReader(System.in);
		BufferedReader stdin = new BufferedReader(inputReader);
		
		System.out.print("Address of cluster> ");
		poolAddress = stdin.readLine();
		
		System.out.print("Input File> ");
		String answer = stdin.readLine();
		
		System.out.print("Output File> ");
		String outfilename = stdin.readLine();
		
		System.out.print("Number of threads> ");
		String sNumThreads = stdin.readLine();
		
		int iNumThreads = Integer.parseInt(sNumThreads);
		
		
		file = new File(answer);
		fileReader = new FileReader(file);
		
		outfile = new File(outfilename);
		fw = new FileWriter(outfile, false);
		final PrintWriter pw = new PrintWriter(fw);
							
		bufferedReader = new BufferedReader(fileReader);
		
		LocalDate startdate = LocalDate.now();
		LocalTime starttime = LocalTime.now();
		
		System.out.print("Start time " + startdate + " " + starttime + " with " + iNumThreads + " threads\r\n");
		pw.print("Start time " + startdate + " " + starttime + " with " + iNumThreads + " threads\r\n");
			
		String line;
			
		try {
		    FPPool.RegisterApplication(appName,appVersion);
			    
			// New feature for 2.3 lazy pool open
			FPPool.setGlobalOption(
				FPLibraryConstants.FP_OPTION_OPENSTRATEGY,
				FPLibraryConstants.FP_LAZY_OPEN);

			// open cluster connection
			final FPPool thePool = new FPPool(poolAddress);
			
			
			// Read entire file onto threadsafe stack
			while((line = bufferedReader.readLine()) != null) {
				stack.push(line);
			}
			
			// start up threads to check for existence
			for(int i=0;i<iNumThreads;i++) {
				new Thread("" + i) {
					public void run() {
						String myLine = "";
						boolean exists = false;
						
						while((myLine = stack.pop()) != null) {
							
							if(myLine.length() == 53) {
								//System.out.println("Checking for clip "+myLine);
								
								try {
									exists = FPClip.Exists(thePool,myLine);
								} catch (FPLibraryException e) {
									iExceptionCount.increment();
								} // end catch 
								
								pw.println(myLine + ","+exists);
								if(exists == true) {
									System.out.println("Clip "+myLine+" Found");
									iExistCount.increment();
								} else {
									System.out.println("Clip "+myLine+" Not Found");
									iMissingCount.increment();
								}
							} // end clip length check
						} // end while
					} // end run
				}.start(); // end new Thread
				
			} // end for
			
			Thread t;
			for(int i=0;i<iNumThreads;i++) { // wait for all threads to complete
				t = getThreadByName("" + i);
				try {
					if(t != null) { // if it's completed already it might be null
						t.join();
					}
				} catch (InterruptedException e) {
					System.err.println("Issue with thread: " + e.getMessage() + "\r\n");
				} // end catch
			}
			
			thePool.Close();
			
		} catch (FPLibraryException e) {
			exitCode = e.getErrorCode();
			System.err.println(
				"Centera SDK Error: " + e.getMessage() + "(" + exitCode + ")");
			System.exit(exitCode);
		} // end catch 
			
		LocalDate enddate = LocalDate.now();
		LocalTime endtime = LocalTime.now();
		
		pw.print("\r\n--------------\nProcessing complete.\r\n");
		pw.print("End time " + enddate + " " + endtime + "\r\n");
		pw.print(iExistCount.value() + " records exist\r\n");
		pw.print(iMissingCount.value() + " records missing\r\n");
		pw.print(iExceptionCount.value() + " records caused Centera SDK exceptions\n");
		
		System.out.print("--------------\nProcessing complete.\r\n");
		System.out.print("End time " + enddate + " " + endtime + "\r\n");
		System.out.print(iExistCount.value() + " records exist\r\n");
		System.out.print(iMissingCount.value() + " records missing\r\n");
		System.out.print(iExceptionCount.value() + " records caused Centera SDK exceptions\n");
		// Always close the Pool connection when finished.
		
		System.out.println(
			"\nClosed connection to Centera cluster (" + poolAddress + ")");
		inputReader.close();
		stdin.close();
		bufferedReader.close();
		pw.close();
		fw.close();
	
		System.exit(exitCode);
		
	} // end main

	public static Thread getThreadByName(String threadName) {
		for(Thread t : Thread.getAllStackTraces().keySet()) {
			if (t.getName().equals(threadName)) return t;
		} // end for
		return null;
	}	
} // end class verifyclips