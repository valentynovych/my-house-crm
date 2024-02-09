package com.example.myhouse24admin.model.houses;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SectionRequest {
    Long id;
    @NotEmpty(message = "{validation-not-empty}")
    @Size(max = 100, message = "{validation-size-max}")
    String name;
    boolean deleted;
    @NotEmpty(message = "{validation-not-empty}")
    @Pattern(regexp = "\\d{1,3}-\\d{1,3}", message = "{validation-apartment-numbers-invalid-pattern}")
    String rangeApartmentNumbers;


    public SectionRequest() {
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getRangeApartmentNumbers() {
        return rangeApartmentNumbers;
    }

    public void setRangeApartmentNumbers(String rangeApartmentNumbers) {
        this.rangeApartmentNumbers = rangeApartmentNumbers;
    }
}
