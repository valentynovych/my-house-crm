package com.example.myhouse24admin.model.houses;

import com.example.myhouse24admin.validators.fileValidator.FirstFileRequired;
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
    @Size(max = 200, message = "{validation-size-max}")
    private String image1;
    @Size(max = 200, message = "{validation-size-max}")
    private String image2;
    @Size(max = 200, message = "{validation-size-max}")
    private String image3;
    @Size(max = 200, message = "{validation-size-max}")
    private String image4;
    @Size(max = 200, message = "{validation-size-max}")
    private String image5;
    @NotEmpty(message = "{validation-list-not-empty}")
    private List<Long> staffIds = new ArrayList<>();
    @Valid
    private List<SectionRequest> sections = new ArrayList<>();
    @Valid
    private List<FloorRequest> floors = new ArrayList<>();
//    @FirstFileRequired
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

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    public String getImage5() {
        return image5;
    }

    public void setImage5(String image5) {
        this.image5 = image5;
    }

    public List<Long> getStaffIds() {
        return staffIds;
    }

    public void setStaffIds(List<Long> staffIds) {
        this.staffIds = staffIds;
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

