package com.example.controller;

import com.example.entity.ServicePageBlock;
import com.example.service.ServicePageService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/web-site/services")
public class ServicePageController {
    private final ServicePageService servicePageService;

    public ServicePageController(ServicePageService servicePageService) {
        this.servicePageService = servicePageService;
    }
    @GetMapping("")
    public ModelAndView getServicePage() {
        return new ModelAndView("service/service");
    }
    @GetMapping("/get")
    public @ResponseBody Page<ServicePageBlock> getServicePageBlocks(@RequestParam("page") int page,
                                                                     @RequestParam("pageSize") int pageSize) {

        return servicePageService.getServicePageBlocks(page, pageSize);
    }
}
