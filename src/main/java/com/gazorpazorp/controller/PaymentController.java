package com.gazorpazorp.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gazorpazorp.model.Sample;
import com.gazorpazorp.model.dto.SampleMinimalDto;
import com.gazorpazorp.model.dtoMapper.SampleMapper;
import com.gazorpazorp.service.SampleService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	
	@Autowired
	PaymentService paymentService;
	
	@GetMapping
	public ResponseEntity<List<SampleMinimalDto>> getAll() throws Exception{
		return Optional.ofNullable(paymentService.getAllSamples())
				.map(s -> new ResponseEntity<List<SampleMinimalDto>>
						(s.stream()
						.map(sample -> SampleMapper.INSTANCE.sampleToSampleMinimalDto(sample))
						.collect(Collectors.toList()), HttpStatus.OK)
					)
				.orElseThrow(() -> new Exception("Account does not exist"));
	}
	


	
	
}
