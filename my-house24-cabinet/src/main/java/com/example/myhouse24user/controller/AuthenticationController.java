package com.example.myhouse24user.controller;

import com.example.myhouse24user.entity.ApartmentOwner;
import com.example.myhouse24user.model.authentication.EmailRequest;
import com.example.myhouse24user.model.authentication.ForgotPasswordRequest;
import com.example.myhouse24user.model.authentication.RegistrationRequest;
import com.example.myhouse24user.service.ApartmentOwnerService;
import com.example.myhouse24user.service.MailService;
import com.example.myhouse24user.service.OwnerPasswordResetTokenService;
import com.example.myhouse24user.service.RecaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cabinet")
public class AuthenticationController {
    private final OwnerPasswordResetTokenService ownerPasswordResetTokenService;
    private final MailService mailService;
    private final ApartmentOwnerService apartmentOwnerService;
    private final RecaptchaService recaptchaService;

    public AuthenticationController(OwnerPasswordResetTokenService ownerPasswordResetTokenService,
                                    MailService mailService,
                                    ApartmentOwnerService apartmentOwnerService,
                                    RecaptchaService recaptchaService) {
        this.ownerPasswordResetTokenService = ownerPasswordResetTokenService;
        this.mailService = mailService;
        this.apartmentOwnerService = apartmentOwnerService;
        this.recaptchaService = recaptchaService;
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage() {
        return new ModelAndView("security/login");
    }
    @GetMapping("/forgotPassword")
    public ModelAndView getForgotPasswordPage() {
        return new ModelAndView("security/forgotPassword");
    }
    @PostMapping("/forgotPassword")
    public @ResponseBody ResponseEntity<?> sendPasswordResetToken(@Valid EmailRequest emailRequest) {
        String token = ownerPasswordResetTokenService.createOrUpdatePasswordResetToken(emailRequest);
        mailService.sendToken(token,emailRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/sentToken")
    public ModelAndView getSentTokenPage() {
        return new ModelAndView("security/sentToken");
    }
    @GetMapping("/changePassword")
    public ModelAndView changePassword(@RequestParam("token")String token){
        if(ownerPasswordResetTokenService.isPasswordResetTokenValid(token)){
            ModelAndView modelAndView = new ModelAndView("security/changePassword");
            modelAndView.addObject("token", token);
            return modelAndView;
        } else {
            return new ModelAndView("security/tokenExpired");
        }
    }

    @PostMapping("/changePassword")
    public @ResponseBody ResponseEntity<?> setNewPassword(@RequestParam("token")String token,@Valid @ModelAttribute ForgotPasswordRequest forgotPasswordRequest){
        if(ownerPasswordResetTokenService.isPasswordResetTokenValid(token)){
            ownerPasswordResetTokenService.updatePassword(token, forgotPasswordRequest.password());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    @GetMapping("/success")
    public ModelAndView getSuccessPage() {
        return new ModelAndView("security/success");
    }
    @GetMapping("/tokenExpired")
    public ModelAndView getTokenExpiredPage() {
        return new ModelAndView("security/tokenExpired");
    }
    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        return new ModelAndView("registration/registration");
    }
    @PostMapping("/register")
    public @ResponseBody ResponseEntity<?> registerOwner(@Valid @ModelAttribute RegistrationRequest registrationRequest,
                                                         @RequestParam("recaptcha")String recaptcha,
                                                         HttpServletRequest request) {
        if(recaptchaService.isRecaptchaValid(recaptcha)) {
            apartmentOwnerService.register(registrationRequest);
            String url = request.getRequestURL().toString();
            int index = url.lastIndexOf("/");
            String returnUrl = url.substring(0, index);
            return new ResponseEntity<>(returnUrl+"/login",HttpStatus.OK);
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("grecaptha","Доведіть що ви не робот");
            return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
        }
    }

}
