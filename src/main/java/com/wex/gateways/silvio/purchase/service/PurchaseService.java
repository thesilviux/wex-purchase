package com.wex.gateways.silvio.purchase.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wex.gateways.silvio.purchase.dto.PurchaseCreateRequest;
import com.wex.gateways.silvio.purchase.exception.PurchaseNotFoundException;
import com.wex.gateways.silvio.purchase.i18n.MessageService;
import com.wex.gateways.silvio.purchase.model.Purchase;
import com.wex.gateways.silvio.purchase.repository.PurchaseRepository;

@Service
public class PurchaseService {
	
	@Autowired
	private PurchaseRepository repository;	
	
	@Autowired
	private MessageService messages;
	
	public Purchase store(PurchaseCreateRequest request) {
		
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse( request.getDate(), dateFormatter);
		
		BigDecimal amount = new BigDecimal( request.getAmount() ).setScale(2, RoundingMode.HALF_UP);
		
		Purchase purchase = Purchase.builder()
				.description(request.getDescription())
				.date( date )
				.amount( amount )
				.build();
		
		repository.save(purchase);
		
		return purchase;
	}

	public Purchase retrieveById(String id) throws PurchaseNotFoundException {
		Optional<Purchase> purchase = repository.findById( Long.valueOf(id));
		
		if(!purchase.isPresent()) throw new PurchaseNotFoundException(
			messages.getMessage("exception.purchase-not-found", id));
		
		return purchase.get();
	}
	
}
