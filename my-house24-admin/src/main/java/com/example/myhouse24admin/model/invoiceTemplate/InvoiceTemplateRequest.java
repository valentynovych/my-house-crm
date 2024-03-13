package com.example.myhouse24admin.model.invoiceTemplate;

import com.example.myhouse24admin.validators.fileValidator.invoiceTemplate.TemplateNotEmpty;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class InvoiceTemplateRequest {
    @NotBlank(message = "{validation-not-empty}")
    private String name;
    @TemplateNotEmpty(message = "{validation-template-file-required}")
    private MultipartFile file;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
