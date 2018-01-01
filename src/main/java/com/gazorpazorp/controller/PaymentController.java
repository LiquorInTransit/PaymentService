package com.gazorpazorp.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gazorpazorp.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	
	@Autowired
	PaymentService paymentService;
	
	@GetMapping
	public ResponseEntity getAll() throws Exception{
		return Optional.ofNullable(paymentService.getAllSamples())
				.map(s -> new ResponseEntity(s, HttpStatus.OK))
				.orElseThrow(() -> new Exception("Account does not exist"));
	}
	


	
	
}
