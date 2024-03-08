package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.entity.OwnerStatus;
import com.example.myhouse24admin.model.apartmentOwner.*;
import com.example.myhouse24admin.model.meterReadings.HouseNameResponse;
import com.example.myhouse24admin.model.meterReadings.SelectSearchRequest;
import com.example.myhouse24admin.service.ApartmentOwnerService;
import com.example.myhouse24admin.service.HouseService;
import com.example.myhouse24admin.service.MailService;
import com.example.myhouse24admin.service.OwnerPasswordResetTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
    private final OwnerPasswordResetTokenService ownerPasswordResetTokenService;
    private final HouseService houseService;
    private final MailService mailService;

    public ApartmentOwnerController(ApartmentOwnerService apartmentOwnerService,
                                    OwnerPasswordResetTokenService ownerPasswordResetTokenService,
                                    HouseService houseService,
                                    MailService mailService) {
        this.apartmentOwnerService = apartmentOwnerService;
        this.ownerPasswordResetTokenService = ownerPasswordResetTokenService;
        this.houseService = houseService;
        this.mailService = mailService;
    }

    @GetMapping()
    public ModelAndView getOwnersPage() {
        return new ModelAndView("owners/owners");
    }

    @GetMapping("/getOwners")
    public @ResponseBody Page<TableApartmentOwnerResponse> getOwners(@RequestParam(name = "page") int page,
                                                                     @RequestParam(name = "pageSize") int pageSize,
                                                                     FilterRequest filterRequest) {
        return apartmentOwnerService.getApartmentOwnerResponsesForTable(page, pageSize, filterRequest);
    }

    @GetMapping("/add")
    public ModelAndView getOwnerPageForCreate() {
        ModelAndView modelAndView = new ModelAndView("owners/owner");
        modelAndView.addObject("statusLink", "get-statuses");
        return modelAndView;
    }

    @GetMapping("/get-statuses")
    public @ResponseBody OwnerStatus[] getOwnerStatuses() {
        return OwnerStatus.values();
    }

    @PostMapping("/add")
    public @ResponseBody ResponseEntity<?> createOwner(@Valid @ModelAttribute CreateApartmentOwnerRequest createApartmentOwnerRequest,
                                                       @RequestParam(name = "avatar", required = false) MultipartFile avatar,
                                                       HttpServletRequest request) {
        apartmentOwnerService.createApartmentOwner(createApartmentOwnerRequest, avatar);
        String url = request.getRequestURL().toString();
        int index = url.lastIndexOf("/");
        String returnUrl = url.substring(0, index);
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
    public @ResponseBody ResponseEntity<?> updateOwner(@PathVariable Long id,
                                                       @Valid @ModelAttribute
                                                       EditApartmentOwnerRequest editApartmentOwnerRequest,
                                                       @RequestParam(name = "avatar", required = false)
                                                       MultipartFile avatar,
                                                       HttpServletRequest request) {
        apartmentOwnerService.updateApartmentOwner(editApartmentOwnerRequest, id, avatar);
        String url = request.getRequestURL().toString();
        int index = url.lastIndexOf("/");
        url = url.substring(0, index - 5);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @GetMapping("/delete/{id}")
    public @ResponseBody ResponseEntity<?> deleteOwner(@PathVariable Long id) {
        boolean deleted = apartmentOwnerService.deleteOwnerById(id);
        if(deleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/view-owner/{id}")
    public ModelAndView getViewOwnerPage() {
        return new ModelAndView("owners/view-owner");
    }

    @GetMapping("/view-owner/get/{id}")
    public @ResponseBody ViewApartmentOwnerResponse getViewOwner(@PathVariable Long id) {
        return apartmentOwnerService.getApartmentOwnerResponseForView(id);
    }

    @GetMapping("get-owners")
    public @ResponseBody ResponseEntity<?> getOwnersShortDetails(@RequestParam int page,
                                                                 @RequestParam int pageSize,
                                                                 @RequestParam String fullName) {
        Page<ApartmentOwnerShortResponse> responsePage =
                apartmentOwnerService.getShortResponseOwners(page, pageSize, fullName);
        return new ResponseEntity<>(responsePage, HttpStatus.OK);
    }
    @GetMapping("/get-houses")
    public @ResponseBody Page<HouseNameResponse> getHouses(SelectSearchRequest selectSearchRequest) {
        return houseService.getHousesForSelect(selectSearchRequest);
    }
    @PostMapping("/send-activation/{id}")
    public @ResponseBody ResponseEntity<?> sendActivation(@PathVariable Long id) {
        String token = ownerPasswordResetTokenService.createOrUpdatePasswordResetToken(id);
        mailService.sendActivationToOwner(token, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
