package com.example.myhouse24admin.util;

import com.example.myhouse24admin.model.invoices.XmlInvoiceDto;
import org.apache.commons.io.FileUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
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
import java.io.*;
import java.net.URL;
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

    public byte[] formPdfFile(XmlInvoiceDto xmlInvoiceDto, String template) throws IOException {
        logger.info("formPdfFile - Forming pdf file with template "+template);
        createXmlFile(xmlInvoiceDto);

        InputStream xsltFile = uploadFileUtil.getFileInputStreamByName(template);
        InputStream xmlFile = uploadFileUtil.getFileInputStreamByName("invoice.xml");
        File pdfFile = new File(uploadPath+"/invoice_"+ LocalDate.now()+".pdf");

        try (OutputStream out = new FileOutputStream(pdfFile)) {
            savePdfFile(xsltFile, xmlFile, out);
        } catch (TransformerException | IOException  |SAXException  e) {
            logger.error(e.getMessage());
        }

        byte[] fileBytes = FileUtils.readFileToByteArray(pdfFile);
        pdfFile.delete();

        logger.info("formPdfFile - Pdf file was formed");
        return fileBytes;
    }
    private FopFactory createFopFactory() throws IOException, SAXException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("fop.xconf");
        FopFactory fopFactory  = FopFactory.newInstance(new File(resource.getFile()));
        return fopFactory;
    }
    private void savePdfFile(InputStream xsltFile, InputStream xmlFile, OutputStream out) throws TransformerException, IOException, SAXException {
        FopFactory fopFactory = createFopFactory();
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

        Source src = new StreamSource(xmlFile);
        Result res = new SAXResult(fop.getDefaultHandler());
        transformer.transform(src, res);
    }
    private void createXmlFile(XmlInvoiceDto xmlInvoiceDto) {
        logger.info("createXmlFile - Creating xml file");
        File file = new File(uploadPath+"/invoice.xml");
        try {
            JAXBContext context = JAXBContext.newInstance(XmlInvoiceDto.class);
            Marshaller mar= context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            mar.marshal(xmlInvoiceDto, file);
            uploadFileUtil.saveFile(file.getName(),file);
            file.delete();
            logger.info("createXmlFile - Xml file was created");
        } catch (JAXBException e) {
            logger.error(e.getMessage());
        }
    }
}
