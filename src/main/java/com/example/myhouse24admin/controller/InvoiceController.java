package com.example.myhouse24admin.controller;

import com.example.myhouse24admin.entity.InvoiceStatus;
import com.example.myhouse24admin.model.invoices.*;
import com.example.myhouse24admin.model.meterReadings.*;
import com.example.myhouse24admin.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/invoices")
public class InvoiceController {
    private final HouseService houseService;
    private final SectionService sectionService;
    private final ApartmentService apartmentService;
    private final TariffService tariffService;
    private final InvoiceService invoiceService;
    private final ServicesService servicesService;
    private final MeterReadingService meterReadingService;
    private final ApartmentOwnerService apartmentOwnerService;

    public InvoiceController(HouseService houseService,
                             SectionService sectionService,
                             ApartmentService apartmentService,
                             TariffService tariffService,
                             InvoiceService invoiceService,
                             ServicesService servicesService,
                             MeterReadingService meterReadingService,
                             ApartmentOwnerService apartmentOwnerService) {
        this.houseService = houseService;
        this.sectionService = sectionService;
        this.apartmentService = apartmentService;
        this.tariffService = tariffService;
        this.invoiceService = invoiceService;
        this.servicesService = servicesService;
        this.meterReadingService = meterReadingService;
        this.apartmentOwnerService = apartmentOwnerService;
    }

    @GetMapping()
    public ModelAndView getInvoicesPage() {
        return new ModelAndView("invoices/invoices");
    }
    @GetMapping("/get")
    public @ResponseBody Page<TableInvoiceResponse> getInvoices(@RequestParam Map<String, String> requestMap) {
        return invoiceService.getInvoiceResponsesForTable(requestMap);
    }
    @GetMapping("/add")
    public ModelAndView getInvoicePage() {
        return new ModelAndView("invoices/add-invoice");
    }
    @PostMapping("/add")
    public @ResponseBody ResponseEntity<?> createInvoice(@ModelAttribute @Valid InvoiceRequest invoiceRequest,
                                              HttpServletRequest request) {
        invoiceService.createInvoice(invoiceRequest);
        String url = request.getRequestURL().toString();
        int index = url.lastIndexOf("/");
        String returnUrl = url.substring(0, index);
        return new ResponseEntity<>(returnUrl, HttpStatus.OK);
    }
    @GetMapping("/get-statuses")
    public @ResponseBody InvoiceStatus[] getStatuses() {
        return InvoiceStatus.values();
    }
    @GetMapping("/get-houses")
    public @ResponseBody Page<HouseNameResponse> getHouses(SelectSearchRequest selectSearchRequest) {
        return houseService.getHousesForSelect(selectSearchRequest);
    }
    @GetMapping("/get-sections")
    public @ResponseBody Page<SectionNameResponse> getSections(@RequestParam Map<String, String> requestMap) {
        return sectionService.getSectionForSelect(requestMap);
    }
    @GetMapping("/get-apartments")
    public @ResponseBody Page<ApartmentNumberResponse> getApartments(SelectSearchRequest selectSearchRequest,
                                                                     @RequestParam("houseId")Long houseId,
                                                                     @RequestParam(value = "sectionId", required = false)Long sectionId) {
        return apartmentService.getApartmentsForSelect(selectSearchRequest, houseId, sectionId);
    }
    @GetMapping("/get-services")
    public @ResponseBody Page<ServiceNameResponse> getServices(SelectSearchRequest selectSearchRequest) {
        return servicesService.getServicesForSelect(selectSearchRequest);
    }
    @GetMapping("/get-number")
    public @ResponseBody String getNumber() {
        return invoiceService.createNumber();
    }
    @GetMapping("/get-owner")
    public @ResponseBody OwnerResponse getOwner(@RequestParam("apartmentId")Long apartmentId) {
        return invoiceService.getOwnerResponse(apartmentId);
    }
    @GetMapping("/get-meter-readings")
    public @ResponseBody Page<ApartmentMeterReadingResponse> getMeterReadings(@RequestParam(name = "page") int page,
                                                                          @RequestParam(name = "pageSize") int pageSize,
                                                                          @RequestParam(name = "apartmentId", required = false) Long apartmentId) {
        return meterReadingService.getMeterReadingResponsesForTableInInvoice(page, pageSize, apartmentId);
    }
    @GetMapping("/get-tariff-items")
    public @ResponseBody List<TariffItemResponse> getTariffItems(@RequestParam(value = "tariffId", required = false)Long tariffId) {
        return tariffService.getTariffItems(tariffId);
    }
    @GetMapping("/get-unit-name")
    public @ResponseBody UnitNameResponse getUnitOfMeasurement(@RequestParam("serviceId")Long serviceId) {
        return servicesService.getUnitOfMeasurementNameByServiceId(serviceId);
    }
    @GetMapping("/get-amounts")
    public @ResponseBody List<BigDecimal> getAmountOfConsumptions(@RequestParam("serviceIds[]")Long[] serviceIds,
                                                                  @RequestParam(name = "apartmentId") Long apartmentId) {
        return meterReadingService.getAmountOfConsumptions(serviceIds, apartmentId);
    }
    @GetMapping("/get-owners")
    public @ResponseBody Page<OwnerNameResponse> getOwners(SelectSearchRequest selectSearchRequest) {
        return apartmentOwnerService.getOwnerNameResponses(selectSearchRequest);
    }
    @GetMapping("/edit/{id}")
    public ModelAndView getEditInvoicePage() {
        return new ModelAndView("invoices/edit-invoice");
    }
    @GetMapping("/get-invoice/{id}")
    public @ResponseBody InvoiceResponse getInvoice(@PathVariable Long id) {
        return invoiceService.getInvoiceResponse(id);
    }
    @PostMapping("/edit/{id}")
    public @ResponseBody ResponseEntity<?>updateInvoice(@PathVariable Long id, HttpServletRequest request,
                                              @ModelAttribute @Valid InvoiceRequest invoiceRequest) {
        invoiceService.updateInvoice(id, invoiceRequest);
        String url = request.getRequestURL().toString();
        int index = url.lastIndexOf("/");
        url = url.substring(0, index - 5);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }
    @GetMapping("/view-invoice/{id}")
    public ModelAndView getViewInvoicePage() {
        return new ModelAndView("invoices/view-invoice");
    }
    @GetMapping("/view-invoice/get/{id}")
    public @ResponseBody ViewInvoiceResponse getInvoiceForView(@PathVariable Long id) {
        return invoiceService.getInvoiceResponseForView(id);
    }
    @GetMapping("/delete/{id}")
    public @ResponseBody ResponseEntity<?> deleteInvoice(@PathVariable Long id) {
        boolean deleted = invoiceService.deleteInvoice(id);
        if(deleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    @GetMapping("/delete-invoices")
    public @ResponseBody ResponseEntity<?> deleteInvoices(@RequestParam(name = "invoiceIds[]", required = false) Long[] invoiceIds) {
        boolean deleted = invoiceService.deleteInvoices(invoiceIds);
        if(deleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    @GetMapping("/copy/{id}")
    public ModelAndView getInvoicePageForCopy() {
        return new ModelAndView("invoices/edit-invoice");
    }
    @PostMapping("/copy/{id}")
    public @ResponseBody ResponseEntity<?> saveCopiedInvoice(@ModelAttribute @Valid InvoiceRequest invoiceRequest,
                                                         HttpServletRequest request) {
        invoiceService.createInvoice(invoiceRequest);
        String url = request.getRequestURL().toString();
        int index = url.lastIndexOf("/");
        url = url.substring(0, index - 5);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

}
