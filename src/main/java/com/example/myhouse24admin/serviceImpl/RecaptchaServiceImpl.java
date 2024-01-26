package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.model.authentication.RecaptchaResponse;
import com.example.myhouse24admin.service.RecaptchaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaServiceImpl implements RecaptchaService {
    @Value("${google.recaptcha.key.secret}")
    private String secretKey;
    private String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";
    private final Logger logger = LogManager.getLogger("serviceLogger");
    private final RestTemplate restTemplate;

    public RecaptchaServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isRecaptchaValid(String recaptcha) {
        logger.info("isRecaptchaValid() - Checking if recaptcha "+recaptcha+" is valid");
        RecaptchaResponse recaptchaResponse = sendCaptchaForVerifying(recaptcha);
        logger.info("isRecaptchaValid() - Recaptcha was checked");
        return recaptchaResponse.success();
    }

    RecaptchaResponse sendCaptchaForVerifying(String recaptcha) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        map.add("secret", secretKey);
        map.add("response",recaptcha);

        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(map,headers);
        ResponseEntity<RecaptchaResponse> response = restTemplate.exchange(verifyUrl,
                HttpMethod.POST,
                entity,
                RecaptchaResponse.class);

        return response.getBody();
    }

}
