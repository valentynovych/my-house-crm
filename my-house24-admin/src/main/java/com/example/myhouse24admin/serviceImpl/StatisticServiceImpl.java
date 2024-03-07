package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.PersonalAccount;
import com.example.myhouse24admin.repository.PersonalAccountRepo;
import com.example.myhouse24admin.service.StatisticService;
import com.example.myhouse24admin.specification.PersonalAccountSpecification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticServiceImpl implements StatisticService {

    private final PersonalAccountRepo personalAccountRepo;

    public StatisticServiceImpl(PersonalAccountRepo personalAccountRepo) {
        this.personalAccountRepo = personalAccountRepo;
    }

    @Override
    public Map<String, String> getPersonalAccountsMetrics() {
        Map<String, String> metrics = new HashMap<>();
        PersonalAccountSpecification byBalanceIsNegative = new PersonalAccountSpecification(Map.of("balance", "arrears"));
        List<PersonalAccount> allNegative = personalAccountRepo.findAll(byBalanceIsNegative);
        BigDecimal accountsBalanceArrears = allNegative.stream()
                .map(personalAccount -> personalAccount.getApartment().getBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        metrics.put("accountsBalanceArrears", accountsBalanceArrears.toString());

        PersonalAccountSpecification byBalanceIsPositive = new PersonalAccountSpecification(Map.of("balance", "overpayment"));
        List<PersonalAccount> allPositive = personalAccountRepo.findAll(byBalanceIsPositive);
        BigDecimal accountsBalanceOverpayments = allPositive.stream()
                .map(personalAccount -> personalAccount.getApartment().getBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        metrics.put("accountsBalanceOverpayments", accountsBalanceOverpayments.toString());

        //TODO add current cash register

        return metrics;
    }
}
