package com.gazorpazorp.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
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
import com.stripe.model.Account;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;

@Service
public class TestService {

	@Value("${stripe.secret-key}")
	String secretKey;
	@Value("${stripe.public-key}")
	String publicKey;

	@Autowired
	AccountClient accountClient;

	public void createNewAccount(HttpServletRequest req) {
		RequestOptions reqopt = (new RequestOptions.RequestOptionsBuilder())
				.setApiKey(secretKey)
				.build();

		try {
			Map<String, Object> params = new HashMap<>();
			params.put("type", "custom");
			params.put("country", "CA");
			params.put("legal_entity[type]", "individual");
			params.put("legal_entity[first_name]", "John");
			params.put("legal_entity[last_name]", "Smith");
			params.put("legal_entity[address][city]", "Oakville");
			params.put("legal_entity[dob][day]", 6);
			params.put("legal_entity[dob][month]", 6);
			params.put("legal_entity[dob][year]", 1966);
			Integer now = (int) (System.currentTimeMillis()/1000L);
			params.put("tos_acceptance[date]", now);
			params.put("tos_acceptance[ip]", req.getRemoteAddr());			
			Account account = Account.create(params, reqopt);
			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testCustomerPaymentToAccount () throws Exception {
		RequestOptions reqopt = (new RequestOptions.RequestOptionsBuilder())
				.setApiKey(secretKey)
				.build();
		
		Stripe.apiKey=secretKey;

		String id = accountClient.getCustomer().getStripeId();

		com.stripe.model.Customer cust;

		cust = com.stripe.model.Customer.retrieve(id, reqopt);
		Map<String, Object> params = new HashMap<>();
		params.put("amount", 15000);
		params.put("currency", "cad");
		params.put("description", "Example charge");
		params.put("customer", cust.getId());
		params.put("source", cust.getDefaultSource());
		params.put("destination", "acct_1BgRGKJHPGq8a2JD");
		params.put("application_fee", 800);
		params.put("capture", "false");
		Map<String, String> initialMetadata = new HashMap<String, String>();
		initialMetadata.put("order_id", "6735");
		params.put("metadata", initialMetadata);
		Charge charge = Charge.create(params, reqopt);
		Charge capped = Charge.retrieve(charge.getId()).capture();
	}

	public Integer test() {
		System.out.println("API KEY: "+secretKey);
		RequestOptions reqopt = (new RequestOptions.RequestOptionsBuilder())
				.setApiKey(secretKey)
				.build();

		String id = accountClient.getCustomer().getStripeId();

		com.stripe.model.Customer cust;
		try {
			cust = com.stripe.model.Customer.retrieve(id, reqopt);
			Map<String, Object> params = new HashMap<>();
			params.put("amount", 1000);
			params.put("currency", "cad");
			params.put("description", "Example charge");
			params.put("customer", cust.getId());
			params.put("source", cust.getDefaultSource());
			Map<String, String> initialMetadata = new HashMap<String, String>();
			initialMetadata.put("order_id", "6735");
			params.put("metadata", initialMetadata);
			Charge charge = Charge.create(params, reqopt);
			return HttpStatus.OK.value();
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException
				| APIException e) {
			e.printStackTrace();
			return null;
		}	
	}
}
