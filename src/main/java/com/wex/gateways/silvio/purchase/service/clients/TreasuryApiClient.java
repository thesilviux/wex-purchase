package com.wex.gateways.silvio.purchase.service.clients;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.wex.gateways.silvio.purchase.exception.ExchangeRateNotFoundException;

@Service
public class TreasuryApiClient {
	
	@Value("${fiscal-service.base-url}")
	private String fiscalServiceBaseUrl;
	
	@Value("${fiscal-service.endpoint}")
	private String fiscalServiceEndpoint;

	@SuppressWarnings("unchecked")
	@Cacheable( value="exchangeRates", sync=true)
	public Map<String, String> getExchangeRates(String countryCurrencyDescription, String date) 
		throws ExchangeRateNotFoundException
	{

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate dateInitial = LocalDate.parse( date, dateFormatter);
		LocalDate dateFinal = dateInitial.minusMonths(6);
	    String dateBefore = dateFinal.format(dateFormatter);
	    
	    UriComponents uri = UriComponentsBuilder
    		.fromHttpUrl(fiscalServiceBaseUrl)
    		.path(fiscalServiceEndpoint)
    		.queryParam("fields", "country_currency_desc,exchange_rate,record_date")
    		.queryParam("filter", ""
				+ "country_currency_desc:eq:" + countryCurrencyDescription + ","
				+ "record_date:lte:" + date + ","
				+ "record_date:gte:" + dateBefore + "" )
    		.queryParam("page[size]", "1")
    		.queryParam("sort", "-record_date")
    		.build();
	    System.out.println(uri.toString());

	    RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<Object> result = restTemplate.getForEntity(uri.toString(), Object.class);
	    
	    Map<String, Object> responseBody = (Map<String, Object>) result.getBody();
	    ArrayList<Map<String, String>> data = (ArrayList<Map<String, String>>) responseBody.get("data");
	    
	    if(data.size()==0) return null;

	    System.out.println(data.get(0));
	    
	    return data.get(0);
	}
}
