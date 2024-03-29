package com.example.myhouse24user.serviceImpl;

import com.example.myhouse24user.entity.Apartment;
import com.example.myhouse24user.entity.Invoice;
import com.example.myhouse24user.entity.InvoiceItem;
import com.example.myhouse24user.model.statistic.GeneralOwnerStatistic;
import com.example.myhouse24user.model.statistic.StatisticDateItem;
import com.example.myhouse24user.model.statistic.StatisticItem;
import com.example.myhouse24user.repository.ApartmentRepo;
import com.example.myhouse24user.repository.InvoiceItemRepo;
import com.example.myhouse24user.repository.InvoiceRepo;
import com.example.myhouse24user.service.StatisticService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class StatisticServiceImpl implements StatisticService {

    private final ApartmentRepo apartmentRepo;
    private final InvoiceRepo invoiceRepo;
    private final InvoiceItemRepo invoiceItemRepo;
    private final Logger logger = LogManager.getLogger(StatisticServiceImpl.class);
    private static LocalDateTime today;

    public StatisticServiceImpl(ApartmentRepo apartmentRepo, InvoiceRepo invoiceRepo, InvoiceItemRepo invoiceItemRepo) {
        this.apartmentRepo = apartmentRepo;
        this.invoiceRepo = invoiceRepo;
        this.invoiceItemRepo = invoiceItemRepo;
    }

    @Override
    public GeneralOwnerStatistic getGeneralStatistic(Long apartment, Principal principal) {
        logger.info("getGeneralStatistic() -> start, apartmentId: {}", apartment);
        Apartment apartmentByIdAndOwnerEmail = findApartmentByIdAndOwner_Email(apartment, principal.getName());
        BigDecimal balance = apartmentByIdAndOwnerEmail.getBalance();
        String accountNumberStr = formatPersonalAccountNumber(
                apartmentByIdAndOwnerEmail.getPersonalAccount().getAccountNumber());
        BigDecimal averageExpense = getAverageApartmentExpense(apartmentByIdAndOwnerEmail);
        GeneralOwnerStatistic statistic = new GeneralOwnerStatistic(balance, accountNumberStr, averageExpense);
        logger.info("getGeneralStatistic() -> end, apartmentId: {} statistic: {}", apartment, statistic);
        return statistic;
    }

    @Override
    public List<StatisticItem> getExpensePerMonthStatistic(Long apartment, Principal principal) {
        logger.info("getExpensePerMonthStatistic() -> start, apartmentId: {}", apartment);
        today = LocalDateTime.now();
        Apartment apartmentByIdAndOwnerEmail = findApartmentByIdAndOwner_Email(apartment, principal.getName());
        LocalDate startCurrentMonth = LocalDate.of(today.getYear(), today.getMonth(), 1);
        List<StatisticItem> statisticItems = getInvoiceItemsStatistic(apartmentByIdAndOwnerEmail, startCurrentMonth);
        logger.info("getExpensePerMonthStatistic() -> end, apartmentId: {} statisticItems: {}", apartment, statisticItems);
        return statisticItems;
    }

    @Override
    public List<StatisticItem> getExpensePerYearStatistic(Long apartment, Principal principal) {
        logger.info("getExpensePerYearStatistic() -> start, apartmentId: {}", apartment);
        today = LocalDateTime.now();
        Apartment apartmentByIdAndOwnerEmail = findApartmentByIdAndOwner_Email(apartment, principal.getName());
        LocalDate startCurrentMonth = LocalDate.of(today.getYear(), 1, 1);
        List<StatisticItem> statisticItems = getInvoiceItemsStatistic(apartmentByIdAndOwnerEmail, startCurrentMonth);
        logger.info("getExpensePerYearStatistic() -> end, apartmentId: {} statisticItems: {}", apartment, statisticItems);
        return statisticItems;
    }

    @Override
    public List<StatisticDateItem> getExpensePerYearOnMonthStatistic(Long apartmentId, Principal principal) {
        logger.info("getExpensePerYearOnMonthStatistic() -> start, apartmentId: {}", apartmentId);
        today = LocalDateTime.now();
        Apartment apartment = findApartmentByIdAndOwner_Email(apartmentId, principal.getName());
        return IntStream.rangeClosed(1, 12)
                .parallel()
                .mapToObj(month -> {
                    logger.info("getExpensePerYearOnMonthStatistic() -> month: {}", month);
                    LocalDate startMonth = today.toLocalDate().withDayOfMonth(1).minusMonths(12 - month);
                    LocalDate endMonth = startMonth.plusMonths(1);
                    Instant instantStartMonth = getInstantFromLocalDate(startMonth);

                    logger.info("getExpensePerYearOnMonthStatistic() -> find invoices between {} and {}", instantStartMonth, getInstantFromLocalDate(endMonth));
                    List<Invoice> invoices = getInvoiceByApartmentAndDateBetween(
                            apartment,
                            instantStartMonth,
                            getInstantFromLocalDate(endMonth));
                    BigDecimal totalAmount = getTotalAmountInvoices(invoices);
                    logger.info("getExpensePerYearOnMonthStatistic() -> month: {} totalAmount: {}", month, totalAmount);
                    return new StatisticDateItem(instantStartMonth, totalAmount);
                })
                .collect(Collectors.toList());
    }

    private BigDecimal getTotalAmountInvoices(List<Invoice> invoices) {
        logger.info("getTotalAmountInvoices() -> start");
        BigDecimal totalAmountInvoices = invoices.stream()
                .map(Invoice::getPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.info("getTotalAmountInvoices() -> end, totalAmountInvoices: {}", totalAmountInvoices);
        return totalAmountInvoices;
    }

    private List<StatisticItem> getInvoiceItemsStatistic(Apartment apartment, LocalDate dateFrom) {
        logger.info("getInvoiceItemsStatistic() -> start");
        List<StatisticItem> statisticItems;
        List<Invoice> invoices = getInvoiceByApartmentAndDateBetween(
                apartment,
                getInstantFromLocalDate(dateFrom),
                today.atZone(ZoneId.systemDefault()).toInstant()
        );
        statisticItems = getStatisticItemFromInvoiceItems(invoiceItemRepo.findInvoiceItemsByInvoiceIn(invoices));
        logger.info("getInvoiceItemsStatistic() -> end, statisticItems: {}", statisticItems);
        return statisticItems;
    }

    private List<StatisticItem> getStatisticItemFromInvoiceItems(List<InvoiceItem> invoiceItems) {
        logger.info("getStatisticItemFromInvoiceItems() -> start");
        Map<String, List<BigDecimal>> collect = invoiceItems.stream()
                .collect(Collectors.groupingBy(invoiceItem -> invoiceItem.getService().getName(),
                        Collectors.mapping(InvoiceItem::getCost, Collectors.toList())));
        List<StatisticItem> statisticItems = new ArrayList<>();
        collect.forEach((serviceName, bigDecimals) -> {
            BigDecimal servicePaidAmount = bigDecimals.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            statisticItems.add(new StatisticItem(serviceName, servicePaidAmount.toString()));
        });
        logger.info("getStatisticItemFromInvoiceItems() -> end, statisticItems: {}", statisticItems);
        return statisticItems;
    }

    private List<Invoice> getInvoiceByApartmentAndDateBetween(Apartment apartment, Instant dateFrom, Instant dateTo) {
        logger.info("getInvoiceByApartmentAndDateBetween() -> start, apartmentId: {}", apartment.getId());
        List<Invoice> invoicesByApartmentAndCreationDateBetween =
                invoiceRepo.findInvoicesByApartmentAndCreationDateBetween(apartment, dateFrom, dateTo);
        logger.info("getInvoiceByApartmentAndDateBetween() -> end, result is done: {}", invoicesByApartmentAndCreationDateBetween);
        return invoicesByApartmentAndCreationDateBetween;
    }

    private Instant getInstantFromLocalDate(LocalDate localDate) {
        return localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
    }

    private BigDecimal getAverageApartmentExpense(Apartment apartment) {
        logger.info("getAverageApartmentExpense() -> start, apartmentId: {}", apartment.getId());
        List<Invoice> invoicesByApartment = invoiceRepo.findInvoicesByApartment(apartment);
        if (invoicesByApartment.isEmpty()) {
            logger.info("getAverageApartmentExpense() -> end, apartmentId: {} - no invoices found",
                    apartment.getId());
            return BigDecimal.ZERO;
        }

        Map<YearMonth, BigDecimal> monthlyTotalExpenses = invoicesByApartment.stream()
                .collect(Collectors.groupingBy(invoice -> YearMonth.from(invoice.getCreationDate()
                                .atZone(ZoneId.systemDefault()).toLocalDate()),
                        Collectors.mapping(Invoice::getPaid, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        logger.info("getAverageApartmentExpense() -> start calculating, apartmentId: {} has {} invoices",
                apartment.getId(), monthlyTotalExpenses.size());

        BigDecimal totalAverageExpense = monthlyTotalExpenses.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(monthlyTotalExpenses.size()), RoundingMode.HALF_UP);
        logger.info("getAverageApartmentExpense() -> end, apartmentId: {} -> totalAverageExpense: {}",
                apartment.getId(), totalAverageExpense);
        return totalAverageExpense;
    }

    private String formatPersonalAccountNumber(Long personalAccountNumber) {
        logger.info("formatPersonalAccountNumber() -> start, personalAccountNumber: {}", personalAccountNumber);
        String stringNumber = StringUtils.leftPad(personalAccountNumber.toString(), 10, "0");
        return stringNumber.substring(0, 5) + "-" + stringNumber.substring(5, 10);
    }

    private Apartment findApartmentByIdAndOwner_Email(Long apartmentId, String ownerEmail) {
        Optional<Apartment> apartmentByIdAndOwnerEmail = apartmentRepo.findApartmentByIdAndOwner_Email(apartmentId, ownerEmail);
        return apartmentByIdAndOwnerEmail.orElseThrow(() -> {
            logger.error("findApartmentByIdAndOwner_Email() -> Apartment by id: {} and ownerEmail: {} - not found",
                    apartmentId, ownerEmail);
            return new EntityNotFoundException(String.format("Apartment by id: %s and ownerEmail: %s - not found",
                    apartmentId, ownerEmail));
        });
    }
}
