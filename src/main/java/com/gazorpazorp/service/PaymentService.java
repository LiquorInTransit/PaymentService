package com.gazorpazorp.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.gazorpazorp.client.AccountClient;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.ChargeOutcome;
import com.stripe.model.EphemeralKey;
import com.stripe.net.RequestOptions;


@Service
public class PaymentService {
	@Value("${stripe.secret-key}")
	String secretKey;
	@Value("${stripe.public-key}")
	String publicKey;
	
	@Autowired
	AccountClient accountClient;
	
	Logger logger = LoggerFactory.getLogger(PaymentService.class);

	@SuppressWarnings("unused")
	public String getKey(String version) {
		RequestOptions reqopt = (new RequestOptions.RequestOptionsBuilder())
								.setApiKey(secretKey)
								.setStripeVersion(version)
								.build();
		logger.warn("Stripe API Version: " + version);
		Map<String, Object> options = new HashMap<>();
		options.put("customer", accountClient.getCustomer().getStripeId());
		
		try {
			EphemeralKey key = EphemeralKey.create(options, reqopt);
			return key.getRawJson();
		} catch (StripeException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public HttpStatus processPayment (String customerId, String driverId, Long orderId, Integer amount) {
		RequestOptions reqopt = (new RequestOptions.RequestOptionsBuilder())
				.setApiKey(secretKey)
				.build();
		
		Stripe.apiKey=secretKey;

		com.stripe.model.Customer cust;
		try {
			cust = com.stripe.model.Customer.retrieve(customerId, reqopt);
			Map<String, Object> params = new HashMap<>();
			params.put("amount", amount);
			params.put("currency", "cad");
			params.put("description", "Example charge");
			params.put("customer", cust.getId());
			String source = cust.getDefaultSource();
			if (source == null)
				return HttpStatus.BAD_REQUEST;
			params.put("source", source);
			params.put("destination", driverId);
			params.put("application_fee", 500);
			params.put("capture", "false");
			Map<String, String> initialMetadata = new HashMap<String, String>();
			initialMetadata.put("order_id", "6735");
			params.put("metadata", initialMetadata);
			Charge charge = Charge.create(params, reqopt);
			if (determineHttpStatus(charge.getOutcome())==HttpStatus.OK) {
				Charge capped = Charge.retrieve(charge.getId()).capture();
				return determineHttpStatus(capped.getOutcome());
			} else {
				return determineHttpStatus(charge.getOutcome());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
//	public HttpStatus processPayment (String customerId, Long orderId, Integer amount) {
//		RequestOptions reqopt = (new RequestOptions.RequestOptionsBuilder())
//				.setApiKey(secretKey)
//				.build();
//		com.stripe.model.Customer cust;
//		try {
//			cust = com.stripe.model.Customer.retrieve(customerId, reqopt);
//			Map<String, Object> params = new HashMap<>();
//			params.put("amount", amount);
//			params.put("currency", "cad");
//			params.put("description", "Example charge");
//			params.put("customer", cust.getId());
//			params.put("source", cust.getDefaultSource());
//			Map<String, String> initialMetadata = new HashMap<String, String>();
//			initialMetadata.put("order_id", String.valueOf(orderId));
//			params.put("metadata", initialMetadata);
//			Charge charge = Charge.create(params, reqopt);
//			return determineHttpStatus(charge.getOutcome());
//		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException
//				| APIException e) {
//			e.printStackTrace();
//			return null;
//		}	
//	}
	
	private HttpStatus determineHttpStatus(ChargeOutcome outcome) {
		System.out.println(outcome.getReason());
		if ("".equals(outcome.getReason()) || outcome.getReason()==null)
			return HttpStatus.OK;
		switch (outcome.getReason()) {
		case "expired_card":
			return HttpStatus.REQUEST_TIMEOUT;
		case "card_not_supported":
			return HttpStatus.FAILED_DEPENDENCY;
		case "invalid_cvc":
			return HttpStatus.FORBIDDEN;	
		default:
			return HttpStatus.GONE;
		}
	}
	
}
