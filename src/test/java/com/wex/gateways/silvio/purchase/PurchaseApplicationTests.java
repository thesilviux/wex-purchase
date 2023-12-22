package com.wex.gateways.silvio.purchase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.*;


//import org.junit.jup.RunWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wex.gateways.silvio.purchase.controller.PurchaseController;
import com.wex.gateways.silvio.purchase.dto.PurchaseCreateRequest;
import com.wex.gateways.silvio.purchase.service.PurchaseService;
import com.wex.gateways.silvio.purchase.service.ExchangeRateService;

import jakarta.servlet.RequestDispatcher;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PurchaseApplicationTests {

	  @Autowired
	  private MockMvc mockMvc;

	  @Autowired
	  private ObjectMapper objectMapper;
	  
	  @InjectMocks
	  private PurchaseService purchaseService;
	  
	  @InjectMocks
	  private ExchangeRateService treasuryService;
	  
	  @Test
	  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = ""
			  	+ "DELETE FROM purchase WHERE id=1")
	  void whenStoreValidInput_thenReturns201Created() throws Exception {
		 PurchaseCreateRequest purchase = new PurchaseCreateRequest(
			 "Test description",
			 "2023-12-15",
			 "1.23");
	    
	     mockMvc.perform(post("/purchase")
	         .contentType("application/json")
	         .content(objectMapper.writeValueAsString(purchase)))
	         .andExpect(status().isCreated());
	  }
	  
	  @Test
	  void whenStoreInvalidInput_thenReturns400BadRequest() throws Exception {
		 PurchaseCreateRequest purchase = new PurchaseCreateRequest("","","");
	    
	     mockMvc.perform(post("/purchase")
	          .contentType("application/json")
	          .content(objectMapper.writeValueAsString(purchase)))
	          .andExpect(status().isBadRequest());
	  }

	  
	  @Test
	  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, statements = ""
	  	+ "INSERT INTO purchase(id, amount, date, description) VALUES (1, 1.15, '2023-09-30', 'Teste transaction')")
	  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = ""
	  	+ "DELETE FROM purchase WHERE id=1")
	  void whenRetrieveValidInput_thenReturns200Ok() throws Exception {
	    
	     mockMvc.perform(get("/purchase/{id}", 1)
	    		 .param("countryCurrency", "Canada-Dollar")
	    	)
	        .andExpect(status().isOk());
	  }

}