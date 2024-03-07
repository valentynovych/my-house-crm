package com.example.myhouse24admin.model.houses;

import com.example.myhouse24admin.model.staff.StaffShortResponse;

import java.util.ArrayList;
import java.util.List;

public class HouseResponse {

    private Long id;
    private String name;
    private String address;
    private String image1;
    private String image2;
    private String image3;
    private String image4;
    private String image5;
    private List<StaffShortResponse> staff = new ArrayList<>();
    private List<SectionResponse> sections = new ArrayList<>();
    private List<FloorResponse> floors = new ArrayList<>();

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

    public List<StaffShortResponse> getStaff() {
        return staff;
    }

    public void setStaff(List<StaffShortResponse> staff) {
        this.staff = staff;
    }

    public List<SectionResponse> getSections() {
        return sections;
    }

    public void setSections(List<SectionResponse> sections) {
        this.sections = sections;
    }

    public List<FloorResponse> getFloors() {
        return floors;
    }

    public void setFloors(List<FloorResponse> floors) {
        this.floors = floors;
    }
}
