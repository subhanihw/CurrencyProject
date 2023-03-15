package com.getdatafromapi.GetApiData.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.getdatafromapi.GetApiData.model.Currency;

public interface CurrencyRepo extends MongoRepository<Currency, String> {
	@Query(value = "{}", sort = "{ date : 1 }")
    List<Currency> findAllSortedByDateAsc();
}
