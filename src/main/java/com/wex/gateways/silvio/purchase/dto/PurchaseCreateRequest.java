package com.wex.gateways.silvio.purchase.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class PurchaseCreateRequest {
	
	@NotBlank( 
		message = "{validation.purchase.description.empty}")
	@Size( max = 50, 
		message = "{validation.purchase.description.size}")
	private String description;

	@Pattern( regexp = "\\d{4}-\\d{2}-\\d{2}", 
		message = "{validation.purchase.date.pattern}")
	private String date;

	@DecimalMin(value = "0.0", inclusive = false, 
		message = "{validation.purchase.amount.positive}")
    @Digits(integer=9, fraction=2, 
    		message = "{validation.purchase.amount.format}")
	private String amount;

}