package com.example.myhouse24admin.model.tariffs;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

public class TariffRequestWrap {
    @Valid
    private TariffRequest tariffRequest;
    private List<Long> tariffItemToDelete = new ArrayList<>();

    public TariffRequestWrap() {
    }

    public TariffRequest getTariffRequest() {
        return tariffRequest;
    }

    public void setTariffRequest(TariffRequest tariffRequest) {
        this.tariffRequest = tariffRequest;
    }

    public List<Long> getTariffItemToDelete() {
        return tariffItemToDelete;
    }

    public void setTariffItemToDelete(List<Long> tariffItemToDelete) {
        this.tariffItemToDelete = tariffItemToDelete;
    }
}
