package com.gazorpazorp.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gazorpazorp.service.PaymentService;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	
	@Autowired
	PaymentService paymentService;	

	@PostMapping("/key")
	@PreAuthorize("#oauth2.hasScope('customer')")
	public ResponseEntity<String> getKey (@RequestParam("api_version") String version) {
		return Optional.ofNullable(paymentService.getKey(version))
				.map(e -> new ResponseEntity<String>(e, HttpStatus.OK))
				.orElse(new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR));
	}
	
	
	
	@PostMapping("/processPayment")
	@PreAuthorize("#oauth2.hasScope('system')")
	public ResponseEntity processPayment (@RequestParam String customerId, @RequestParam String driverId, @RequestParam Long orderId, @RequestParam Integer amount) {
		return Optional.ofNullable(paymentService.processPayment(customerId, driverId, orderId, amount))
				.map(e -> new ResponseEntity(e))
				.orElse(new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR));
	}
	
}
