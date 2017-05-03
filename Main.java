package parser;
import java.io.*;

public class Main {
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("Invalid arguments. Compiler requires two args: inputFilePath outFilePath");
			System.exit(1);
		}
		String inputFilePath = args[0];
		String outputFilePath = args[1];
		try {
			File textFile = new File(inputFilePath);
			CISCFile cisc = new CISCFile(textFile);
			cisc.toRisc(outputFilePath);
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println("Input file path not found");
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}
}
