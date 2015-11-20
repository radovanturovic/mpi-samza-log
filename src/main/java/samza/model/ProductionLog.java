package samza.model;

import java.util.HashMap;

public class ProductionLog {
	private String content;
	private String date;
	private String response;
	private HashMap<String, String> parameters;

	public ProductionLog() {
		super();
	}

	public ProductionLog(String content, String date, String response,
			HashMap<String, String> parameters) {
		super();
		this.content = content;
		this.date = date;
		this.response = response;
		this.parameters = parameters;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, String> parameters) {
		this.parameters = parameters;
	}

}
