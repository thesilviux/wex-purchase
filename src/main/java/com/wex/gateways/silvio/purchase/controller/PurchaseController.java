package com.wex.gateways.silvio.purchase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wex.gateways.silvio.purchase.dto.ExchangeRateDTO;
import com.wex.gateways.silvio.purchase.dto.PurchaseConvertResponse;
import com.wex.gateways.silvio.purchase.dto.PurchaseCreateRequest;
import com.wex.gateways.silvio.purchase.exception.CurrencyDoesntExistException;
import com.wex.gateways.silvio.purchase.exception.ExchangeRateNotFoundException;
import com.wex.gateways.silvio.purchase.exception.PurchaseNotFoundException;
import com.wex.gateways.silvio.purchase.model.Purchase;
import com.wex.gateways.silvio.purchase.service.PurchaseService;
import com.wex.gateways.silvio.purchase.service.ExchangeRateService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {
	
	@Autowired
	private PurchaseService purchaseService;
	
	@Autowired
	private ExchangeRateService exchangeService;

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public Purchase store(@Valid @RequestBody PurchaseCreateRequest purchase) {
		System.out.println(purchase);
		Purchase created = purchaseService.store(purchase);
		return created;
	}
	
	@GetMapping("/{id}")
	public PurchaseConvertResponse retrieve(
			@PathVariable String id, 
			@RequestParam String countryCurrency) 
		throws PurchaseNotFoundException, CurrencyDoesntExistException, ExchangeRateNotFoundException {
		
		Purchase purchase = purchaseService.retrieveById(id);
		
		return convert(purchase, countryCurrency);
	}
	
	private PurchaseConvertResponse convert(Purchase purchase, String countryCurrency) 
		throws CurrencyDoesntExistException, ExchangeRateNotFoundException {
		
		PurchaseConvertResponse purchaseConvertResponse = new PurchaseConvertResponse(purchase);
		String date = purchaseConvertResponse.getDate();
		
		ExchangeRateDTO exchangeRate = exchangeService.findExchangeRateByCountryCurrency(countryCurrency,date);
		System.out.println(exchangeRate);
		purchaseConvertResponse.setExchangeRate(exchangeRate.getExchangeRate());

		return purchaseConvertResponse;
	}
	
}
