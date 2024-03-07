package com.example.myhouse24admin.model.houses;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StaffShortRequest {
    @NotNull(message = "validation-not-empty")
    @Min(value = 1, message = "validation-invalid-value")
    private Long id;
    private String firstName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
