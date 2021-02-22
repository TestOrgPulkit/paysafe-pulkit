package com.paysafe.assignment.controller;

import com.paysafe.assignment.entity.UserEntity;
import com.paysafe.assignment.entity.UserRepository;
import com.paysafe.assignment.model.RequestDetails;
import com.paysafe.assignment.model.Token;
import com.paysafe.assignment.model.SingleUseCustomerTokenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@CrossOrigin
@RestController
public class Controller {

    @Autowired
    private UserRepository userRepository;
    // paysafe's api endpoint for processing payments
    final String url = "https://api.test.paysafe.com/paymenthub/v1/payments";
    RestTemplate restTemplate = new RestTemplate();

    // rendering home page
    @GetMapping("/")
    public ModelAndView redirectToHome() {
        return new ModelAndView("home");
    }

    // processing payment
    @PostMapping("/payment")
    public HttpStatus paymentProcessing(@RequestBody RequestDetails requestDetails) {
        Token token = new Token(requestDetails.getPaymentHandleToken(), requestDetails.getMerchantRefNum(),
                requestDetails.getAmount(), requestDetails.getCurrencyCode());

        if (requestDetails.getCustomerOperation() != null && requestDetails.getCustomerOperation().equals("ADD")) {
            if (userRepository.findByEmail(requestDetails.getEmail()) == null) {
                // create a new merchant customer Id for a new customer
                String merchantCustomerId = "";
                do {
                    long number = ThreadLocalRandom.current().nextLong(1000000);
                    merchantCustomerId = "AlgoCaptainCustomer" + number;
                }
                while (userRepository.findByMerchantCustomerId(merchantCustomerId) != null);
                token.setMerchantCustomerId(merchantCustomerId);
            } else {
                // set the customer id field for a returning customer
                UserEntity queriedUserEntity = userRepository.findByEmail(requestDetails.getEmail());
                token.setCustomerId(queriedUserEntity.getCustomerId());
            }
        }
        // prepping up for api call
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic cHJpdmF0ZS03NzUxOkItcWEyLTAtNWYwMzFjZGQtMC0zMDJkMDIxNDQ5NmJlODQ3MzJhMDFmNjkwMjY4ZDNiOGViNzJlNWI4Y2NmOTRlMjIwMjE1MDA4NTkxMzExN2YyZTFhODUzMTUwNWVlOGNjZmM4ZTk4ZGYzY2YxNzQ4");

        List<MediaType> list = new ArrayList<MediaType>();
        list.add(MediaType.APPLICATION_JSON);
        headers.setAccept(list);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Token> request = new HttpEntity<Token>(token, headers);

        ResponseEntity<String> result = restTemplate.postForEntity(url, request, String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        UserEntity userEntity = new UserEntity();

        try {
            userEntity = objectMapper.readValue(result.getBody(), UserEntity.class);
        } catch (Exception e) {
            System.out.println(e);
        }
        //  save the created user entity only if save card flow has been requested
        if (requestDetails.getCustomerOperation() != null && requestDetails.getCustomerOperation().equals("ADD") && token.getMerchantCustomerId() != null) {
            userEntity.setEmail(requestDetails.getEmail());
            userRepository.save(userEntity);
        }

        return result.getStatusCode();
    }

    //below method generates SingleUseCustomerToken
    @PostMapping(path = "/token", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public SingleUseCustomerTokenRequest customerIdCheck(@RequestBody RequestDetails requestEmail) {
        String email = requestEmail.getEmail();
        if (userRepository.findByEmail(email) == null) {
            return null;
        } else {    //else block will fetch singleUseCustomerToken for our returning customer from paysafe's server, by using saved customer id
            String id = userRepository.findByEmail(email).getCustomerId();
            String url = "https://api.test.paysafe.com/paymenthub/v1/customers/" + id + "/singleusecustomertokens";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic cHJpdmF0ZS03NzUxOkItcWEyLTAtNWYwMzFjZGQtMC0zMDJkMDIxNDQ5NmJlODQ3MzJhMDFmNjkwMjY4ZDNiOGViNzJlNWI4Y2NmOTRlMjIwMjE1MDA4NTkxMzExN2YyZTFhODUzMTUwNWVlOGNjZmM4ZTk4ZGYzY2YxNzQ4");
            headers.add("Content-Type", "application/json");

            String body = "{ \"paymentTypes\": [\"CARD\"] }";

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> result = restTemplate.postForEntity(url, request, String.class);

            ObjectMapper objectMapper = new ObjectMapper();

            SingleUseCustomerTokenRequest singleUseCustomerTokenRequest = null;

            try {
                singleUseCustomerTokenRequest = objectMapper.readValue(result.getBody(), SingleUseCustomerTokenRequest.class);
            } catch (Exception e) {
                System.out.println(e);
            }

            return singleUseCustomerTokenRequest;
        }
    }
}
