package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.*;
import com.example.myhouse24admin.model.statistic.BalanceStatistic;
import com.example.myhouse24admin.model.statistic.IncomeExpenseStatistic;
import com.example.myhouse24admin.model.statistic.InvoicePaidArrearsStatistic;
import com.example.myhouse24admin.model.statistic.StatisticGeneralResponse;
import com.example.myhouse24admin.repository.*;
import com.example.myhouse24admin.service.StatisticService;
import com.example.myhouse24admin.specification.PersonalAccountSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class StatisticServiceImpl implements StatisticService {

    private final PersonalAccountRepo personalAccountRepo;
    private final HouseRepo houseRepo;
    private final ApartmentRepo apartmentRepo;
    private final MasterRequestRepo masterRequestRepo;
    private final ApartmentOwnerRepo apartmentOwnerRepo;
    private final CashSheetRepo cashSheetRepo;
    private final InvoiceRepo invoiceRepo;
    private final InvoiceItemRepo invoiceItemRepo;
    private final int year = LocalDate.now().getYear();
    private final Month currentMonth = LocalDate.now().getMonth();
    private final Logger logger = LogManager.getLogger(StatisticServiceImpl.class);

    public StatisticServiceImpl(PersonalAccountRepo personalAccountRepo, HouseRepo houseRepo,
                                ApartmentRepo apartmentRepo, MasterRequestRepo masterRequestRepo,
                                ApartmentOwnerRepo apartmentOwnerRepo, CashSheetRepo cashSheetRepo,
                                InvoiceRepo invoiceRepo, InvoiceItemRepo invoiceItemRepo) {
        this.personalAccountRepo = personalAccountRepo;
        this.houseRepo = houseRepo;
        this.apartmentRepo = apartmentRepo;
        this.masterRequestRepo = masterRequestRepo;
        this.apartmentOwnerRepo = apartmentOwnerRepo;
        this.cashSheetRepo = cashSheetRepo;
        this.invoiceRepo = invoiceRepo;
        this.invoiceItemRepo = invoiceItemRepo;
    }

    @Override
    public BalanceStatistic getPersonalAccountsMetrics() {
        logger.info("getPersonalAccountsMetrics() ->Get personal accounts metrics");
        BigDecimal accountsBalanceArrears;
        BigDecimal accountsBalanceOverpayments;
        BigDecimal cashRegisterBalance;

        try {
            accountsBalanceArrears = getAccountsBalanceArrears().get();
            accountsBalanceOverpayments = getAccountsBalanceOverpayments().get();
            cashRegisterBalance = getCashRegisterBalance().get();
            logger.info("getPersonalAccountsMetrics() -> Get personal accounts metrics completed");
        } catch (InterruptedException | ExecutionException e) {
            logger.error("getPersonalAccountsMetrics() -> Get personal accounts metrics failed", e);
            throw new RuntimeException(e);
        }

        BalanceStatistic metrics = new BalanceStatistic(
                accountsBalanceArrears, accountsBalanceOverpayments, cashRegisterBalance);
        logger.info("getPersonalAccountsMetrics() -> Get personal accounts metrics completed");
        return metrics;
    }


    @Override
    public StatisticGeneralResponse getGeneralStatistic() throws ExecutionException, InterruptedException {
        logger.info("Get general statistic");
        int countAllHouses = getCountAllHouses().get();
        int countAllApartments = getCountAllApartments().get();
        int countAllActiveApartmentOwners = getCountAllActiveApartmentOwners().get();
        int countAllPersonalAccounts = getCountAllPersonalAccounts().get();
        int countMasterRequestsInProgress = getCountMasterRequestsInProgress().get();
        int countMasterRequestsNew = getCountMasterRequestsNew().get();

        StatisticGeneralResponse generalResponse =
                new StatisticGeneralResponse(countAllApartments,
                        countAllHouses,
                        countAllActiveApartmentOwners,
                        countAllPersonalAccounts,
                        countMasterRequestsInProgress,
                        countMasterRequestsNew);
        logger.info("Get general statistic completed");
        return generalResponse;
    }

    @Override
    public List<IncomeExpenseStatistic> getIncomeExpenseStatisticPerYear() {
        logger.info("getIncomeExpenseStatisticPerYear() -> Get income expense statistic per year");

        return IntStream.rangeClosed(1, 12)
                .parallel()
                .mapToObj(month -> {
                    logger.info("getIncomeExpenseStatisticPerYear() -> Get income expense statistic per year -> month: " + month);
                    LocalDate startMouth = LocalDate.of(year, month, 1);
                    LocalDate endMouth = startMouth.plusMonths(1);
                    Instant dateFrom = startMouth.atStartOfDay(ZoneId.systemDefault()).toInstant();
                    Instant dateTo = endMouth.atStartOfDay(ZoneId.systemDefault()).toInstant();
                    List<CashSheet> cashSheets;

                    BigDecimal totalIncome;
                    BigDecimal totalExpense;
                    try {
                        cashSheets = cashSheetRepo.findByCreationDateBetweenAndDeletedIsFalse(dateFrom, dateTo).get();
                        totalIncome = getAmountValueFromCashSheetsBySheetType(cashSheets, CashSheetType.INCOME).get();
                        totalExpense = getAmountValueFromCashSheetsBySheetType(cashSheets, CashSheetType.EXPENSE).get();
                        logger.info("getIncomeExpenseStatisticPerYear() -> " +
                                "Get income expense statistic per year -> month: {} completed", month);
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error("Get income expense statistic per year error", e);
                        throw new RuntimeException(e);
                    }
                    logger.info("Get income expense statistic per year completed");
                    return new IncomeExpenseStatistic(dateFrom, totalIncome, totalExpense);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoicePaidArrearsStatistic> getInvoicesPaidArrearsStatisticPerYear() {
        logger.info("getInvoicesPaidArrearsStatisticPerYear() -> Get invoices paid arrears statistic per year");
        return IntStream.rangeClosed(1, 12)
                .parallel()
                .mapToObj(month -> {
                    logger.info("getInvoicesPaidArrearsStatisticPerYear() -> " +
                            "Get invoices paid arrears statistic per year -> month: {}", month);
                    LocalDate startMouth = LocalDate.of(year, month, 1);
                    Instant dateFrom;
                    List<Invoice> invoices;
                    BigDecimal invoiceItemsCostSum;
                    try {
                        dateFrom = getInstantFromLocalDate(startMouth).get();
                        invoices = invoiceRepo.findByCreationDateBetweenAndDeletedIsFalse(
                                        dateFrom,
                                        getInstantFromLocalDate(startMouth.plusMonths(1)).get())
                                .get();
                        invoiceItemsCostSum = getInvoiceItemsByInvoices(invoices).get();
                        logger.info("Get invoices paid arrears statistic per year -> month: {} completed", month);
                    } catch (InterruptedException | ExecutionException e) {
                        logger.info("Get invoices paid arrears statistic per year error", e);
                        throw new RuntimeException(e);
                    }

                    BigDecimal totalPaid = invoices.stream()
                            .map(Invoice::getPaid)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    logger.info("Get invoices paid arrears statistic per year completed");
                    return new InvoicePaidArrearsStatistic(dateFrom, totalPaid, invoiceItemsCostSum != null ? invoiceItemsCostSum : BigDecimal.ZERO);
                })
                .collect(Collectors.toList());
    }

    @Async
    protected CompletableFuture<BigDecimal> getCashRegisterBalance() throws ExecutionException, InterruptedException {
        logger.info("Get cash register balance");
        LocalDate localDate = LocalDate.of(year, currentMonth, 1);
        List<CashSheet> cashSheets = cashSheetRepo.findByCreationDateBetweenAndDeletedIsFalse(
                getInstantFromLocalDate(localDate).get(),
                getInstantFromLocalDate(localDate.plusMonths(1)).get()).get();

        BigDecimal incomeBalance = getAmountValueFromCashSheetsBySheetType(cashSheets, CashSheetType.INCOME).get();
        BigDecimal expenseBalance = getAmountValueFromCashSheetsBySheetType(cashSheets, CashSheetType.EXPENSE).get();

        logger.info("Get cash register balance completed, incomeBalance: {}, expenseBalance: {}", incomeBalance, expenseBalance);
        return CompletableFuture.completedFuture(incomeBalance.subtract(expenseBalance));
    }

    @Async
    protected CompletableFuture<BigDecimal> getAmountValueFromCashSheetsBySheetType(List<CashSheet> cashSheets, CashSheetType cashSheetType) {
        return CompletableFuture.completedFuture(cashSheets.stream()
                .filter(sheet -> sheet.getSheetType() == cashSheetType)
                .map(CashSheet::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Async
    protected CompletableFuture<BigDecimal> getAccountsBalanceArrears() {
        logger.info("getAccountsBalanceArrears() -> Get accounts balance arrears");
        PersonalAccountSpecification byBalanceIsNegative = new PersonalAccountSpecification(Map.of("balance", "arrears"));
        List<PersonalAccount> allNegative = personalAccountRepo.findAll(byBalanceIsNegative);
        BigDecimal accountsBalanceArrears = allNegative.stream()
                .map(personalAccount -> personalAccount.getApartment().getBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.info("getAccountsBalanceArrears() -> Get accounts balance arrears completed");
        return CompletableFuture.completedFuture(accountsBalanceArrears);
    }

    @Async
    protected CompletableFuture<BigDecimal> getAccountsBalanceOverpayments() {
        logger.info("getAccountsBalanceOverpayments() -> Get accounts balance overpayments");
        PersonalAccountSpecification byBalanceIsPositive = new PersonalAccountSpecification(Map.of("balance", "overpayment"));
        List<PersonalAccount> allPositive = personalAccountRepo.findAll(byBalanceIsPositive);
        BigDecimal accountsBalanceOverpayments = allPositive.stream()
                .map(personalAccount -> personalAccount.getApartment().getBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.info("getAccountsBalanceOverpayments() -> Get accounts balance overpayments completed");
        return CompletableFuture.completedFuture(accountsBalanceOverpayments);
    }

    @Async
    protected CompletableFuture<Instant> getInstantFromLocalDate(LocalDate localDate) {
        logger.info("Get instant from local date");
        return CompletableFuture.supplyAsync(() -> localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Async
    protected CompletableFuture<BigDecimal> getInvoiceItemsByInvoices(List<Invoice> invoices) throws ExecutionException, InterruptedException {
        logger.info("getInvoiceItemsByInvoices() -> Get invoice items by invoices");
        BigDecimal sumCost = invoiceItemRepo.getItemsSumByInvoiceIdIn(
                        invoices.stream()
                                .map(Invoice::getId)
                                .toList())
                .get();
        logger.info("getInvoiceItemsByInvoices() -> Get invoice items by invoices completed");
        return CompletableFuture.completedFuture(sumCost);
    }

    @Async
    protected CompletableFuture<Integer> getCountAllHouses() {
        logger.info("getCountAllHouses() -> Get count all houses");
        int countHouses = houseRepo.countHousesByDeletedIsFalse();
        logger.info("getCountAllHouses() -> Get count all houses completed");
        return CompletableFuture.completedFuture(countHouses);
    }

    @Async
    protected CompletableFuture<Integer> getCountAllApartments() {
        logger.info("getCountAllApartments() -> Get count all apartments");
        int countApartments = apartmentRepo.countApartmentsByDeletedIsFalse();
        logger.info("getCountAllApartments() -> Get count all apartments completed");
        return CompletableFuture.completedFuture(countApartments);
    }

    @Async
    protected CompletableFuture<Integer> getCountAllActiveApartmentOwners() {
        logger.info("getCountAllActiveApartmentOwners() -> Get count all active apartment owners");
        int countActiveApartmentOwners =
                apartmentOwnerRepo.countApartmentOwnersByDeletedIsFalseAndStatus(OwnerStatus.ACTIVE);
        logger.info("getCountAllActiveApartmentOwners() -> Get count all active apartment owners completed");
        return CompletableFuture.completedFuture(countActiveApartmentOwners);
    }

    @Async
    protected CompletableFuture<Integer> getCountAllPersonalAccounts() {
        logger.info("getCountAllPersonalAccounts() -> Get count all personal accounts");
        int countActiveApartmentOwners = personalAccountRepo.countPersonalAccountsByDeletedIsFalse();
        logger.info("getCountAllPersonalAccounts() -> Get count all personal accounts completed");
        return CompletableFuture.completedFuture(countActiveApartmentOwners);
    }

    @Async
    protected CompletableFuture<Integer> getCountMasterRequestsInProgress() {
        logger.info("getCountMasterRequestsInProgress() -> Get count master requests in progress");
        CompletableFuture<Integer> integerCompletableFuture = masterRequestRepo.countMasterRequestsByStatus(MasterRequestStatus.IN_PROGRESS);
        logger.info("getCountMasterRequestsInProgress() -> Get count master requests in progress completed");
        return integerCompletableFuture;
    }

    @Async
    protected CompletableFuture<Integer> getCountMasterRequestsNew() {
        logger.info("getCountMasterRequestsNew() -> Get count master requests new");
        CompletableFuture<Integer> integerCompletableFuture = masterRequestRepo.countMasterRequestsByStatus(MasterRequestStatus.NEW);
        logger.info("getCountMasterRequestsNew() -> Get count master requests new completed");
        return integerCompletableFuture;
    }
}
