package com.getdatafromapi.GetApiData.model;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Document(collection = "values")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Currency{
	@Id
	private String date;
	private Map<String, Double> values;
	
	public Currency() {
	}

	public Currency(String date, Map<String, Double> map) {
		super();
		this.date = date;
		this.values = map;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Map<String, Double> getValues() {
		return values;
	}

	public void setValues(Map<String, Double> values) {
		this.values = values;
	}
	
	public void printMap() {
		for (String name: values.keySet()) {
		    String key = name.toString();
		    String value = values.get(name).toString();
		    System.out.println(key + " " + value);
		}
	}

	@Override
	public String toString() {
		return "Currency [date=" + date + ", values=" + values + "]";
	}
	
	
}
