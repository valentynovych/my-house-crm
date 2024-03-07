package com.example.myhouse24admin.model.houses;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class FloorRequest {
    Long id;
    @NotEmpty(message = "{validation-not-empty}")
    @Size(max = 100, message = "{validation-size-max}")
    String name;
    boolean deleted;

    public FloorRequest() {
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
}
