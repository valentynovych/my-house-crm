package com.example.myhouse24rest.model.apartment;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ApartmentShortResponsePage extends PageImpl<ApartmentShortResponse> {
    public ApartmentShortResponsePage(List<ApartmentShortResponse> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }
}
