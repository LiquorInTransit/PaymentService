package com.gazorpazorp.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gazorpazorp.client.AccountClient;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.EphemeralKey;
import com.stripe.net.RequestOptions;

@Service
public class PaymentService {
	@Value("stripe.key")
	String apiKey;
	
	@Autowired
	AccountClient accountClient;

	@SuppressWarnings("unused")public String getKey(String version) {
		
		String v = Stripe.VERSION;
		RequestOptions reqopt = (new RequestOptions.RequestOptionsBuilder())
								.setApiKey(apiKey)
								.setStripeVersion(version)
								.build();
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
}
