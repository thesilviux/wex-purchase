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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wex.gateways.silvio.purchase.controller.PurchaseController;
import com.wex.gateways.silvio.purchase.dto.PurchaseCreateRequest;
import com.wex.gateways.silvio.purchase.service.PurchaseService;

import jakarta.servlet.RequestDispatcher;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PurchaseCreateRequestValidationMessagesTest {

	  @Autowired
	  private MockMvc mockMvc;

	  @Autowired
	  private ObjectMapper objectMapper;
	  
	  @InjectMocks
	  private PurchaseService purchaseService;
	  
	  @Test
	  void whenDescriptionIsEmpty_thenValidatesWithErrorMessage() throws Exception {
		 PurchaseCreateRequest purchase = new PurchaseCreateRequest(
			 "",
			 "2023-12-15",
			 "1.11");
	    
		  mockMvc.perform(post("/purchase")
	          .contentType("application/json")
	          .content(objectMapper.writeValueAsString(purchase)))
	          .andExpect( status().isBadRequest() )
	          .andExpect(jsonPath("$.errors[0].field").value("description"))
	          .andExpect(jsonPath("$.errors[0].defaultMessage")
        		  .value("The description must not be empty or blank"))
          ;
		  System.out.println("<EMPTY> - The description must not be empty or blank");
	  }
	  
	  @Test()
	  void whenDescriptionIsLongerThanMax_thenValidatesWithErrorMessage() throws Exception {
		 PurchaseCreateRequest purchase = new PurchaseCreateRequest(""
		 		+ "Test description Test description Test description "
		 		+ "Test description Test description Test description "
		 		+ "Test description Test description Test description ",
			 "2023-12-15",
			 "1.11");
	    
		  mockMvc.perform(post("/purchase")
	          .contentType("application/json")
	          .content(objectMapper.writeValueAsString(purchase)))
	          .andExpect( status().isBadRequest() )
	          .andExpect(jsonPath("$.errors[0].field").value("description"))
	          .andExpect(jsonPath("$.errors[0].defaultMessage")
        		  .value("The description must be a maximum of 50 characters"))
	      ;

		  System.out.println("<OUT_OF_BOUNDS> - The description must be a maximum of 50 characters");
	  }
	  
	  @Test
	  void whenDateIsEmpty_thenValidatesWithErrorMessage() throws Exception {
		 PurchaseCreateRequest purchase = new PurchaseCreateRequest(
			 "Test description",
			 "",
			 "1.11");
	    
		  mockMvc.perform(post("/purchase")
	          .contentType("application/json")
	          .content(objectMapper.writeValueAsString(purchase)))
	          .andExpect( status().isBadRequest() )
	          .andExpect(jsonPath("$.errors[0].field").value("date"))
	          .andExpect(jsonPath("$.errors[0].defaultMessage")
        		  .value("The date must be in format YYYY-MM-DD"))
	          ;
		  System.out.println("<EMPTY> - The date must be in format YYYY-MM-DD");
	  }
	  
	  @Test
	  void whenDateIsWrongFormat_thenValidatesWithErrorMessage() throws Exception {
		 PurchaseCreateRequest purchase = new PurchaseCreateRequest(
			 "Test description",
			 "2023-12-XX",
			 "1.11");
	    
		  mockMvc.perform(post("/purchase")
	          .contentType("application/json")
	          .content(objectMapper.writeValueAsString(purchase)))
	          .andExpect( status().isBadRequest() )
	          .andExpect(jsonPath("$.errors[0].field").value("date"))
	          .andExpect(jsonPath("$.errors[0].defaultMessage")
        		  .value("The date must be in format YYYY-MM-DD"))
	          ;
		  System.out.println("<WRONG_FORMAT> - The date must be in format YYYY-MM-DD");
	  }
	  
	  @Test
	  void whenAmountIsEmpty_thenValidatesWithErrorMessage() throws Exception {
		 PurchaseCreateRequest purchase = new PurchaseCreateRequest(
			 "Test description",
			 "2023-12-15",
			 "");

		  String errorsByField = "$.errors[?(@.field == '%s')]";
		  String errorsByDefaultMessage = "$.errors[?(@.defaultMessage == '%s')]";
			
		  mockMvc.perform(post("/purchase")
	          .contentType("application/json")
	          .content(objectMapper.writeValueAsString(purchase)))
	          .andExpect( status().isBadRequest() )
	          .andExpect(jsonPath(errorsByField, "amount").exists())
	          .andExpect(jsonPath(errorsByDefaultMessage,
        		  "The amount must be a positive decimal value greater than 0.00")
        		  .exists()
        		  )
	          ;
		  System.out.println("<EMPTY> - The amount must be a positive decimal value greater than 0.00");
	  }
	  
	  @Test
	  void whenAmountIsNegative_thenValidatesWithErrorMessage() throws Exception {
		 PurchaseCreateRequest purchase = new PurchaseCreateRequest(
			 "Test description",
			 "2023-12-15",
			 "-1.11");

		  String errorsByDefaultMessage = "$.errors[?(@.defaultMessage == '%s')]";
			
		  mockMvc.perform(post("/purchase")
	          .contentType("application/json")
	          .content(objectMapper.writeValueAsString(purchase)))
	          .andExpect( status().isBadRequest() )
	          //.andExpect(jsonPath("$.errors[0].field").value("amount"))
	          .andExpect(jsonPath(errorsByDefaultMessage,
        		  "The amount must be a positive decimal value greater than 0.00")
        		  .exists()
        		  )
	          ;
		  System.out.println("<NEGATIVE> - The amount must be a positive decimal value greater than 0.00");
	  }
	  
	  @Test
	  void whenAmountIsOneDecimalDigit_thenItIsOK() throws Exception {
		 PurchaseCreateRequest purchase = new PurchaseCreateRequest(
			 "Test description",
			 "2023-12-15",
			 "1.1");
	    
		  mockMvc.perform(post("/purchase")
	          .contentType("application/json")
	          .content(objectMapper.writeValueAsString(purchase)))
	          .andExpect( status().isCreated() );
		  System.out.println("<ONE_DECIMAL> - OK 201 Created");
	  }
	  
	  @Test
	  void whenAmountIsOutOfBoundsFormat_thenValidatesWithErrorMessage() throws Exception {
		 PurchaseCreateRequest purchase = new PurchaseCreateRequest(
			 "Test description",
			 "2023-12-15",
			 "1.111");
	    
		  mockMvc.perform(post("/purchase")
	          .contentType("application/json")
	          .content(objectMapper.writeValueAsString(purchase)))
	          .andExpect( status().isBadRequest() )
	          .andExpect(jsonPath("$.errors[0].field").value("amount"))
	          .andExpect(jsonPath("$.errors[0].defaultMessage")
        		  .value("The amount is out of bounds (9 digits . 2 digits)"))
	          ;
		  System.out.println("<OUT_OF_BOUNDS> - The amount is out of bounds (9 digits . 2 digits)");
	  }

}