package com.example.myhouse24admin.model.houses;

import com.example.myhouse24admin.model.staff.StaffShortResponse;
import com.example.myhouse24admin.validators.fileValidator.ImageExtension;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class HouseEditRequest {
    private Long id;
    @NotEmpty(message = "{validation-not-empty}")
    @Size(max = 100, message = "{validation-size-max}")
    private String name;
    @NotEmpty(message = "{validation-not-empty}")
    @Size(max = 150, message = "{validation-size-max}")
    private String address;
    @NotEmpty(message = "{validation-list-not-empty}")
    private List<StaffShortRequest> staff = new ArrayList<>();
    @Valid
    private List<SectionRequest> sections = new ArrayList<>();
    @Valid
    private List<FloorRequest> floors = new ArrayList<>();
    private List<@ImageExtension MultipartFile> images;

    public HouseEditRequest() {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<StaffShortRequest> getStaff() {
        return staff;
    }

    public void setStaff(List<StaffShortRequest> staff) {
        this.staff = staff;
    }

    public List<SectionRequest> getSections() {
        return sections;
    }

    public void setSections(List<SectionRequest> sections) {
        this.sections = sections;
    }

    public List<FloorRequest> getFloors() {
        return floors;
    }

    public void setFloors(List<FloorRequest> floors) {
        this.floors = floors;
    }

    public List<MultipartFile> getImages() {
        return images;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }
}

