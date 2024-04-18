package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.*;
import com.example.myhouse24admin.model.statistic.BalanceStatistic;
import com.example.myhouse24admin.model.statistic.IncomeExpenseStatistic;
import com.example.myhouse24admin.model.statistic.InvoicePaidArrearsStatistic;
import com.example.myhouse24admin.model.statistic.StatisticGeneralResponse;
import com.example.myhouse24admin.repository.*;
import com.example.myhouse24admin.specification.PersonalAccountSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticServiceImplTest {
    @Mock
    private PersonalAccountRepo personalAccountRepo;
    @Mock
    private HouseRepo houseRepo;
    @Mock
    private ApartmentRepo apartmentRepo;
    @Mock
    private MasterRequestRepo masterRequestRepo;
    @Mock
    private ApartmentOwnerRepo apartmentOwnerRepo;
    @Mock
    private CashSheetRepo cashSheetRepo;
    @Mock
    private InvoiceRepo invoiceRepo;
    @Mock
    private InvoiceItemRepo invoiceItemRepo;
    @InjectMocks
    private StatisticServiceImpl statisticService;

    @BeforeEach
    void setUp() {
    }

    @Test
    public void testGetPersonalAccountsMetrics_HappyPath() throws Exception {

        // given
        List<PersonalAccount> negativeAccounts = new ArrayList<>();
        List<PersonalAccount> positiveAccounts = new ArrayList<>();
        List<CashSheet> cashSheets = new ArrayList<>();

        PersonalAccount negativeAccount = new PersonalAccount();
        Apartment negativeApartment = new Apartment();
        negativeApartment.setId(1L);
        negativeApartment.setBalance(BigDecimal.valueOf(200).negate());
        negativeAccount.setApartment(negativeApartment);

        PersonalAccount positiveAccount = new PersonalAccount();
        Apartment positiveApartment = new Apartment();
        positiveApartment.setId(1L);
        positiveApartment.setBalance(BigDecimal.valueOf(200));
        positiveAccount.setApartment(positiveApartment);

        negativeAccounts.add(negativeAccount);
        positiveAccounts.add(positiveAccount);

        CashSheet incomeCashSheet = new CashSheet();
        incomeCashSheet.setAmount(BigDecimal.valueOf(500));
        incomeCashSheet.setSheetType(CashSheetType.INCOME);

        CashSheet expenseCashSheet = new CashSheet();
        expenseCashSheet.setAmount(BigDecimal.valueOf(500));
        expenseCashSheet.setSheetType(CashSheetType.EXPENSE);

        cashSheets.add(incomeCashSheet);
        cashSheets.add(expenseCashSheet);

        // when
        when(personalAccountRepo.findAll(any(Specification.class)))
                .thenReturn(negativeAccounts, positiveAccounts);
        when(cashSheetRepo.findByCreationDateBetweenAndDeletedIsFalseAndIsProcessedIsTrue(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(cashSheets));

        // call the method
        BalanceStatistic metrics = statisticService.getPersonalAccountsMetrics();

        // then
        assertEquals(BigDecimal.valueOf(200).negate(), metrics.accountsBalanceArrears());
        assertEquals(BigDecimal.valueOf(200), metrics.accountsBalanceOverpayments());
        assertEquals(BigDecimal.ZERO, metrics.cashRegisterBalance());

    }

    @Test()
    public void testGetPersonalAccountsMetrics_Exception() {
        // when
        when(personalAccountRepo.findAll(any(PersonalAccountSpecification.class)))
                .thenReturn(new ArrayList<>());
        when(cashSheetRepo.findByCreationDateBetweenAndDeletedIsFalseAndIsProcessedIsTrue(Mockito.any(), Mockito.any()))
                .thenReturn(CompletableFuture.failedFuture(new InterruptedException("Simulated exception")));

        // Call the method (expect exception)
        assertThrows(RuntimeException.class, () -> statisticService.getPersonalAccountsMetrics());
    }

    @Test
    void getGeneralStatistic() throws ExecutionException, InterruptedException {
        // given

        // when
        when(houseRepo.countHousesByDeletedIsFalse())
                .thenReturn(3);
        when(apartmentRepo.countApartmentsByDeletedIsFalse())
                .thenReturn(3);
        when(apartmentOwnerRepo.countApartmentOwnersByDeletedIsFalseAndStatus(OwnerStatus.ACTIVE))
                .thenReturn(3);
        when(personalAccountRepo.countPersonalAccountsByDeletedIsFalse())
                .thenReturn(3);
        when(masterRequestRepo.countMasterRequestsByStatus(MasterRequestStatus.IN_PROGRESS))
                .thenReturn(CompletableFuture.completedFuture(3));
        when(masterRequestRepo.countMasterRequestsByStatus(MasterRequestStatus.NEW))
                .thenReturn(CompletableFuture.completedFuture(3));

        // call the method
        StatisticGeneralResponse generalStatistic = statisticService.getGeneralStatistic();

        // then
        assertEquals(3, generalStatistic.countHouses());
        assertEquals(3, generalStatistic.countApartments());
        assertEquals(3, generalStatistic.countActiveApartmentOwners());
        assertEquals(3, generalStatistic.countPersonalAccounts());
        assertEquals(3, generalStatistic.countMasterRequestsInProgress());
        assertEquals(3, generalStatistic.countMasterRequestsNew());
    }

    @Test
    void getIncomeExpenseStatisticPerYear() {
        // given
        List<CashSheet> cashSheets = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            CashSheet incomeCashSheet = new CashSheet();
            incomeCashSheet.setAmount(BigDecimal.valueOf(500));
            incomeCashSheet.setSheetType(CashSheetType.INCOME);
            cashSheets.add(incomeCashSheet);

            CashSheet expenseCashSheet = new CashSheet();
            expenseCashSheet.setAmount(BigDecimal.valueOf(400));
            expenseCashSheet.setSheetType(CashSheetType.EXPENSE);
            cashSheets.add(expenseCashSheet);
        }

        // when
        when(cashSheetRepo.findByCreationDateBetweenAndDeletedIsFalseAndIsProcessedIsTrue(any(Instant.class), any(Instant.class)))
                .thenReturn(CompletableFuture.completedFuture(cashSheets));

        // call the method
        List<IncomeExpenseStatistic> incomeExpenseStatisticPerYear = statisticService.getIncomeExpenseStatisticPerYear();

        // then
        assertEquals(12, incomeExpenseStatisticPerYear.size());
        for (IncomeExpenseStatistic incomeExpenseStatistic : incomeExpenseStatisticPerYear) {
            assertEquals(BigDecimal.valueOf(2500), incomeExpenseStatistic.allIncomes());
            assertEquals(BigDecimal.valueOf(2000), incomeExpenseStatistic.allExpenses());
        }
    }

    @Test
    void getIncomeExpenseStatisticPerYear_Exception() {
        // when
        when(cashSheetRepo.findByCreationDateBetweenAndDeletedIsFalseAndIsProcessedIsTrue(any(Instant.class), any(Instant.class)))
                .thenReturn(CompletableFuture.failedFuture(new InterruptedException("Simulated exception")));

        // call the method
        assertThrows(RuntimeException.class, () -> statisticService.getIncomeExpenseStatisticPerYear());
    }

    @Test
    void getInvoicesPaidArrearsStatisticPerYear() {
        // given
        List<Invoice> invoices = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Invoice invoice = new Invoice();
            invoice.setId((long) i + 1);
            invoice.setPaid(BigDecimal.valueOf(500));
            invoices.add(invoice);
        }

        // when
        when(invoiceRepo.findByCreationDateBetweenAndDeletedIsFalse(any(Instant.class), any(Instant.class)))
                .thenReturn(CompletableFuture.completedFuture(invoices));
        when(invoiceItemRepo.getItemsSumByInvoiceIdIn(anyList()))
                .thenReturn(CompletableFuture.completedFuture(BigDecimal.valueOf(2000)));

        // call the method
        List<InvoicePaidArrearsStatistic> paidArrearsStatisticPerYear = statisticService.getInvoicesPaidArrearsStatisticPerYear();

        // then
        assertEquals(12, paidArrearsStatisticPerYear.size());
        for (InvoicePaidArrearsStatistic invoicePaidArrearsStatistic : paidArrearsStatisticPerYear) {
            assertEquals(BigDecimal.valueOf(2000), invoicePaidArrearsStatistic.paidArrears());
            assertEquals(BigDecimal.valueOf(2500), invoicePaidArrearsStatistic.arrears());
        }
    }

    @Test
    void getInvoicesPaidArrearsStatisticPerYear_Exception() {
        // when
        when(invoiceRepo.findByCreationDateBetweenAndDeletedIsFalse(any(Instant.class), any(Instant.class)))
                .thenReturn(CompletableFuture.failedFuture(new InterruptedException("Simulated exception")));

        // call the method
        assertThrows(RuntimeException.class, () -> statisticService.getInvoicesPaidArrearsStatisticPerYear());
    }
}