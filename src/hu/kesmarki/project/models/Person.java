package hu.kesmarki.project.models;

import java.util.ArrayList;
import java.util.List;

public class Person {
	
	private int id;
	private String firstname;
	private String lastname;
	private int age;
	private String gender;
	private List<Address> addresses;

	public Person(String person, String address, String contact) {
		String pParts[] = person.split("<>");
		this.id = Integer.parseInt(pParts[0]);
		this.firstname = pParts[1];
		this.lastname = pParts[2];
		this.age = Integer.parseInt(pParts[3]);
		this.gender = pParts[4];
		this.addresses = new ArrayList<Address>();
		if (!address.split("<>")[0].equals("null")) {
			this.addresses.add(new Address(address, contact));
		}
		
	}
	
	public Person(String person) {
		this(person, "null", "null");
	}
	
	public List<Address> getAddresses() {
		return addresses;
	}
	
	public void setData(List <String> data) {
		this.firstname = data.get(0);
		this.lastname = data.get(1);
		this.age = Integer.parseInt(data.get(2));
		this.gender = data.get(3);
	}

	public void addAddress(String address, String contact) {
		if (!address.split("<>")[0].equals("null")) {
			this.addresses.add(new Address(address, contact));
		}
	}
	
	public int getId() {
		return id;
	}
	
	public String getAllData() {
		String addressString = "";
		
		for (Address address : addresses) {
			addressString += address.getAllData();
		}
		
		return "\nPerson:\n"
				+ "- ID: " + id
				+ ", First Name: " + firstname
				+ ", Last Name: " + lastname
				+ ", Age: " + age
				+ ", Gender: " + gender
				+ "\n	Address(es): " + addressString;
	}		
}
