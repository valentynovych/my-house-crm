package com.example.myhouse24user.controller;

import com.example.myhouse24user.model.tariff.TariffResponse;
import com.example.myhouse24user.service.TariffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("cabinet/tariffs")
public class TariffController {

    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping("{apartmentId}")
    public ModelAndView viewTariff(@PathVariable("apartmentId") Long apartmentId) {
        return new ModelAndView("tariffs/tariff");
    }

    @GetMapping("get-apartment-tariff/{apartmentId}")
    public ResponseEntity<?> getApartmentTariff(@PathVariable("apartmentId") Long apartmentId) {
        TariffResponse response = tariffService.getApartmentTariff(apartmentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
