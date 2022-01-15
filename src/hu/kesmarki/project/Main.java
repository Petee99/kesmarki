package hu.kesmarki.project;

import java.util.List;
import hu.kesmarki.project.models.Person;

public class Main {
	public static void main(String[] args) {
		DB dataBase = new DB();
		List<Person> people = dataBase.getData();
		String option = "";
		
		Tools.welcome();
		do {
			System.out.println("\nChoose an action! (list/add/delete/modify/exit)");
			option = Tools.askInput("String", "");
			
			switch (option) {
			case "list":
				printAllPeople(people);
				break;
			case "add":
				people = dataBase.addData(people);
				break;
			case "delete":
				people = dataBase.deleteData(people);
				break;
			case "modify":
				people = dataBase.modifyData(people);
				break;
			case "exit":
				System.out.println("\nGood bye!");
				break;
			default:
				System.out.println("\nThis isn't a proper command.");
				break;
			}
		} while(!option.equals("exit"));
		
	}
	
	private static void printAllPeople(List<Person> people) {
		for (int dIndex = 0; dIndex < people.size(); dIndex++) {
			System.out.println(people.get(dIndex).getAllData());
		}
	}
	
}
