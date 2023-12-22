package com.wex.gateways.silvio.purchase.exception;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@ControllerAdvice
public class ControllerAdvisor {
	
	@Getter
	@Setter
	class shortError{
		
		private String field;
		private String defaultMessage;
		
		public shortError(String field, String defaultMessage) {
			this.field = field;
			this.defaultMessage = defaultMessage;
		}
	}
	

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDate.now());
        
        List<shortError> fieldErrors =  ex.getFieldErrors().stream()
        		.map( error -> new shortError(error.getField(),error.getDefaultMessage()))
        		.collect(Collectors.toList());

        body.put("errors", fieldErrors );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    protected ResponseEntity<Object> handleHandlerMethodValidation(HandlerMethodValidationException ex) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDate.now());
        
        List<shortError> fieldErrors =  ex.getAllErrors().stream()
        		.map( error -> new shortError(
        				((DefaultMessageSourceResolvable) error.getArguments()[0]).getCode(),
        				error.getDefaultMessage()))
        		.collect(Collectors.toList());
        
        System.out.println(fieldErrors);

        body.put("errors", fieldErrors );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        
    }
    
    @ExceptionHandler(ExchangeRateNotFoundException.class)
    protected ResponseEntity<Object> handleExchangeRateNotFound( ExchangeRateNotFoundException ex){
    	return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(PurchaseNotFoundException.class)
    protected ResponseEntity<Object> handlePurchaseNotFound( PurchaseNotFoundException ex){
    	return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(CurrencyDoesntExistException.class)
    protected ResponseEntity<Object> handleCurrencyDoesntExist( CurrencyDoesntExistException ex){
    	return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

