package com.example.myhouse24admin.util;

import com.example.myhouse24admin.model.invoices.XmlInvoiceDto;
import org.apache.fop.apps.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;

@Component
public class PdfGenerator {
    private final UploadFileUtil uploadFileUtil;
    @Value("${upload.path}")
    private String uploadPath;
    private final Logger logger = LogManager.getLogger(PdfGenerator.class);
    public PdfGenerator(UploadFileUtil uploadFileUtil) {
        this.uploadFileUtil = uploadFileUtil;
    }

    public File formPdfFile(XmlInvoiceDto xmlInvoiceDto, String template) {
        logger.info("formPdfFile - Forming pdf file with template "+template);
        createXmlFile(xmlInvoiceDto);
        File xsltFile = uploadFileUtil.getFileByName(template);
        File xmlFile = uploadFileUtil.getFileByName("invoice.xml");
        File pdfFile = new File(uploadPath+"/invoice_"+ LocalDate.now()+".pdf");
        FopFactory fopFactory = null;
        try {
            fopFactory = FopFactory.newInstance(new File("classpath:/fop.xconf"));
        } catch (SAXException | IOException e) {
            logger.error(e.getMessage());
        }
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

        try (OutputStream out = new FileOutputStream(pdfFile)) {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

            Source src = new StreamSource(xmlFile);
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);
        } catch (TransformerException | IOException | FOPException e) {
            logger.error(e.getMessage());
        }
        logger.info("formPdfFile - Pdf file was formed");
        return uploadFileUtil.getFileByName("/invoice_"+LocalDate.now()+".pdf");
    }
    private void createXmlFile(XmlInvoiceDto xmlInvoiceDto) {
        logger.info("createXmlFile - Creating xml file");
        File file = uploadFileUtil.getFileByName("invoice.xml");
        if(file.exists()){
            uploadFileUtil.deleteFile("invoice.xml");
        }
        try {
            JAXBContext context = JAXBContext.newInstance(XmlInvoiceDto.class);
            Marshaller mar= context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            mar.marshal(xmlInvoiceDto, new File(uploadPath+"/invoice.xml"));
            logger.info("createXmlFile - Xml file was created");
        } catch (JAXBException e) {
            logger.error(e.getMessage());
        }
    }
}
