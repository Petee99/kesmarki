package hu.kesmarki.project.models;

import java.util.List;

public class Contact {
	
	private int id;
	private String phoneNumber;
	private int intercom;
	
	public Contact(String contact) {
		String cParts[] = contact.split("<>");
		this.id = Integer.parseInt(cParts[0]);
		this.phoneNumber = cParts[1];
		this.intercom = Integer.parseInt(cParts[2]);
	}
	
	public void setData(List <String> data) {
		this.phoneNumber = data.get(0);
		this.intercom = Integer.parseInt(data.get(1));
	}
	
	public String getAllData() {
		return "\n		"
				+ " - ID: " + id
				+ ", Phone Number: " + phoneNumber
				+ ", Intercom: " + intercom;
	}
	
	public int getId() {
		return id;
	}
}
