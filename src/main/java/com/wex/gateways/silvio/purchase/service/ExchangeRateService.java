package com.wex.gateways.silvio.purchase.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wex.gateways.silvio.purchase.dto.ExchangeRateDTO;
import com.wex.gateways.silvio.purchase.exception.CurrencyDoesntExistException;
import com.wex.gateways.silvio.purchase.exception.ExchangeRateNotFoundException;
import com.wex.gateways.silvio.purchase.i18n.MessageService;
import com.wex.gateways.silvio.purchase.model.CountryCurrency;
import com.wex.gateways.silvio.purchase.service.clients.TreasuryApiClient;

@Service
public class ExchangeRateService {
	
	@Value("${fiscal-service.base-url}")
	private String fiscalServiceBaseUrl;
	
	@Value("${fiscal-service.endpoint}")
	private String fiscalServiceEndpoint;
	
	@Autowired
	private TreasuryApiClient treasuryApiClient;
	
	@Autowired
	private MessageService messages;
	
	public ExchangeRateDTO  findExchangeRateByCountryCurrency(String countryCurrencyDescription, String date) 
		throws CurrencyDoesntExistException, ExchangeRateNotFoundException {
		
		CountryCurrency currency = CountryCurrency.fromString(countryCurrencyDescription);
		if(currency==null) throw new CurrencyDoesntExistException(
			messages.getMessage("exception.currency-doesnt-exist", countryCurrencyDescription));
		
		Map<String, String> results = treasuryApiClient.getExchangeRates(countryCurrencyDescription, date);
		
		if(results == null) throw new ExchangeRateNotFoundException(
			messages.getMessage("exception.exchange-rate-not-found"));
		
		if(results.size()>0) {
			return new ExchangeRateDTO(
				results.get("country_currency_desc"), 
				results.get("exchange_rate"), 
				results.get("record_date")
			);
		}
		
		return null;
	}
}
