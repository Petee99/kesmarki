package hu.kesmarki.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	private static String objects[] = {"person", "address", "contact"};
	
	
	public List<Person> getData() {
			
		List<String> returnList = jdbcEntity.query(false, select, 14, selectBreakPoints);
		List<Person> people = new ArrayList<Person>();
		
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
		String objectId = "";
		
		if(option>1) {
			System.out.println("Please provide the ID of which " + objects[option-2] 
					+ " you want to add this " + objects[option-1] + " to!");
			objectId = Tools.askInput("int", "") + ",";	
		}
		
		
		String propArray[] = dataProps.split(";");
		boolean needId = false;
		
		String insertString = "INSERT INTO "+tables[option-1]
				+ " VALUES ("+generateId(tables[option-1]) + "," + objectId ;
		
		List <String> newData = Tools.readData(propArray[option-1].split("<>"), needId);
		
		for(int ndIndex = 0; ndIndex < newData.size(); ndIndex++) {
			
			if (ndIndex != newData.size()-1) {
				insertString += newData.get(ndIndex) + ", ";
			}
			else {
				insertString += newData.get(ndIndex) + ")";
			}
		}
		
		jdbcEntity.update(insertString);
		
		return getData();
	}
	
	public List<Person> modifyData(List<Person> people) {
		int option = Tools.choose("modify");
		
		System.out.println("Please provide the ID of the chosen record!");
		int id = Integer.parseInt(Tools.askInput("int", ""));
		
		System.out.println(people.stream().filter(person -> id==person.getId()).findFirst().orElse(null));
		
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
		}
		
		jdbcEntity.update(updateString);		
		return getData();
	}	
	
	public List<Person> deleteData(List<Person> people) {
		
		int option = Tools.choose("delete");
		System.out.println("Please provide the ID of the chosen record!");
		int id = Integer.parseInt(Tools.askInput("int", ""));

		String propArray[] = dataProps.split(";");
		String deleteString = "DELETE FROM "
				+ tables[option-1] + " WHERE "
				+ propArray[option-1].split(":")[0] +"=" + id; 
		
		jdbcEntity.update(deleteString);
		return getData();
	}
	
	
	private static int generateId(String table) {
		String queryString = "SELECT * FROM " + table;
		List<String> returnList = jdbcEntity.query(queryString, 1);
		int ids[] = new int[returnList.size()];
		
		for (int lIndex = 0; lIndex < returnList.size(); lIndex++) {
			ids[lIndex] = Integer.parseInt(returnList.get(lIndex));
		}
		
		Arrays.sort(ids);
		int id = ids[ids.length-1]+1;
		
		for(int index = 0; index < ids.length; index++) {

			if (index != ids.length-1 && ids[index+1]-ids[index]>1) {
				id = ids[index+1]-1;
			}
		}
		
		return id;
	}
}
