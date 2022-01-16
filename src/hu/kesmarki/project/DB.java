package hu.kesmarki.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import hu.kesmarki.project.models.Person;

public class DB {

	private static String select = "SELECT "
			+ "people.person_id, firstname, lastname, age, gender, "
			+ "addresses.address_id, house_number, street, city, country, zip, "
			+ "contacts.contact_id, phone_number, intercom "
			+ "FROM people "
			+ "FULL OUTER JOIN addresses on people.person_id = addresses.person_id "
			+ "FULL OUTER JOIN contacts on addresses.address_id = contacts.address_id";
	private static JDBC jdbcEntity = new JDBC();
	private static int selectBreakPoints[] = {5, 11};
	private static String dataProps = jdbcEntity.query(true, select, 14, selectBreakPoints).get(0);
	private static String tables[] = {"people", "addresses", "contacts"};
	
	public List<Person> getData() {
			
		List<String> returnList = jdbcEntity.query(false, select, 14, selectBreakPoints);
		List<Person> people = new ArrayList<Person>();
		
		if(returnList.size()<1) {
			System.out.println("DATABASE: There are no records in the database.\n");
			return people;
		}
		
		String currentRow[];
		boolean persExists;
		boolean addrExists;
		int persId;
		int addrId;
		int tempAddrsSize;
		
		for (int lIndex = 0; lIndex < returnList.size(); lIndex++) {
			
			currentRow = returnList.get(lIndex).split(";");
			persExists = false;
			addrExists = false;
			persId = Integer.parseInt(currentRow[0].split("<>")[0]);
			
			if (!currentRow[1].split("<>")[0].equals("null")) {
				addrId = Integer.parseInt(currentRow[1].split("<>")[0]);
			} else {
				addrId = 0;
			}
			for (int pIndex = 0; pIndex < people.size(); pIndex++) {
					
					tempAddrsSize = people.get(pIndex).getAddresses().size();
					for (int aIndex = 0; aIndex < tempAddrsSize ; aIndex ++) {
						
						if(addrId == people.get(pIndex).getAddresses().get(aIndex).getId()) {
							people.get(pIndex).getAddresses().get(aIndex).addContact((currentRow[2]));
							persExists = true;
							addrExists = true;
						}
					}
					if(persId == people.get(pIndex).getId() && !addrExists) {
						people.get(pIndex).addAddress(currentRow[1], currentRow[2]);
						persExists = true;
					}
				}
			if(!persExists) {
				people.add(new Person(currentRow[0],currentRow[1],currentRow[2]));
			}
		}
		
		return people;
	}
	
	public List<Person> addData(List<Person> people) {
		
		int option = Tools.choose("add");
				
		int id;
		String parentId = "";
		
		if(option>1) {
			System.out.println("Please provide the ID of the entity you want to add to!");
			id = checkId(option, people);
			parentId += id + ",";
		} else {
			id = 0;
		}
		
		String propArray[] = dataProps.split(";");
		boolean needId = false;
		int newId = generateId(option, people);
		
		String insertString = "INSERT INTO "+tables[option-1]
				+ " VALUES ("+ newId + "," + parentId ;
		List <String> newData = Tools.readData(propArray[option-1].split("<>"), needId);
		
		for(int ndIndex = 0; ndIndex < newData.size(); ndIndex++) {
			if (ndIndex != newData.size()-1) {
				insertString += newData.get(ndIndex) + ", ";
			}
			else {
				insertString += newData.get(ndIndex) + ")";
			}
			newData.set(ndIndex, Tools.removeApostrophes(newData.get(ndIndex)));
		}
		
		if (jdbcEntity.update(insertString)) {
			switch (option) {
			case 1:
				people.add(new Person(newId + "<>" 
						+ newData.stream()
						.collect(Collectors.joining("<>"))));
				break;
			case 2:
				people.stream().filter(person -> person.getId() == id)
				.findFirst().orElse(null).addAddress(newId + "<>" 
						+ newData.stream()
						.collect(Collectors.joining("<>")), "null");
				break;
			case 3:
				people.stream()
				.flatMap(person -> person.getAddresses().stream())
				.filter(address -> address.getId() == id)
				.findFirst()
				.orElse(null).addContact(newId + "<>" 
						+ newData.stream()
						.collect(Collectors.joining("<>")));
				break;
			default:
				break;
			}
		}
		
		Collections.sort(people, Comparator.comparingInt(Person ::getId));
		return people;
	}
	
	public List<Person> modifyData(List<Person> people) {
		int option = Tools.choose("modify");
		
		if (noRecords(option, people)) {
			return people;
		}
		
		System.out.println("Please provide the ID of the chosen record!");
		int id = checkId(option, people);
		
		String propArray[] = dataProps.split(";");
		List <String> newData = Tools.readData(propArray[option-1].split("<>"), false);
		
		String updateString = "UPDATE " + tables[option-1] + " SET ";
		String properties[] = propArray[option-1].split("<>");
		
		for(int ndIndex = 0; ndIndex < newData.size(); ndIndex++) {
			updateString += properties[ndIndex+1].split(":")[0]
					+ " = " + newData.get(ndIndex);
			
			if (ndIndex != newData.size()-1) {
				updateString += ", ";
			}
			else {
				updateString += " WHERE " + properties[0].split(":")[0] 
						+ " = " + id;
			}
			newData.set(ndIndex, Tools.removeApostrophes(newData.get(ndIndex)));
		}
		
		if (jdbcEntity.update(updateString)) {
			switch (option) {
			case 1:
				people.stream().filter(person -> person.getId() == id)
				.findFirst().orElse(null).setData(newData);
				break;
			case 2:
				people.stream()
				.flatMap(person -> person.getAddresses().stream())
				.filter(address -> address.getId() == id)
				.findFirst()
				.orElse(null).setData(newData);
				break;
			case 3:
				people.stream()
				.flatMap(person -> person.getAddresses().stream())
				.flatMap(address -> address.getContacts().stream())
				.filter(contact -> contact.getId() == id)
				.findFirst()
				.orElse(null).setData(newData);
				break;
			default:
				break;
			}
		}
		return people;
	}	
	
	public List<Person> deleteData(List<Person> people) {
		
		int option = Tools.choose("delete");
		
		if (noRecords(option, people)) {
			return people;
		}
		
		System.out.println("Please provide the ID of the chosen record!");
		int id = checkId(option, people);
			
		String propArray[] = dataProps.split(";");
		String deleteString = "DELETE FROM "
				+ tables[option-1] + " WHERE "
				+ propArray[option-1].split(":")[0] +"=" + id; 
		
		if (jdbcEntity.update(deleteString)) {
			switch (option) {
			case 1:
				people.removeIf(person -> person.getId() == id);
				break;
			case 2:
				people.forEach(person -> person.getAddresses()
						.removeIf(address -> address.getId() == id));
				break;
			case 3:
				people.stream()
				.flatMap(person -> person.getAddresses().stream())
				.forEach(address -> address.getContacts()
				.removeIf(contact -> contact.getId() == id));
				break;
			default:
				break;
			}
		}
		return people;
	}
	
	private static int checkId(int option, List<Person> people) {
		List<Integer> validIds = getIds(option, people);
		
		int id = Integer.parseInt(Tools.askInput("int", ""));	
		
		while (!validIds.contains(id)) {
			System.out.println("Please select a valid ID!");
			id = Integer.parseInt(Tools.askInput("int", ""));
		}
		
		return id;
	}
	
	private static int generateId(int option, List<Person> people) {
		List<Integer>returnList = getIds(option, people);
		
		if( returnList.size()<1 ) {
			return 1;
		}
		
		int id = returnList.get(returnList.size()-1)+1;
		
		for(int index = 0; index < returnList.size(); index++) {
			System.out.println(returnList.get(index));
			if (index != returnList.size()-1 && returnList.get(index+1)-returnList.get(index)>1) {
				id = returnList.get(index)+1;
			}
		}
		
		return id;
	}
	
	private static List<Integer> getIds(int option, List<Person> people) {
		List<Integer> returnList = new ArrayList<Integer>();
				
		switch (option) {
		case 1:
			returnList = 
				people.stream()
	              .map(person -> person.getId())
	              .collect(Collectors.toList());
			break;
		case 2:
			returnList =
				people.stream()
				.flatMap(person -> person.getAddresses().stream())
				.map(address -> address.getId())
				.collect(Collectors.toList());
			break;
		case 3:
			returnList =
				people.stream()
				.flatMap(person -> person.getAddresses().stream())
				.flatMap(address-> address.getContacts().stream())
				.map(contact -> contact.getId())
				.collect(Collectors.toList());
			break;
		default:
			break;
		}
		
		Collections.sort(returnList);
		return returnList;
	}
	
	private static boolean noRecords(int option, List<Person> people) {
		if (getIds(option, people).size()<1) {
			System.out.println("DB: There're no records of the selected type.");
			return true;
		}
		else {
			return false;
		}
	}

}
