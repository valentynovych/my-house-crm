package com.example.myhouse24admin.model.siteManagement.mainPage;

import com.example.myhouse24admin.validators.fileValidator.mainPage.MainBlockImageNotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
@MainBlockImageNotEmpty(
        id="id",
        image="image",
        message = "{validation-image-required}"
)
public class MainPageBlockRequest {
    private Long id;
    @NotBlank(message = "{validation-not-empty}")
    private String title;
    @NotBlank(message = "{validation-not-empty}")
    @Size(max = 300, message = "{validation-size-max}")
    private String description;
    private MultipartFile image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
