package com.getdatafromapi.GetApiData.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.getdatafromapi.GetApiData.model.Currency;
import com.getdatafromapi.GetApiData.service.CurrencyService;

@RestController
public class CurrencyController {
	private CurrencyService currencyService;
	
	
	public CurrencyController(CurrencyService currencyService) {
		super();
		this.currencyService = currencyService;
	}

	@GetMapping("/currency")
	public List<Currency> addToDataBase(){
		return currencyService.getAll();
	}
	
	@GetMapping("/currency/exchange")
	public HashMap<String, String> exchange(
				@RequestHeader(name="base", required = true) String base,
				@RequestHeader(name="destination", required = true) String destination) 
	{
		
		HashMap<String, String> map = new HashMap<>();
		int numCoins = Integer.parseInt(base.split(" ")[0]);
		
		String baseCurString = base.split(" ")[1];
		if (baseCurString.equalsIgnoreCase(destination))
		{
			map.put(base, numCoins+" "+destination);
			return map;
		}
		Currency cur = currencyService.getRecentDate();
		double res = currencyService.calculate(numCoins, baseCurString, destination, cur);
		map.put(base, res + " "+destination);
		return map;
	}
	
	@GetMapping("/currency/predict")
	public HashMap<String, String> predict(@RequestHeader(name="base", required = true) String base,
						  @RequestHeader(name="date", required = true) String date) 
	{
		HashMap<String, String> map = new HashMap<>();
		double predictValue = currencyService.predict(base, date);
		map.put("predictedValue", predictValue+" "+base);
		return map;
	}
}
