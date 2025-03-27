package com.example.demo.utils;

import com.example.demo.models.ExchangeRate;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {

    public static void generateExcel(HttpServletResponse response, List<ExchangeRate> rates) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Exchange Rates");

        // Створюємо заголовок таблиці
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Currency", "Rate", "Date"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(getHeaderStyle(workbook)); // Стиль заголовку
        }

        // Додаємо рядки з даними
        int rowNum = 1;
        for (ExchangeRate rate : rates) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rate.getId());
            row.createCell(1).setCellValue(rate.getCurrency());
            row.createCell(2).setCellValue(rate.getRate());
            row.createCell(3).setCellValue(rate.getDate().toString());
        }

        // Записуємо файл у відповідь
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // Метод для стилізації заголовків (жирний текст)
    private static CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}