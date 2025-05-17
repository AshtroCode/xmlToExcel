package org.turnxcel.turnxcel.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Component
public class ConfigXmlToExcel {
    private static final Logger logger = LoggerFactory.getLogger(ConfigXmlToExcel.class);

    public ByteArrayOutputStream writeExcel(Map<String, String> parameterData) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("XML Data");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("S No.");
            headerRow.createCell(1).setCellValue("Xpath/Parameter");
            headerRow.createCell(2).setCellValue("Value");
            headerRow.createCell(3).setCellValue("Identifier");
            CellStyle headerCellStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                headerRow.getCell(i).setCellStyle(headerCellStyle);
                sheet.setColumnWidth(i, 20 * 256);
            }

            int rowNum = 1;
            for (Map.Entry<String, String> entry : parameterData.entrySet()) {
                Row row = sheet.createRow(rowNum++);
                String[] values = entry.getValue().split(";", 3);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(values.length > 0 ? values[0] : "");
                row.createCell(2).setCellValue(values.length > 1 ? values[1] : "");
                row.createCell(3).setCellValue(values.length > 2 ? values[2] : "");
            }

            sheet.createFreezePane(0, 1);
            workbook.write(outputStream);
            logger.info("Configuration XML data has been successfully converted to Excel format");
        } catch (IOException e) {
            logger.error("Error writing to Excel: {}", e.getMessage());
        }

        return outputStream;
    }
}
