package com.example.myhouse24admin.util;

import com.example.myhouse24admin.entity.CashSheetType;
import com.example.myhouse24admin.entity.PaymentType;
import com.example.myhouse24admin.model.cashRegister.CashSheetTableResponse;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class CashSheetTableExelGenerator {
    private final List<CashSheetTableResponse> cashSheetTableResponses;
    private final MessageSource messageSource;
    private final Locale locale;
    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private int rowCounter = 0;

    public CashSheetTableExelGenerator(List<CashSheetTableResponse> responseList,
                                       MessageSource messageSource,
                                       Locale locale) {
        cashSheetTableResponses = responseList;
        this.messageSource = messageSource;
        this.locale = locale;
        workbook = new XSSFWorkbook();
    }

    public void generateExcelFile(HttpServletResponse response) {
        writeHeader();
        writeData();
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createCell(Row row, int columnCount, String value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
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

    private void writeHeader() {
        sheet = workbook.createSheet(messageSource.getMessage("menu-item-master-request", new Object[0], locale));
        Row row = sheet.createRow(0);
        CellStyle cellStyle = createHeaderStyle();
        createCell(row,
                0,
                messageSource.getMessage("cash-register-label-account-number", new Object[0], locale),
                cellStyle);
        createCell(row,
                1,
                messageSource.getMessage("cash-register-label-date-of-create", new Object[0], locale),
                cellStyle);
        createCell(row,
                2,
                messageSource.getMessage("cash-register-label-status", new Object[0], locale),
                cellStyle);
        createCell(row,
                3,
                messageSource.getMessage("cash-register-label-payment-item-name", new Object[0], locale),
                cellStyle);
        createCell(row,
                4,
                messageSource.getMessage("cash-register-label-owner", new Object[0], locale),
                cellStyle);
        createCell(row,
                5,
                messageSource.getMessage("cash-register-label-personal-account", new Object[0], locale),
                cellStyle);
        createCell(row,
                6,
                messageSource.getMessage("cash-register-label-payment-type", new Object[0], locale),
                cellStyle);
        createCell(row,
                7,
                messageSource.getMessage("cash-register-label-amount", new Object[0], locale),
                cellStyle);
        rowCounter++;
    }

    private void writeData() {
        CellStyle style = createValueStyle();
        BigDecimal sumProcessedIncomes = BigDecimal.ZERO;
        BigDecimal sumProcessedExpense = BigDecimal.ZERO;

        Row row;
        for (CashSheetTableResponse response : cashSheetTableResponses) {
            int columCount = 0;
            row = sheet.createRow(rowCounter);
            createCell(row, columCount++, response.getSheetNumber(), style);
            String creationDate = response.getCreationDate()
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            createCell(row, columCount++, creationDate, style);
            String processedLabel;
            if (response.isProcessed()) {
                processedLabel = messageSource.getMessage("cash-register-label-sheet-status-confirmed", new Object[0], locale);
                if (response.getSheetType().equals(CashSheetType.INCOME))
                    sumProcessedIncomes = sumProcessedIncomes.add(response.getAmount());
                if (response.getSheetType().equals(CashSheetType.EXPENSE))
                    sumProcessedExpense = sumProcessedExpense.add(response.getAmount());
            } else {
                processedLabel = messageSource.getMessage("cash-register-label-sheet-status-not-confirmed", new Object[0], locale);
            }
            createCell(row, columCount++, processedLabel, style);
            createCell(row, +columCount++, response.getPaymentItem().getName(), style);
            createCell(row, columCount++, response.getApartmentOwner() != null
                            ? response.getApartmentOwner().fullName()
                            : "-",
                    style);

            String accountNumber = "-";
            if (response.getPersonalAccount() != null) {
                accountNumber = String.format("%010d", response.getPersonalAccount().getAccountNumber());
                accountNumber = accountNumber.substring(0, 5) + "-" + accountNumber.substring(5);
            }

            createCell(row, columCount++, accountNumber, style);
            PaymentType paymentType = response.getPaymentItem().getPaymentType();
            String paymentTypeLabel = paymentType.equals(PaymentType.INCOME)
                    ? messageSource.getMessage("payment-item-label-type-income", new Object[0], locale)
                    : messageSource.getMessage("payment-item-label-type-expense", new Object[0], locale);
            createCell(row, columCount++, paymentTypeLabel, style);
            createCell(row, columCount, response.getAmount().toString(), style);
            rowCounter++;
        }

        Row rowIncomes = sheet.createRow(++rowCounter);
        createCell(rowIncomes, 6,
                messageSource.getMessage("cash-register-label-processed-income-amount", new Object[0], locale),
                style);
        createCell(rowIncomes, 7, sumProcessedIncomes.toString(), style);
        Row rowExpense = sheet.createRow(++rowCounter);
        createCell(rowExpense, 6,
                messageSource.getMessage("cash-register-label-processed-expense-amount", new Object[0], locale),
                style);
        createCell(rowExpense, 7, sumProcessedExpense.toString(), style);
    }
}
