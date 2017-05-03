package parser;

import java.util.*;
import java.io.*;

public class CISCFile {
	ArrayList<String> lines = new ArrayList<String>();
	
	public CISCFile(File assemblyFile) {
		this.lines = getLines(assemblyFile);
	}

	public ArrayList<String> getLines(File assemblyFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(assemblyFile));
			String line;
		    while ((line = reader.readLine()) != null) {
		    	String[] lineSplit = line.split("//");
		    	if (lineSplit.length > 0) {
		    		lines.add(lineSplit[0].trim());
		    	} else {
		    		lines.add(line);
		    	}
		    }
		    reader.close();
		} catch (Exception e) {
			if (e.getMessage() == "Invalid Line") {
				System.out.println(e.getMessage());
				System.exit(1);
			}
			e.printStackTrace();
		}
		return lines;
	}
	
	public void toRisc(String outputFile) {
		Converter converter = new Converter();
		ArrayList<String> newLines = converter.convertToRisc(lines);
		FileWriter writer;
		try {
			writer = new FileWriter(outputFile);
			for(String line: newLines) {
				if (line.trim().replaceAll("/", ""	).length() > 0) {
				  writer.write(line + "\n");
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
}

