package parser;
import java.util.*;

public class Converter {
	
	//Static Variables
	private static Map<String, Integer> opcodes = new HashMap<String, Integer>();
	static {
		opcodes.put("add", 0x0);
		opcodes.put("sub", 0x1);
		opcodes.put("and", 0x2); 
		opcodes.put("or",  0x3);
		opcodes.put("inc", 0x4);
		opcodes.put("bra", 0x5);
    }
	
	private static String variable = "[A-Za-z0-9]";
	private static String literal = "#+[0-9]";
	
	public static Integer getOpcode(String instructionName) {
		return (Integer) opcodes.get(instructionName.toLowerCase());
	}
	
	public static ArrayList<String> INC(ArrayList<String> params){
		ArrayList<String> instructions = new ArrayList<String>();
		instructions.add("ILOAD " + params.get(0));
		instructions.add("IINC 1");
		instructions.add("ISTORE " + params.get(0));
		return instructions;
	}
	
	public static ArrayList<String> BRA(ArrayList<String> params){
		ArrayList<String> instructions = new ArrayList<String>();
		System.out.println(params.get(1));
		if(params.get(0).toLowerCase().equals("z")) {
			instructions.add("IFEQ " + params.get(1));
		}
		else if (params.get(0).toLowerCase().equals("lt")) { 
			instructions.add("IFLT " + params.get(1));
		}	
		return instructions;
	}
	
	//OPERATIONS INCLLUDE IADD,ISUB,IAND,IOR
	public static ArrayList<String> operate(ArrayList<String> params, String instruction) {
		
		ArrayList<String> instructions = new ArrayList<String>();
		ArrayList<Integer> al = new ArrayList<Integer>();
		int vars = 0;
		int literals = 0;
		for (int i = 0; i < params.size(); i++) {
			String param = params.get(i);
			if (param.matches(variable)) {
				al.add(0);
				vars++;
			} else if (param.matches(literal)) {
				al.add(1);
				literals++;
			} 
		}
		if (vars == 1 && literals == 1) {
			if(al.get(0) == 0) instructions.add("ILOAD " + params.get(0));
			else instructions.add("BIPUSH " + params.get(0));
			if(al.get(1) == 0) instructions.add("ILOAD " + params.get(1));
			else instructions.add("BIPUSH " + params.get(1));
			instructions.add(instruction);
			instructions.add("ISTORE " + params.get(0));
		} else if (vars == 1 && literals == 2) {
			instructions.add("BIPUSH " + params.get(1));
			instructions.add("BIPUSH " + params.get(2));
			instructions.add(instruction);
			instructions.add("ISTORE " + params.get(0));
		} else if (vars == 2 && literals == 1) {
			if(al.get(1) == 0) instructions.add("ILOAD " + params.get(1));
			else instructions.add("BIPUSH " + params.get(1));
			if(al.get(2) == 0) instructions.add("ILOAD " + params.get(2));
			else instructions.add("BIPUSH " + params.get(2));
			instructions.add(instruction);
			instructions.add("ISTORE " + params.get(0));
		} else if (vars == 2) {
			instructions.add("ILOAD " + params.get(0));
			instructions.add("ILOAD " + params.get(1));
			instructions.add(instruction);
			instructions.add("ISTORE " + params.get(0));
		} else if (vars == 3) {
			instructions.add("ILOAD " + params.get(1));
			instructions.add("ILOAD " + params.get(2));
			instructions.add(instruction);
			instructions.add("ISTORE " + params.get(0));
		}
		return instructions;
	}
	
	public ArrayList<String> instructionPass(ArrayList<String> lines) {
        int lineNumber = 0;
        for (int h = 0; h < lines.size(); h++) {
        		String line = lines.get(h);
                Boolean instructionStarted = false;
                String instruction = "";
                final int lineLength = line.length();
                name: for (int j = 0; j < lineLength; j++) {
                    if (instructionStarted == false) {
                        if (!Character.isWhitespace(line.charAt(j))) {
                            instructionStarted = true;
                            instruction += line.charAt(j);
                        }
                    } else {
                        if (!Character.isWhitespace(line.charAt(j))) {
                            instruction += line.charAt(j);
                        } else {
                            break name;
                        }
                    }
                }
                if (opcodes.containsKey(instruction.toLowerCase())) {
                    String paramsLine = line.replace(instruction, "").replaceAll(" ", "");
                    String[] rawParams  = paramsLine.split(",");
                    ArrayList<String> params = new ArrayList<String>(Arrays.asList(rawParams));
                    ArrayList<String> replacementInstructions = toIJVM(opcodes.get(instruction.toLowerCase()), params);
                    int startIndex = lineNumber;
                    int endIndex = lineNumber + replacementInstructions.size();
                    lines.remove(startIndex);
                    int x = 0;
                    for (int i = startIndex; i < endIndex; i++) {
                    	lines.add(i, replacementInstructions.get(x));
                    	x += 1;
                    }
                }
            lineNumber += 1;
        }
        return lines;
    }
    
    public ArrayList<String> convertToRisc(ArrayList<String> lines) {
        ArrayList<String > newLines = instructionPass(lines);
        return newLines;
    }
	
	public static ArrayList<String> toIJVM(int opcode, ArrayList<String> params) {
		ArrayList<String> risc = new ArrayList<String>();
		switch(opcode) {
			case 0: 
				risc = operate(params,"IADD");
			break;
			case 1:
				risc = operate(params,"ISUB");
			break;
			case 2:
				risc = operate(params,"IAND");
			break;
			case 3:
				risc = operate(params,"IOR");
			break;
			case 4:
				risc = INC(params);
			break;
			case 5:
				risc = BRA(params);
			break;
			default:
			break;
		}
		return risc;
	}
}