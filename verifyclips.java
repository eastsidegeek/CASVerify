import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import com.filepool.fplibrary.*;

public class verifyclips {

	public static void main(String[] args) {
		
		int exitCode = 0;
		String appName="HCA CAS Verify";
	    String appVersion="3.1";
		
		InputStreamReader inputReader = new InputStreamReader(System.in);
		BufferedReader stdin = new BufferedReader(inputReader);
		boolean exists = false;
		
		try {
			System.out.print("Address of cluster> ");
			String poolAddress = stdin.readLine();
			
			System.out.print("Input File> ");
			String answer = stdin.readLine();
			
			System.out.print("Output File> ");
			String outfilename = stdin.readLine();
			
			File file = new File(answer);
			FileReader fileReader = new FileReader(file);
			
			File outfile = new File(outfilename);
			FileWriter fw = new FileWriter(outfile, false);
			PrintWriter pw = new PrintWriter(fw);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line;
		
		    FPPool.RegisterApplication(appName,appVersion);
			    
			// New feature for 2.3 lazy pool open
			FPPool.setGlobalOption(
				FPLibraryConstants.FP_OPTION_OPENSTRATEGY,
				FPLibraryConstants.FP_LAZY_OPEN);

			// open cluster connection
			FPPool thePool = new FPPool(poolAddress);
			
			int iExistCount = 0;
			int iMissingCount = 0;
			while((line = bufferedReader.readLine()) != null) {
								
				exists = FPClip.Exists(thePool,line);
				pw.print(line + ","+exists+"\n");
				if(exists == true) {
					iExistCount++;
				} else {
					iMissingCount++;
				}
			} // end while
			
			pw.print("--------------\nProcessing complete.\n");
			pw.print(iExistCount + " records exist\n");
			pw.print(iMissingCount + " records missing\n");
			// Always close the Pool connection when finished.
			thePool.Close();
			System.out.println(
				"\nClosed connection to Centera cluster (" + poolAddress + ")");
			inputReader.close();
			stdin.close();
			bufferedReader.close();
			pw.close();
			fw.close();
				
		} catch (FPLibraryException e) {
			exitCode = e.getErrorCode();
			System.err.println(
				"Centera SDK Error: " + e.getMessage() + "(" + exitCode + ")");
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			exitCode = -1;
		} catch (IOException e) {
			System.err.println("IO Error occured: " + e.getMessage());
			e.printStackTrace();
			exitCode = -1;
		}

		System.exit(exitCode);
		
	} // end main

} // end class verifyclips
