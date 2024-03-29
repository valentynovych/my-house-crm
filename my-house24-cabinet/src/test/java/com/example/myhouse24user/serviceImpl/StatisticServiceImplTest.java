package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.*;
import com.example.myhouse24user.model.statistic.GeneralOwnerStatistic;
import com.example.myhouse24user.model.statistic.StatisticDateItem;
import com.example.myhouse24user.model.statistic.StatisticItem;
import com.example.myhouse24user.repository.ApartmentRepo;
import com.example.myhouse24user.repository.InvoiceItemRepo;
import com.example.myhouse24user.repository.InvoiceRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.myhouse24user.config.TestConfig.USER_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticServiceImplTest {

    @Mock
    private ApartmentRepo apartmentRepo;
    @Mock
    private InvoiceRepo invoiceRepo;
    @Mock
    private InvoiceItemRepo invoiceItemRepo;
    @InjectMocks
    private StatisticServiceImpl statisticService;
    @Mock
    private Principal principal;
    private Apartment apartment;
    private List<Invoice> apartmentInvoices;
    private List<InvoiceItem> apartmentInvoiceItems;

    @BeforeEach
    void setUp() {
        this.apartmentInvoices = new ArrayList<>();
        this.apartmentInvoiceItems = new ArrayList<>();
        this.apartment = new Apartment();

        this.apartment.setId(1L);
        this.apartment.setApartmentNumber("00001");
        this.apartment.setBalance(BigDecimal.valueOf(-1552.4));

        PersonalAccount personalAccount = new PersonalAccount();
        personalAccount.setAccountNumber(1L);
        this.apartment.setPersonalAccount(personalAccount);

        Service invoiceItemService = new Service();
        invoiceItemService.setId(1L);
        invoiceItemService.setName("Service");

        for (int i = 0; i < 12; i++) {
            Invoice invoice = new Invoice();
            invoice.setId((long) i);
            invoice.setApartment(apartment);
            invoice.setCreationDate(LocalDate.now().minusMonths(i).atStartOfDay(ZoneId.systemDefault()).toInstant());
            invoice.setNumber("0000" + i);
            invoice.setProcessed(true);
            invoice.setPaid(BigDecimal.valueOf(i * 100));
            invoice.setStatus(InvoiceStatus.PAID);
            apartmentInvoices.add(invoice);

            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setId((long) i);
            invoiceItem.setInvoice(invoice);
            invoiceItem.setService(invoiceItemService);
            invoiceItem.setCost(BigDecimal.valueOf(i * 10));
            invoiceItem.setAmount(BigDecimal.valueOf(i * 10));
            invoiceItem.setPricePerUnit(BigDecimal.valueOf(10));
            apartmentInvoiceItems.add(invoiceItem);
        }
    }

    @Test
    void getGeneralStatistic_WhenApartmentExistAndHasInvoices() {
        // when
        when(principal.getName())
                .thenReturn(USER_EMAIL);
        when(apartmentRepo.findApartmentByIdAndOwner_Email(1L, USER_EMAIL))
                .thenReturn(Optional.of(apartment));
        when(invoiceRepo.findInvoicesByApartment(apartment))
                .thenReturn(apartmentInvoices);

        GeneralOwnerStatistic generalStatistic = statisticService.getGeneralStatistic(1L, principal);

        // then
        assertNotNull(generalStatistic);
        assertEquals(apartment.getBalance(), generalStatistic.currentBalance());
        assertEquals("00000-00001", generalStatistic.personalAccountNumber());
        assertTrue(generalStatistic.expenseOnLastMonth().doubleValue() > 0.0);
        System.out.println(generalStatistic.expenseOnLastMonth());
    }

    @Test
    void getGeneralStatistic_WhenApartmentNotExistInvoices() {
        // when
        when(principal.getName())
                .thenReturn(USER_EMAIL);
        when(apartmentRepo.findApartmentByIdAndOwner_Email(1L, USER_EMAIL))
                .thenReturn(Optional.of(apartment));
        when(invoiceRepo.findInvoicesByApartment(apartment))
                .thenReturn(new ArrayList<>());

        GeneralOwnerStatistic generalStatistic = statisticService.getGeneralStatistic(1L, principal);

        // then
        assertNotNull(generalStatistic);
        assertEquals(apartment.getBalance(), generalStatistic.currentBalance());
        assertEquals("00000-00001", generalStatistic.personalAccountNumber());
        assertEquals(0.0, generalStatistic.expenseOnLastMonth().doubleValue());
    }

    @Test
    void getGeneralStatistic_WhenApartmentNotFound() {
        // when
        when(principal.getName())
                .thenReturn(USER_EMAIL);
        when(apartmentRepo.findApartmentByIdAndOwner_Email(1L, USER_EMAIL))
                .thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class,
                () -> statisticService.getGeneralStatistic(1L, principal));
    }

    @Test
    void getExpensePerMonthStatistic_CorrectTest() {
        // when
        when(principal.getName())
                .thenReturn(USER_EMAIL);
        when(apartmentRepo.findApartmentByIdAndOwner_Email(1L, USER_EMAIL))
                .thenReturn(Optional.of(apartment));
        when(invoiceRepo.findInvoicesByApartmentAndCreationDateBetween(eq(apartment), any(Instant.class), any(Instant.class)))
                .thenReturn(apartmentInvoices);
        when(invoiceItemRepo.findInvoiceItemsByInvoiceIn(anyList()))
                .thenReturn(apartmentInvoiceItems);

        List<StatisticItem> expensePerMonthStatistic = statisticService.getExpensePerMonthStatistic(
                apartment.getId(), principal);

        // then
        assertFalse(expensePerMonthStatistic.isEmpty());
        assertEquals(1, expensePerMonthStatistic.size());

        for (StatisticItem item : expensePerMonthStatistic) {
            assertTrue(new BigDecimal(item.itemValue()).doubleValue() > 0.0);
        }
    }

    @Test
    void getExpensePerYearStatistic() {
        // when
        when(principal.getName())
                .thenReturn(USER_EMAIL);
        when(apartmentRepo.findApartmentByIdAndOwner_Email(1L, USER_EMAIL))
                .thenReturn(Optional.of(apartment));
        when(invoiceRepo.findInvoicesByApartmentAndCreationDateBetween(eq(apartment), any(Instant.class), any(Instant.class)))
                .thenReturn(apartmentInvoices);
        when(invoiceItemRepo.findInvoiceItemsByInvoiceIn(anyList()))
                .thenReturn(apartmentInvoiceItems);

        List<StatisticItem> expensePerYearStatistic = statisticService.getExpensePerYearStatistic(apartment.getId(), principal);
        // then
        assertFalse(expensePerYearStatistic.isEmpty());
        assertEquals(1, expensePerYearStatistic.size());

        for (StatisticItem item : expensePerYearStatistic) {
            assertTrue(new BigDecimal(item.itemValue()).doubleValue() > 0.0);
        }
    }

    @Test
    void getExpensePerYearOnMonthStatistic() {

        // when
        when(principal.getName())
                .thenReturn(USER_EMAIL);
        when(apartmentRepo.findApartmentByIdAndOwner_Email(1L, USER_EMAIL))
                .thenReturn(Optional.of(apartment));
        when(invoiceRepo.findInvoicesByApartmentAndCreationDateBetween(eq(apartment), any(Instant.class), any(Instant.class)))
                .thenReturn(apartmentInvoices);

        List<StatisticDateItem> expensePerYearOnMonthStatistic =
                statisticService.getExpensePerYearOnMonthStatistic(apartment.getId(), principal);

        // then
        assertFalse(expensePerYearOnMonthStatistic.isEmpty());
        assertEquals(12, expensePerYearOnMonthStatistic.size());
        for (StatisticDateItem item : expensePerYearOnMonthStatistic) {
            assertEquals(6600, item.amount().intValue());
        }
    }
}