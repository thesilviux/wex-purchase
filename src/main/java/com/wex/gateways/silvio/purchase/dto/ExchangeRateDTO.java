package com.wex.gateways.silvio.purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ExchangeRateDTO {

	private String countryCurrencyDesc;
	private String exchangeRate;
	private String recordDate;
}
