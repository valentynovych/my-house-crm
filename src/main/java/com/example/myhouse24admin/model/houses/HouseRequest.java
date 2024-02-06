package com.example.myhouse24admin.model.houses;

import com.example.myhouse24admin.validators.fileValidator.FileExtension;
import com.example.myhouse24admin.validators.fileValidator.FileRequired;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record HouseRequest(Long id,
                           @NotEmpty(message = "{validation-not-empty}")
                           @Size(max = 100, message = "{validation-size-max}")
                           String name,
                           @NotEmpty(message = "{validation-not-empty}")
                           @Size(max = 150, message = "{validation-size-max}")
                           String address,
                           @Size(max = 200, message = "{validation-size-max}")
                           String image1,
                           @Size(max = 200, message = "{validation-size-max}")
                           String image2,
                           @Size(max = 200, message = "{validation-size-max}")
                           String image3,
                           @Size(max = 200, message = "{validation-size-max}")
                           String image4,
                           @Size(max = 200, message = "{validation-size-max}")
                           String image5,
                           @NotEmpty(message = "{validation-list-not-empty}")
                           List<Long> staffIds,
                           @Valid
                           List<SectionRequest> sections,
                           @Valid
                           List<FloorRequest> floors,
                           @FileExtension
                           @FileRequired
                           List<MultipartFile> images) {
}

