package com.gazorpazorp.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gazorpazorp.service.TestService;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;

@RestController
@RequestMapping("/api/test")
public class TestController {
	
	@Autowired
	TestService testService;
	
	@PostMapping("/accounts")
	public ResponseEntity createConnectedAccount(HttpServletRequest req) {
		testService.createNewAccount(req);
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@PostMapping("/pay")
	public ResponseEntity payFromCustomerToAccount() throws Exception {
		testService.testCustomerPaymentToAccount();
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@PostMapping("/testPayment")
	public Integer test	() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		return Optional.ofNullable(testService.test())
				.orElse(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}
}
