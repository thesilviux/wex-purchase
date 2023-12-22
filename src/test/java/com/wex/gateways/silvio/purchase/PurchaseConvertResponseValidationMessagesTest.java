package com.wex.gateways.silvio.purchase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.aspectj.lang.annotation.Before;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wex.gateways.silvio.purchase.dto.ExchangeRateDTO;
import com.wex.gateways.silvio.purchase.exception.CurrencyDoesntExistException;
import com.wex.gateways.silvio.purchase.exception.ExchangeRateNotFoundException;
import com.wex.gateways.silvio.purchase.model.Purchase;
import com.wex.gateways.silvio.purchase.repository.PurchaseRepository;
import com.wex.gateways.silvio.purchase.service.PurchaseService;
import com.wex.gateways.silvio.purchase.service.ExchangeRateService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PurchaseConvertResponseValidationMessagesTest {

	  @Autowired
	  private MockMvc mockMvc;

	  @InjectMocks
	  private PurchaseService purchaseService;
	  
	  @InjectMocks
	  private ExchangeRateService treasuryService;
	  
	  @Test()
	  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, statements = ""
	  	+ "INSERT INTO purchase(id, amount, date, description) VALUES (1, 1.15, '2023-09-30', 'Teste transaction')")
	  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = ""
	  	+ "DELETE FROM purchase WHERE id=1")
	  void whenEverithingIsOK_thenReturnExpectedValues() throws Exception {
		  
		  String countryCurrencyDescription = "Canada-Dollar";
		  String expectedJson = """
			 {
			     "id": "1",
			     "description": "Teste transaction",
			     "date": "2023-09-30",
			     "originalAmount": "1.15",
			     "exchangeRate": "1.343",
			     "convertedAmount": "1.54"
			 }
			 """;
		  
		  mockMvc.perform(get("/purchase/{id}", 1)
			  .param("countryCurrency", countryCurrencyDescription))
	          .andExpect(status().isOk())
	          .andExpect(content().json(expectedJson))
	          ;
		  
		  System.out.println(expectedJson);
	  }
	  
	  @Test()
	  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, statements = ""
	  	+ "INSERT INTO purchase(id, amount, date, description) VALUES (1, 1.15, '2023-09-30', 'Teste transaction')")
	  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = ""
	  	+ "DELETE FROM purchase WHERE id=1")
	  void whenCurrencyDoesntExist_thenValidatesWithErrorMessage() throws Exception {
		  
		  String countryCurrencyDescription = "Canada-Libra";
		  String expectedMessage = ""
			  + "Country-Currency '"
			  + countryCurrencyDescription
			  + "' does not exist";
		  
		  mockMvc.perform(get("/purchase/{id}", 1)
			  .param("countryCurrency", countryCurrencyDescription))
	          .andExpect(status().isBadRequest())
	          .andExpect(content().string(expectedMessage));
		  
		  System.out.println(expectedMessage);
	  }
	  
	  @Test()
	  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, statements = ""
	  	+ "INSERT INTO purchase(id, amount, date, description) VALUES (2, 1.15, '2019-03-31', 'Teste transaction')")
	  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = ""
	  	+ "DELETE FROM purchase WHERE id=2")
	  void whenExchangeRateNotFound_thenValidatesWithErrorMessage() throws Exception {
		  
		  String expectedMessage = "Exchange rate not found in period";
		  
		  mockMvc.perform(get("/purchase/{id}", 2)
			  .param("countryCurrency", "Canada-Dollar"))
	          .andExpect(status().isNotFound())
	          .andExpect(content().string(expectedMessage));
		  
		  
		  System.out.println(expectedMessage);
	  }
	  
	  @Test()
	  void whenPurchaseNotFound_thenValidatesWithErrorMessage() throws Exception {
		  
		  String id = "3";
		  String expectedMessage = "Purchase with id '" + id + "' not found";
		  
		  mockMvc.perform(get("/purchase/{id}", id)
			  .param("countryCurrency", "Canada-Dollar"))
	          .andExpect(status().isNotFound())
	          .andExpect(content().string(expectedMessage));
		  
		  
		  
		  System.out.println(expectedMessage);
	  }

}