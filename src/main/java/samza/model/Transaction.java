package samza.model;

import java.util.ArrayList;

public class Transaction {
	String ip;
	ArrayList<String> links;
	public Transaction(String ip, ArrayList<String> links) {
		super();
		this.ip = ip;
		this.links = links;
	}
	public Transaction() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public ArrayList<String> getLinks() {
		return links;
	}
	public void setLinks(ArrayList<String> links) {
		this.links = links;
	}
	
}
