package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.model.authentication.EmailRequest;
import com.example.myhouse24admin.model.authentication.ForgotPasswordRequest;
import com.example.myhouse24admin.service.MailService;
import com.example.myhouse24admin.service.PasswordResetTokenService;
import com.example.myhouse24admin.service.StaffService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AuthenticationController {
    private final PasswordResetTokenService passwordResetTokenService;
    private final MailService mailService;

    public AuthenticationController(PasswordResetTokenService passwordResetTokenService, MailService mailService) {
        this.passwordResetTokenService = passwordResetTokenService;
        this.mailService = mailService;
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
    public @ResponseBody ResponseEntity<?> sendPasswordResetToken(@Valid EmailRequest emailRequest,
                                                                  HttpServletRequest httpServletRequest) {
        String token = passwordResetTokenService.createOrUpdatePasswordResetToken(emailRequest);
        mailService.sendToken(token,emailRequest, String.valueOf(httpServletRequest.getRequestURL()));
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/sentToken")
    public ModelAndView getSentTokenPage() {
        return new ModelAndView("security/sentToken");
    }
    @GetMapping("/changePassword")
    public ModelAndView changePassword(@RequestParam("token")String token){
        if(passwordResetTokenService.isPasswordResetTokenValid(token)){
            ModelAndView modelAndView = new ModelAndView("security/changePassword");
            modelAndView.addObject("token", token);
            return modelAndView;
        } else {
            return new ModelAndView("security/tokenExpired");
        }
    }

    @PostMapping("/changePassword")
    public @ResponseBody ResponseEntity<?> setNewPassword(@RequestParam("token")String token,@Valid @ModelAttribute ForgotPasswordRequest forgotPasswordRequest){
        if(passwordResetTokenService.isPasswordResetTokenValid(token)){
            passwordResetTokenService.updatePassword(token, forgotPasswordRequest.password());
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


}
