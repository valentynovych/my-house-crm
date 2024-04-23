package com.example.myhouse24admin.util;

import com.example.myhouse24admin.model.cashRegister.CashSheetResponse;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CashSheetViewExelGenerator {
    private final CashSheetResponse sheetResponse;
    private final MessageSource messageSource;
    private final Locale locale;
    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private int rowCounter = 0;

    public CashSheetViewExelGenerator(CashSheetResponse sheetResponse, MessageSource messageSource, Locale locale) {
        this.sheetResponse = sheetResponse;
        this.messageSource = messageSource;
        this.locale = locale;
        workbook = new XSSFWorkbook();
    }

    public void generateExcelFile(HttpServletResponse response) {
        writeData();
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createCell(Row row, int columnNumber, String value, CellStyle style) {
        sheet.autoSizeColumn(columnNumber);
        Cell cell = row.createCell(columnNumber);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private CellStyle createHeaderStyle() {
        CellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    private CellStyle createValueStyle() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setLocked(true);
        return style;
    }

    private void createRow(String leftHeader, CellStyle leftHeaderStyle, String rightValue, CellStyle rightValueStyle) {
        Row row = sheet.createRow(rowCounter);
        createCell(row, 0, leftHeader, leftHeaderStyle);
        createCell(row, 1, rightValue, rightValueStyle);
        rowCounter++;
    }

    private void writeData() {
        sheet = workbook.createSheet("CashSheet_" + sheetResponse.getSheetNumber());
        CellStyle headerStyle = createHeaderStyle();
        CellStyle style = createValueStyle();
        createRow(messageSource.getMessage("cash-register-label-account-number", new Object[0], locale),
                headerStyle, sheetResponse.getSheetNumber(), style);

        String creationDate = LocalDate.ofInstant(sheetResponse.getCreationDate(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        createRow(messageSource.getMessage("cash-register-label-date-of-create", new Object[0], locale),
                headerStyle, creationDate, style);

        if (sheetResponse.getPersonalAccount() != null) {
            createRow(messageSource.getMessage("cash-register-label-apartment-owner", new Object[0], locale),
                    headerStyle, sheetResponse.getPersonalAccount().apartmentOwner().fullName(), style);
            createRow(messageSource.getMessage("cash-register-label-personal-account", new Object[0], locale),
                    headerStyle, sheetResponse.getPersonalAccount().accountNumber(), style);
        }

        createRow(messageSource.getMessage("cash-register-label-payment-item-name", new Object[0], locale),
                headerStyle, sheetResponse.getPaymentItem().getName(), style);

        if (sheetResponse.getInvoice() != null) {
            String dividerFrom = messageSource.getMessage("cash-register-label-divider-from", new Object[0], locale);
            String invoiceCreationDate = LocalDate.ofInstant(sheetResponse.getInvoice().creationDate(), ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String value = String.format("%s %s %s", sheetResponse.getInvoice().number(), dividerFrom, invoiceCreationDate);
            createRow(messageSource.getMessage("cash-register-label-invoice", new Object[0], locale),
                    headerStyle, value, style);
        }

        String staffFullName = sheetResponse.getStaff().getFirstName() + " " + sheetResponse.getStaff().getLastName();
        createRow(messageSource.getMessage("cash-register-label-staff", new Object[0], locale),
                headerStyle, staffFullName, style);

        createRow(messageSource.getMessage("cash-register-label-amount", new Object[0], locale),
                headerStyle, sheetResponse.getAmount().toString(), style);

        createRow(messageSource.getMessage("cash-register-label-comment", new Object[0], locale),
                headerStyle, sheetResponse.getComment(), style);
    }
}
