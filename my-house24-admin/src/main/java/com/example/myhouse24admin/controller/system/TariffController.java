package com.example.myhouse24admin.controller.system;

import com.example.myhouse24admin.exception.TariffAlreadyUsedException;
import com.example.myhouse24admin.model.tariffs.TariffRequestWrap;
import com.example.myhouse24admin.model.tariffs.TariffResponse;
import com.example.myhouse24admin.service.TariffService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("admin/system-settings/tariffs")
public class TariffController {

    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping()
    public ModelAndView viewTariff() {
        return new ModelAndView("system/tariffs/tariffs");
    }

    @GetMapping("add")
    public ModelAndView viewAddTariff(ModelMap model,
                                      @ModelAttribute("copyTariff") TariffResponse copyTariff) {
        model.addAttribute("copyTariff", copyTariff);
        return new ModelAndView("system/tariffs/add-tariff", model);
    }

    @GetMapping("edit-tariff/{tariffId}")
    public ModelAndView viewEditTariff(@PathVariable @Min(1) Long tariffId) {
        return new ModelAndView("system/tariffs/edit-tariff");
    }

    @GetMapping("view-tariff/{tariffId}")
    public ModelAndView viewTariffBuId(@PathVariable @Min(1) Long tariffId) {
        return new ModelAndView("system/tariffs/view-tariff");
    }

    @PostMapping("add-tariff")
    public ResponseEntity<?> addNewTariff(@ModelAttribute @Valid TariffRequestWrap tariffRequest) {
        tariffService.addNewTariff(tariffRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("get-tariffs")
    public ResponseEntity<Page<TariffResponse>> getAllTariffs(@RequestParam @Min(0) int page,
                                                              @RequestParam @Min(1) int pageSize) {
        Page<TariffResponse> tariffResponses = tariffService.getAllTariffs(page, pageSize);
        return new ResponseEntity<>(tariffResponses, HttpStatus.OK);
    }

    @GetMapping("get-tariff-by-id/{tariffId}")
    public ResponseEntity<TariffResponse> getAllTariffs(@PathVariable Long tariffId) {
        TariffResponse tariffResponse = tariffService.getTariffById(tariffId);
        return new ResponseEntity<>(tariffResponse, HttpStatus.OK);
    }

    @PostMapping("edit-tariff/{tariffId}")
    public ResponseEntity<?> editTariff(@ModelAttribute @Valid TariffRequestWrap tariffRequest,
                                        @PathVariable Long tariffId) {
        tariffService.editTariff(tariffId, tariffRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("delete/{tariffId}")
    public ResponseEntity<?> deleteTariff(@PathVariable @Min(1) Long tariffId) {
        boolean isDeleted;
        try {
            isDeleted = tariffService.deleteTariffById(tariffId);
        } catch (TariffAlreadyUsedException usedException) {
            MultiValueMap<String, String> head = new LinkedMultiValueMap<>();
            head.add("Content-Type", "text/html; charset=utf-8");
            return new ResponseEntity<>(usedException.getTariffName(), head, HttpStatus.CONFLICT);
        }
        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("copy-tariff/{tariffId}")
    public RedirectView copyTariffById(@PathVariable Long tariffId, RedirectAttributes attributes) {
        TariffResponse tariffById = tariffService.getTariffById(tariffId);
        attributes.addFlashAttribute("copyTariff", tariffById);
        return new RedirectView("/admin/system-settings/tariffs/add", true);
    }
}
