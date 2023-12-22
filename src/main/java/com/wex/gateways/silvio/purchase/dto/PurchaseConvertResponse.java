package com.wex.gateways.silvio.purchase.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.wex.gateways.silvio.purchase.model.Purchase;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PurchaseConvertResponse {
	
	private String id;
	private String description;
	private String date;
	private String originalAmount;
	private String exchangeRate;
	private String convertedAmount;

	public PurchaseConvertResponse(Purchase purchase) {
		this.id = purchase.getId().toString();
		this.description = purchase.getDescription();
		this.date = purchase.getDate().toString();
		this.originalAmount = purchase.getAmount().toString();
	}
	
	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;

		BigDecimal rate = new BigDecimal(exchangeRate);
		BigDecimal original = new BigDecimal(originalAmount);
		BigDecimal converted = original.multiply(rate).setScale(2, RoundingMode.HALF_UP);
		
		convertedAmount = converted.toPlainString();
	}
	
}
