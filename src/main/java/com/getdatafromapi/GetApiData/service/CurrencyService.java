package com.getdatafromapi.GetApiData.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getdatafromapi.GetApiData.model.Currency;
import com.getdatafromapi.GetApiData.repo.CurrencyRepo;

import jakarta.annotation.PostConstruct;

@Service
public class CurrencyService {
	@Autowired
	private CurrencyRepo currencyRepo;	
	
	public CurrencyService() {}

	public CurrencyService(CurrencyRepo currencyRepo) {
		super();
		this.currencyRepo = currencyRepo;
	}
	
	// Currency conversion method
	public double calculate(int numCoins,String base, String destination, Currency cur) 
	{
		double baseCur = cur.getValues().get(base);
		double destinationCur = cur.getValues().get(destination);
		double mid = destinationCur/baseCur;

		return numCoins*mid;
	}
	
	// To Get the recent Date out of the database
	public Currency getRecentDate() 
	{
		List<Currency> currencies = currencyRepo.findAllSortedByDateAsc();
		List<LocalDate> localDates = new ArrayList<>();
		for (Currency currency:currencies)
		{
			LocalDate date = LocalDate.parse(currency.getDate());
			localDates.add(date);
		}
		LocalDate recentDate = Collections.max(localDates);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String date = recentDate.format(formatter);
		
		
		return currencyRepo.findById(date).get();
	}
	
	// Predict method
	public double predict(String base, String date) 
	{
		List<Currency> currencies = currencyRepo.findAllSortedByDateAsc();
		
		List<LocalDate> dates = new ArrayList<>();
		List<Double> val = new ArrayList<>();
		
		for (int i=0; i<currencies.size(); i++)
		{
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			dates.add(LocalDate.parse(currencies.get(i).getDate(), dtf));
			val.add(currencies.get(i).getValues().get(base));
		}
		
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate newDate = LocalDate.parse(date, inputFormatter);
		
		long daysBetween = ChronoUnit.DAYS.between(dates.get(dates.size()-1),newDate);
		
		for (int i=0; i<daysBetween; i++)
		{
			double nextVal = getNextValue(val, 0, val.size()-1);
			val.add(nextVal);
			val.remove(0);
		}
		return val.get(val.size()-1);
	}	
	
	// Function to get the next value of series of value by finding average
	public double getNextValue(List<Double> values, int start, int total) 
	{
	    double sum = 0;
	    for (int i = start; i < total; i++) {
	      sum += values.get(i);
	    }
	    return sum / (total-start);
	 }
	
	@Scheduled(cron = "0 1 1 * * ?")
	@PostConstruct
	// Function which fetch data from API and store the results in database
	public void fetchAndStore() throws Exception
	{
        RestTemplate restTemplate = new RestTemplate();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusDays(30);
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", "KzY10GJw16FuKRnvNNZa51aZawypdNmH");
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String apiUrl = "https://api.apilayer.com/fixer/timeseries?start_date="+startDate+"&end_date="+endDate+"&base=USD";
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        JSONObject myObject = new JSONObject(response.getBody());
        JSONObject values = myObject.getJSONObject("rates");
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(values.toString());

        Iterator<String> fieldNames = jsonNode.fieldNames();
        
        List<Currency> currencies = new ArrayList<>();
        while (fieldNames.hasNext()) {
           String fieldName = fieldNames.next();
           Map<String, Double> map = toMap(values.getJSONObject(fieldName));
           Currency currency = new Currency(fieldName, map);
           currencies.add(currency);
        }
        
        currencyRepo.saveAll(currencies); 
        System.out.println("updated");
    }
	
	
	// Function to convert JSON object to map (Helper function)
	public static Map<String, Double> toMap(JSONObject jsonobj)  throws JSONException 
	{
        Map<String, Double> map = new HashMap<String, Double>();
        @SuppressWarnings("unchecked")
		Iterator<String> keys = jsonobj.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            Double val = jsonobj.getDouble(key);
            map.put(key, val);
        }   
        return map;
    }
	
	public List<Currency> getAll() {
		return currencyRepo.findAllSortedByDateAsc();
	}
}
