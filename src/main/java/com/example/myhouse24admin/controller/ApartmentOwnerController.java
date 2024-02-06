package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.entity.OwnerStatus;
import com.example.myhouse24admin.model.apartmentOwner.CreateApartmentOwnerRequest;
import com.example.myhouse24admin.model.apartmentOwner.ApartmentOwnerResponse;
import com.example.myhouse24admin.model.apartmentOwner.EditApartmentOwnerRequest;
import com.example.myhouse24admin.service.ApartmentOwnerService;
import com.example.myhouse24admin.validators.fileExtensionValidation.ValidFileExtension;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/owners")
public class ApartmentOwnerController {
    private final ApartmentOwnerService apartmentOwnerService;

    public ApartmentOwnerController(ApartmentOwnerService apartmentOwnerService) {
        this.apartmentOwnerService = apartmentOwnerService;
    }

    @GetMapping()
    public ModelAndView getOwnersPage() {
        return new ModelAndView("owners/owners");
    }
    @GetMapping("/add")
    public ModelAndView getOwnerPageForCreate() {
        ModelAndView modelAndView = new ModelAndView("owners/owner");
        modelAndView.addObject("statusLink", "get-statuses");
        return modelAndView;
    }
    @GetMapping("/get-statuses")
    public @ResponseBody OwnerStatus[] getOwnerStatuses(){
        return OwnerStatus.values();
    }
    @PostMapping("/add")
    public @ResponseBody ResponseEntity<?> createOwner(@Valid @ModelAttribute CreateApartmentOwnerRequest createApartmentOwnerRequest,
                                                       @RequestParam(name = "avatar", required = false) MultipartFile avatar,
                                                       HttpServletRequest request){
        apartmentOwnerService.createApartmentOwner(createApartmentOwnerRequest, avatar);
        String url = request.getRequestURL().toString();
        int index = url.lastIndexOf("/");
        String returnUrl = url.substring(0,index);
        return new ResponseEntity<>(returnUrl, HttpStatus.OK);

    }
    @GetMapping("/edit/{id}")
    public ModelAndView getOwnerPageForEdit() {
        ModelAndView modelAndView = new ModelAndView("owners/owner");
        modelAndView.addObject("statusLink", "../get-statuses");
        return modelAndView;
    }
    @GetMapping("/edit/get-owner/{id}")
    public @ResponseBody ApartmentOwnerResponse getOwnerForEdit(@PathVariable Long id) {
        return apartmentOwnerService.getApartmentOwnerResponse(id);
    }

    @PostMapping("/edit/{id}")
    public @ResponseBody ResponseEntity<?> updateOwner(@PathVariable long id,
                                                       @Valid @ModelAttribute
                                                       EditApartmentOwnerRequest editApartmentOwnerRequest,
                                                       @RequestParam(name = "avatar", required = false)
                                                           MultipartFile avatar,
                                                       HttpServletRequest request) {
        apartmentOwnerService.updateApartmentOwner(editApartmentOwnerRequest,id,avatar);
        String url = request.getRequestURL().toString();
        int index = url.lastIndexOf("/");
        url = url.substring(0,index-4);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }


}
