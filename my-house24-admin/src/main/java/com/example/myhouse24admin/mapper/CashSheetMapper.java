package com.example.myhouse24admin.mapper;

import com.example.myhouse24admin.entity.*;
import com.example.myhouse24admin.model.cashRegister.*;
import com.example.myhouse24admin.model.invoices.InvoiceShortResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {PersonalAccountMapper.class,
                ApartmentOwnerMapper.class,
                PaymentItemMapper.class,
                StaffMapper.class})
public interface CashSheetMapper {


    List<CashSheetTableResponse> sashSheetListToCashSheetTableResponseList(List<CashSheet> cashSheetList);

    @Mapping(target = "apartmentOwner", source = "personalAccount.apartment.owner")
    CashSheetTableResponse cashSheetToCashSheetTableResponse(CashSheet cashSheet);

    @Mapping(target = "sheetType", constant = "INCOME")
    @Mapping(target = "personalAccount.id", source = "personalAccountId")
    @Mapping(target = "personalAccount.apartment.owner.id", source = "ownerId")
    @Mapping(target = "staff.id", source = "staffId")
    @Mapping(target = "paymentItem.id", source = "paymentItemId")
    CashSheet cashSheetIncomeAddRequestToCashSheet(CashSheetIncomeAddRequest cashSheetIncomeAddRequest);

    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "setInvoice")
    CashSheetResponse cashSheetToCashSheetWithOwnerResponse(CashSheet cashSheetById);

    @Mapping(target = "personalAccount", source = "personalAccountId", qualifiedByName = "setPersonalAccount")
    @Mapping(target = "paymentItem", source = "paymentItemId", qualifiedByName = "setPaymentItem")
    @Mapping(target = "staff", source = "staffId", qualifiedByName = "setStaff")
    @Mapping(target = "sheetNumber", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sheetType", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    void updateCashSheetFromCashSheetIncomeUpdateRequest(@MappingTarget CashSheet cashSheet, CashSheetIncomeUpdateRequest updateRequest);

    @Mapping(target = "sheetType", constant = "EXPENSE")
    @Mapping(target = "staff", source = "staffId", qualifiedByName = "setStaff")
    @Mapping(target = "paymentItem", source = "paymentItemId", qualifiedByName = "setPaymentItem")
    CashSheet cashSheetExpenseAddRequestToCashSheet(CashSheetExpenseAddRequest addRequest);

    @Mapping(target = "paymentItem", source = "paymentItemId", qualifiedByName = "setPaymentItem")
    @Mapping(target = "staff", source = "staffId", qualifiedByName = "setStaff")
    @Mapping(target = "sheetNumber", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sheetType", ignore = true)
    void updateCashSheetFromCashSheetExpenseUpdateRequest(@MappingTarget CashSheet cashSheetById, CashSheetExpenseUpdateRequest updateRequest);

    @Named(value = "setPersonalAccount")
    static PersonalAccount setPersonalAccount(Long personalAccountId) {
        PersonalAccount personalAccount = null;
        if (personalAccountId != null) {
            personalAccount = new PersonalAccount();
            personalAccount.setId(personalAccountId);
        }
        return personalAccount;
    }

    @Named(value = "setPaymentItem")
    static PaymentItem setPaymentItem(Long paymentItemId) {
        PaymentItem paymentItem = null;
        if (paymentItemId != null) {
            paymentItem = new PaymentItem();
            paymentItem.setId(paymentItemId);
        }
        return paymentItem;
    }

    @Named(value = "setStaff")
    static Staff setStaff(Long staffId) {
        Staff staff = null;
        if (staffId != null) {
            staff = new Staff();
            staff.setId(staffId);
        }
        return staff;
    }

    @Named(value = "setInvoice")
    static InvoiceShortResponse setInvoice(Invoice invoice) {
        return Mappers.getMapper(InvoiceMapper.class).invoiceToInvoiceShortResponse(invoice);
    }


    @Mapping(target = "sheetNumber", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sheetType", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "amount", source = "paid")
    @Mapping(target = "personalAccount", source = "apartment.personalAccount")
    void updateCashSheetFromInvoice(@MappingTarget CashSheet cashSheet, Invoice invoice);
}
