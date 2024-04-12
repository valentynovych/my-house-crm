package com.example.controller;

import com.example.model.aboutPage.AboutPageResponse;
import com.example.model.mainPage.MainPageResponse;
import com.example.service.AboutPageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequestMapping("/about-us")
public class AboutPageController {
    private final AboutPageService aboutPageService;

    public AboutPageController(AboutPageService aboutPageService) {
        this.aboutPageService = aboutPageService;
    }

    @GetMapping("")
    public ModelAndView getAboutPage() {
        return new ModelAndView("about-us/about-us");
    }
    @GetMapping("/get")
    public @ResponseBody AboutPageResponse getAboutPageResponse() {
        return aboutPageService.getAboutPageResponse();
    }
    @GetMapping("/download/{document}")
    public @ResponseBody ResponseEntity<byte[]> downloadDocument(@PathVariable String document) throws UnsupportedEncodingException {
        byte[] file = aboutPageService.getDocument(document);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename="+ URLEncoder.encode(document, "UTF-8"))
                .body(file);
    }

}
