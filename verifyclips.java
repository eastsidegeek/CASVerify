import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import com.filepool.fplibrary.*;

public class verifyclips {

	public static void main(String[] args) throws IOException {
		
		int exitCode = 0;
		String appName="HCA CAS Verify";
	    String appVersion="3.1";
	    String poolAddress = "";
	    BufferedReader bufferedReader;
	    PrintWriter pw;
	    FileWriter fw;
	    File outfile;
	    File file;
	    FileReader fileReader;
	    int iExistCount = 0;
		int iMissingCount = 0;
		int iExceptionCount = 0;
		
		InputStreamReader inputReader = new InputStreamReader(System.in);
		BufferedReader stdin = new BufferedReader(inputReader);
		boolean exists = false;
		FPPool thePool;
		
		
		System.out.print("Address of cluster> ");
		poolAddress = stdin.readLine();
		
		System.out.print("Input File> ");
		String answer = stdin.readLine();
		
		System.out.print("Output File> ");
		String outfilename = stdin.readLine();
		
		file = new File(answer);
		fileReader = new FileReader(file);
		
		outfile = new File(outfilename);
		fw = new FileWriter(outfile, false);
		pw = new PrintWriter(fw);
							
		bufferedReader = new BufferedReader(fileReader);
		
			
		String line;
			
		try {
		    FPPool.RegisterApplication(appName,appVersion);
			    
			// New feature for 2.3 lazy pool open
			FPPool.setGlobalOption(
				FPLibraryConstants.FP_OPTION_OPENSTRATEGY,
				FPLibraryConstants.FP_LAZY_OPEN);

			// open cluster connection
			thePool = new FPPool(poolAddress);
			
			while((line = bufferedReader.readLine()) != null) {
				if(line.length() == 53) {
					pw.print("Checking for clip "+line+"\n");
					
					try {
						exists = FPClip.Exists(thePool,line);
					} catch (FPLibraryException e) {
						iExceptionCount++;
					} // end catch 
					
					pw.print(line + ","+exists+"\n");
					if(exists == true) {
						iExistCount++;
					} else {
						iMissingCount++;
					}
				} // end clip length check
			} // end while
			
			thePool.Close();
			
		} catch (FPLibraryException e) {
			exitCode = e.getErrorCode();
			System.err.println(
				"Centera SDK Error: " + e.getMessage() + "(" + exitCode + ")");
			System.exit(exitCode);
		} // end catch 
			
		pw.print("--------------\nProcessing complete.\n");
		pw.print(iExistCount + " records exist\n");
		pw.print(iMissingCount + " records missing\n");
		pw.print(iExceptionCount + " records caused Centera SDK exceptions\n");
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

} // end class verifyclips
