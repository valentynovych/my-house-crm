package com.example.myhouse24admin.model.statistic;

public record StatisticGeneralResponse(
        Integer countApartments,
        Integer countHouses,
        Integer countActiveApartmentOwners,
        Integer countPersonalAccounts,
        Integer countMasterRequestsInProgress,
        Integer countMasterRequestsNew
) {
}
