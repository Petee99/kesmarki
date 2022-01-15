package hu.kesmarki.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Tools {
	
	private static Scanner scanner = new Scanner(System.in);
	
	public static List<String> readData(String properties[], boolean needId) {
		
		List<String> returnList = new ArrayList<String>();
		String property;
		String pType;
		
		for (int propIndex = 0; propIndex < properties.length; propIndex++ ) {
			property = properties[propIndex].split(":")[0].toUpperCase();
			pType = properties[propIndex].split(":")[1];
						
			if(needId && !property.equals("PERSON_ID")) {
				System.out.println("ID of the chosen entity:");
				returnList.add(askInput(pType,""));
			} else if (propIndex > 0) {
				System.out.println(property + " (" + pType + "):");
				returnList.add(askInput(pType,""));
			}
		}
		
		return returnList;
	}
	
	public static boolean checkLimits(float num, float minInc, float maxInc) {
		if(num >= minInc && num <= maxInc) {
			return true;
		}
		else {
			System.out.println("The number should be between " + (int)minInc 
					+ " and " + (int)maxInc + ". Please try again!");
			return false;
		}
	}
	
	// Number constraint form: [x;y] string form: excluded_char1; ...
	public static String askInput(String type, String constraints) {
		String input;
		boolean isCorrect = false; 
		
		do {
			input = scanner.nextLine();
			isCorrect = validateInput(input, type);
			
			if(isCorrect) {
				if(constraints.matches("\\[+[0-9]*;[0-9]*+]")) {
					float min = Float.parseFloat(constraints.split(";")[0].substring(1));
					float max = Float.parseFloat(constraints.split(";")[1].substring(0, 
							constraints.split(";")[1].length()-1));
					isCorrect = checkLimits(Float.parseFloat(input), min, max);
				}
			}
		} while (!isCorrect);
		
		if(type.equals("varchar")) {
			input = "'" + input + "'";
		}
		
		return input;
	}
	
	public static boolean validateInput(String input, String inType) {
		boolean isCorrect;
		
		switch (inType) {
		case "int":
			try{
		        Integer.valueOf(input);
		        isCorrect = true;
		    } catch (NumberFormatException e) {
		    	isCorrect = false;
		    }
			break;
		default: //If input should be string/varchar
			isCorrect = true;
			break;
		}
		
		if(!isCorrect) {
			System.out.println("Input is not of type: (" + inType + "). Please try again!");
		}
		
		return isCorrect;
	}
	
	public static void welcome() {
		System.out.println("Welcome! This console app uses the keywords listed below:\n"
				+ ">> list - lists all peopledata,\n"
				+ ">> add - adds peopledata,\n"
				+ ">> delete - deletes peopledata,\n"
				+ ">> modify - modifies peopledata,\n"
				+ ">> exit - exits the program.");
	}
	
	public static int choose(String action) {
		System.out.println("\nDo you want to "+ action +" a person (1),"
				+ " OR an address (2)"
				+ " OR a contact (3)?");
		return Integer.parseInt(Tools.askInput("int", "[1;3]"));
	}
}
