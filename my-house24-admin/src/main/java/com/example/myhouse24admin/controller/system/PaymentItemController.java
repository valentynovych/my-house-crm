package com.example.myhouse24admin.controller.system;

import com.example.myhouse24admin.model.paymentItem.PaymentItemDto;
import com.example.myhouse24admin.service.PaymentItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("admin/system-settings/payment-items")
public class PaymentItemController {

    private final PaymentItemService paymentItemService;

    public PaymentItemController(PaymentItemService paymentItemService) {
        this.paymentItemService = paymentItemService;
    }

    @GetMapping
    public ModelAndView viewPaymentItems() {
        return new ModelAndView("system/payment.item/payment-items");
    }

    @GetMapping("edit-item/{itemId}")
    public ModelAndView editItemById(@PathVariable Long itemId) {
        return new ModelAndView("system/payment.item/edit-payment-item");
    }

    @GetMapping("add-item")
    public ModelAndView editItemById() {
        return new ModelAndView("system/payment.item/add-payment-item");
    }

    @GetMapping("get-items")
    public ResponseEntity<?> getAllItems(@RequestParam int page,
                                         @RequestParam int pageSize,
                                         @RequestParam Map<String, String> searchParams) {
        Page<PaymentItemDto> paymentItems = paymentItemService.getPaymentItems(page, pageSize, searchParams);
        return new ResponseEntity<>(paymentItems, HttpStatus.OK);
    }


    @ResponseBody
    @GetMapping("get-item/{itemId}")
    public ResponseEntity<?> getItemById(@PathVariable Long itemId) {
        PaymentItemDto itemById = paymentItemService.getItemById(itemId);
        return new ResponseEntity<>(itemById, HttpStatus.OK);
    }

    @PostMapping("edit-item/{itemId}")
    public ResponseEntity<?> editItemById(@PathVariable Long itemId,
                                          @Valid @ModelAttribute("paymentItem") PaymentItemDto paymentItem) {
        paymentItemService.editItemById(itemId, paymentItem);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("add-item")
    public ResponseEntity<?> addItem(@Valid @ModelAttribute("paymentItem") PaymentItemDto paymentItem) {
        paymentItemService.addItem(paymentItem);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("get-item-types")
    public ResponseEntity<?> getAllItemTypes() {
        Map<String, String> itemTypes = paymentItemService.getItemTypes();
        return new ResponseEntity<>(itemTypes, HttpStatus.OK);
    }

    @DeleteMapping("delete/{paymentItemId}")
    public ResponseEntity<?> deletePaymentItem(@PathVariable Long paymentItemId) {
        boolean isDeleted = paymentItemService.deleteItemById(paymentItemId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
