package com.gazorpazorp.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gazorpazorp.service.PaymentService;

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
	
	
}
