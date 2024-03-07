package com.example.myhouse24admin.model.tariffs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class TariffRequest {

    private Long id;
    @Size(min = 3, max = 100, message = "{validation-size-min-max}")
    @NotEmpty(message = "{validation-not-empty}")
    private String name;
    @Size(min = 3, max = 350, message = "{validation-size-min-max}")
    @NotEmpty(message = "{validation-not-empty}")
    private String description;
    @Valid
    private List<TariffItemRequest> tariffItems;

    public TariffRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TariffItemRequest> getTariffItems() {
        return tariffItems;
    }

    public void setTariffItems(List<TariffItemRequest> tariffItems) {
        this.tariffItems = tariffItems;
    }
}
