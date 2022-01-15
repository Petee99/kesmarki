package hu.kesmarki.project.models;

import java.util.ArrayList;
import java.util.List;

public class Address {
	
	private int id;
	private int houseNumber;
	private String street;
	private String city;
	private String country;
	private int zip;
	private List<Contact> contacts;
	
	public Address(String address, String contact) {
		String aParts[] = address.split("<>");
		this.id = Integer.parseInt(aParts[0]);
		this.houseNumber = Integer.parseInt(aParts[1]);
		this.street = aParts[2];
		this.city = aParts[3];
		this.country = aParts[4];
		this.zip = Integer.parseInt(aParts[5]);
		this.contacts = new ArrayList<Contact>();
		if (!contact.split("<>")[0].equals("null")) {
			this.contacts.add(new Contact(contact));
		}
	}
	
	public List<Contact> getContacts() {
		return contacts;
	}

	public int getId() {
		return id;
	}
	
	public void setData(List <String> data) {
		this.houseNumber = Integer.parseInt(data.get(0));
		this.street = data.get(1);
		this.city = data.get(2);
		this.country = data.get(3);
		this.zip = Integer.parseInt(data.get(4));;
	}
	
	public void addContact(String contact) {
		if (!contact.split("<>")[0].equals("null")) {
			this.contacts.add(new Contact(contact));
		}
	}
	
	public String getAllData() {
		String contactString = "";
		
		for (Contact contact : contacts) {
			contactString += contact.getAllData();
		}
		
		return "\n	"
				+ " - ID: " + id
				+ ", House number: " + houseNumber
				+ ", Street: " + street
				+ ", City: " + city
				+ ", Country: " +country
				+ ", Zip: " + zip
				+ "\n		Contact(s): " + contactString;
	}
}
