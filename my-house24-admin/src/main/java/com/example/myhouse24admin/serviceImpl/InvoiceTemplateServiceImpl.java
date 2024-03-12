package com.example.myhouse24admin.serviceImpl;

import com.example.myhouse24admin.entity.InvoiceTemplate;
import com.example.myhouse24admin.mapper.InvoiceTemplateMapper;
import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateListRequest;
import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateRequest;
import com.example.myhouse24admin.model.invoiceTemplate.InvoiceTemplateResponse;
import com.example.myhouse24admin.repository.InvoiceTemplateRepo;
import com.example.myhouse24admin.service.InvoiceTemplateService;
import com.example.myhouse24admin.util.UploadFileUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
@Service
public class InvoiceTemplateServiceImpl implements InvoiceTemplateService {
    private final InvoiceTemplateRepo invoiceTemplateRepo;
    private final InvoiceTemplateMapper invoiceTemplateMapper;
    private final UploadFileUtil uploadFileUtil;
    private final ServletContext servletContext;
    private final Logger logger = LogManager.getLogger(InvoiceTemplateServiceImpl.class);

    public InvoiceTemplateServiceImpl(InvoiceTemplateRepo invoiceTemplateRepo,
                                      InvoiceTemplateMapper invoiceTemplateMapper,
                                      UploadFileUtil uploadFileUtil,
                                      ServletContext servletContext) {
        this.invoiceTemplateRepo = invoiceTemplateRepo;
        this.invoiceTemplateMapper = invoiceTemplateMapper;
        this.uploadFileUtil = uploadFileUtil;
        this.servletContext = servletContext;
    }

    @Override
    public List<InvoiceTemplateResponse> getInvoiceTemplatesResponses() {
        logger.info("getInvoiceTemplatesResponses - Getting invoice templates responses");
        List<InvoiceTemplate> invoiceTemplates = invoiceTemplateRepo.findAll();
        List<InvoiceTemplateResponse> invoiceTemplateResponses = invoiceTemplateMapper
                .invoiceTemplateListToInvoiceTemplateResponseList(invoiceTemplates);
        logger.info("getInvoiceTemplatesResponses - Invoice templates responses were got");
        return invoiceTemplateResponses;
    }

    @Override
    public void updateTemplates(InvoiceTemplateListRequest invoiceTemplateListRequest) {
        logger.info("updateTemplates - Updating invoice templates");
        deleteInvoiceTemplates(invoiceTemplateListRequest.getIdsToDelete());
        saveNewInvoiceTemplates(invoiceTemplateListRequest.getInvoiceTemplates());
        logger.info("updateTemplates - Invoice templates were updated");
    }

    private void deleteInvoiceTemplates(List<Long> idsToDelete) {
        if(idsToDelete != null){
            List<InvoiceTemplate> invoiceTemplates = invoiceTemplateRepo.findAllById(idsToDelete);
            for(InvoiceTemplate invoiceTemplate: invoiceTemplates){
                uploadFileUtil.deleteFile(invoiceTemplate.getFile());
            }
            invoiceTemplateRepo.deleteAllById(idsToDelete);
        }
    }
    private void saveNewInvoiceTemplates(List<InvoiceTemplateRequest> invoiceTemplates) {
        if(invoiceTemplates != null) {
            for (InvoiceTemplateRequest invoiceTemplateRequest : invoiceTemplates) {
                String fileName = uploadFileUtil.saveFile(invoiceTemplateRequest.getFile());
                InvoiceTemplate invoiceTemplate = invoiceTemplateMapper
                        .invoiceTemplateRequestToInvoiceTemplate(invoiceTemplateRequest, fileName);
                invoiceTemplateRepo.save(invoiceTemplate);
            }
        }
    }

    @Override
    public void setDefaultInvoice(Long id) {
        logger.info("setDefaultInvoice - Setting invoice template with id "+id+" as default");
        InvoiceTemplate invoiceTemplate = invoiceTemplateRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Invoice template was not found by id "+id));
        invoiceTemplate.setDefault(true);
        invoiceTemplateRepo.save(invoiceTemplate);
        logger.info("setDefaultInvoice - Invoice template was set");
    }

    @Override
    public File getTemplateFile(String fileName) {
        logger.info("getTemplateFile - Getting file by name "+fileName);
        File file = uploadFileUtil.getFileByName(fileName);
        logger.info("getTemplateFile - File was got");
        return file;
    }

    @Override
    public MediaType getMediaTypeForFileName(String fileName) {
        logger.info("getMediaTypeForFileName - Getting media type for file with name "+fileName);
        String mineType = servletContext.getMimeType(fileName);
        MediaType mediaType = MediaType.parseMediaType(mineType);
        logger.info("getMediaTypeForFileName - Media type was got");
        return mediaType;
    }
}
