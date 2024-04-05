package com.example.myhouse24admin.util;

import com.example.myhouse24admin.entity.PersonalAccountStatus;
import com.example.myhouse24admin.model.apartments.ApartmentResponse;
import com.example.myhouse24admin.model.personalAccounts.PersonalAccountTableResponse;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PersonalAccountExelGenerator {

    private List<PersonalAccountTableResponse> personalAccounts;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final MessageSource messageSource;
    private final Locale locale;

    public PersonalAccountExelGenerator(List<PersonalAccountTableResponse> personalAccounts, MessageSource messageSource, Locale locale) {
        this.personalAccounts = personalAccounts;
        this.messageSource = messageSource;
        this.locale = locale;
        workbook = new XSSFWorkbook();
    }

    private void writeHeader() {
        sheet = workbook.createSheet("PersonalAccounts");
        Row row = sheet.createRow(0);
        CellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setFillBackgroundColor((short) 0x4F81BD);

        createCell(row,
                0,
                messageSource.getMessage("personal-accounts-label-account-number", new Object[0], locale),
                cellStyle);
        createCell(row,
                1,
                messageSource.getMessage("personal-accounts-label-status", new Object[0], locale),
                cellStyle);
        createCell(row,
                2,
                messageSource.getMessage("personal-accounts-label-apartment", new Object[0], locale),
                cellStyle);
        createCell(row,
                3,
                messageSource.getMessage("personal-accounts-label-house", new Object[0], locale),
                cellStyle);
        createCell(row,
                4,
                messageSource.getMessage("personal-accounts-label-section", new Object[0], locale),
                cellStyle);
        createCell(row,
                5,
                messageSource.getMessage("personal-accounts-label-apartment-owner", new Object[0], locale),
                cellStyle);
        createCell(row,
                6,
                messageSource.getMessage("personal-accounts-label-balance", new Object[0], locale),
                cellStyle);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof PersonalAccountStatus) {
            if (value.equals(PersonalAccountStatus.ACTIVE)) {
                cell.setCellValue(messageSource.getMessage("personal-accounts-label-status-active", new Object[0], locale));
            } else {
                cell.setCellValue(messageSource.getMessage("personal-accounts-label-status-nonactive", new Object[0], locale));
            }
        } else {
            cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);
    }

    private void writeData() {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        for (PersonalAccountTableResponse personalAccount : personalAccounts) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            ApartmentResponse apartment = personalAccount.getApartment();
            String accountNumber = String.format("%010d", personalAccount.getAccountNumber());
            accountNumber = accountNumber.substring(0, 5) + "-" + accountNumber.substring(5);
            createCell(row, columnCount++, accountNumber, style);
            createCell(row, columnCount++, personalAccount.getStatus(), style);
            createCell(row, columnCount++, apartment != null ? StringUtils.leftPad(apartment.getApartmentNumber(), 5, "0") : "-", style);
            createCell(row, columnCount++, apartment != null ? apartment.getHouse().getName() : "-", style);
            createCell(row, columnCount++, apartment != null ? apartment.getSection().getName() : "-", style);
            createCell(row, columnCount++, apartment != null ? apartment.getOwner().fullName() : "-", style);
            createCell(row, columnCount++, apartment != null ? apartment.getBalance() : "-", style);
        }
    }

    public void generateExcelFile(HttpServletResponse response) {
        writeHeader();
        writeData();
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
