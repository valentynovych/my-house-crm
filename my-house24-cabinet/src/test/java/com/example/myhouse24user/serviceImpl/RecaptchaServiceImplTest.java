package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.model.authentication.RecaptchaResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
@ExtendWith(MockitoExtension.class)
class RecaptchaServiceImplTest {
    @Mock
    private RestTemplate restTemplate;
    @Value("${google.recaptcha.key.secret}")
    private String secretKey;
    private String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";
    @InjectMocks
    private RecaptchaServiceImpl recaptchaService;
    @Test
    void isRecaptchaValid() {
        RecaptchaResponse recaptchaResponse = new RecaptchaResponse(true, "ch", "host", new String[0]);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        map.add("secret", secretKey);
        map.add("response","recaptcha");
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(map,headers);

        when(restTemplate.exchange(verifyUrl,
                HttpMethod.POST,
                entity,
                RecaptchaResponse.class)).thenReturn(new ResponseEntity<>(recaptchaResponse, HttpStatus.OK));

        boolean isValid = recaptchaService.isRecaptchaValid("recaptcha");
        assertThat(isValid).isTrue();

        verify(restTemplate, times(1)).exchange(verifyUrl,
                HttpMethod.POST,
                entity,
                RecaptchaResponse.class);
        verifyNoMoreInteractions(restTemplate);
    }
}