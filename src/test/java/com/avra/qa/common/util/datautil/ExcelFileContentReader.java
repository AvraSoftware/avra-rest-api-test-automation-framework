package com.avra.qa.common.util.datautil;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Component
public class ExcelFileContentReader<T> {

    private static final String TEMPORARY_DOWNLOAD_EXCEL_PATH = "src/test/resources/tempFiles/";
    private String newTempExcelFileName;

    public int getReportExcelSheetCount() throws IOException {
        FileInputStream inputStream = new FileInputStream(new File(TEMPORARY_DOWNLOAD_EXCEL_PATH + newTempExcelFileName));
        Workbook workbook = WorkbookFactory.create(inputStream);

        int sheetCount = workbook.getNumberOfSheets();

        inputStream.close();

        return sheetCount;
    }

    public int getReportExcelFileRowsCount(int sheetIndex) throws IOException {
        FileInputStream inputStream = new FileInputStream(new File(TEMPORARY_DOWNLOAD_EXCEL_PATH + newTempExcelFileName));
        Workbook workbook = WorkbookFactory.create(inputStream);

        Sheet sheet = workbook.getSheetAt(sheetIndex);
        int rowCount = sheet.getPhysicalNumberOfRows();

        inputStream.close();

        return rowCount - 1;
    }

    public int getReportExcelFileColumnsCount(int sheetIndex) throws IOException {
        FileInputStream inputStream = new FileInputStream(new File(TEMPORARY_DOWNLOAD_EXCEL_PATH + newTempExcelFileName));
        Workbook workbook = WorkbookFactory.create(inputStream);

        Sheet sheet = workbook.getSheetAt(sheetIndex);
        int columnsCount = sheet.getRow(0).getLastCellNum();

        inputStream.close();

        return columnsCount;
    }

    public String getReportExcelFileForStringCellData(int sheetIndex, int rowIndex, int columnIndex) throws IOException {
        FileInputStream inputStream = new FileInputStream(new File(TEMPORARY_DOWNLOAD_EXCEL_PATH + newTempExcelFileName));
        Workbook workbook = WorkbookFactory.create(inputStream);

        Sheet sheet = workbook.getSheetAt(sheetIndex);
        String columnsCount = sheet.getRow(rowIndex).getCell(columnIndex).getStringCellValue();

        inputStream.close();

        return columnsCount;
    }

    public Double getReportExcelFileForNumericCellData(int sheetIndex, int rowIndex, int columnIndex) throws IOException {
        FileInputStream inputStream = new FileInputStream(new File(TEMPORARY_DOWNLOAD_EXCEL_PATH + newTempExcelFileName));
        Workbook workbook = WorkbookFactory.create(inputStream);

        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Double columnsCount = sheet.getRow(rowIndex).getCell(columnIndex).getNumericCellValue();

        inputStream.close();

        return columnsCount;
    }

    public List<String> getReportExcelFileForStringCellsData(int sheetIndex, int columnIndex) throws IOException {
        FileInputStream inputStream = new FileInputStream(new File(TEMPORARY_DOWNLOAD_EXCEL_PATH + newTempExcelFileName));
        Workbook workbook = WorkbookFactory.create(inputStream);

        Sheet sheet = workbook.getSheetAt(sheetIndex);

        List<String> cellsData = new LinkedList<>();

        int rowCount = sheet.getPhysicalNumberOfRows() - 1;

        for (int i = 1; i <= rowCount; i++) {
            String cellData = sheet.getRow(i).getCell(columnIndex).getStringCellValue();
            cellsData.add(cellData);
        }
        inputStream.close();

        return cellsData;
    }

    public List<Double> getReportExcelFileForNumericCellsData(int sheetIndex, int columnIndex) throws IOException {
        FileInputStream inputStream = new FileInputStream(new File(TEMPORARY_DOWNLOAD_EXCEL_PATH + newTempExcelFileName));
        Workbook workbook = WorkbookFactory.create(inputStream);

        Sheet sheet = workbook.getSheetAt(sheetIndex);

        List<Double> cellsData = new LinkedList<>();

        int rowCount = sheet.getPhysicalNumberOfRows() - 1;

        for (int i = 1; i <= rowCount; i++) {
            Double cellData = sheet.getRow(i).getCell(columnIndex).getNumericCellValue();
            cellsData.add(cellData);
        }
        inputStream.close();

        return cellsData;
    }

    public List<Date> getReportExcelFileForDateCellsData(int sheetIndex, int columnIndex) throws IOException {
        FileInputStream inputStream = new FileInputStream(new File(TEMPORARY_DOWNLOAD_EXCEL_PATH + newTempExcelFileName));
        Workbook workbook = WorkbookFactory.create(inputStream);

        Sheet sheet = workbook.getSheetAt(sheetIndex);

        List<Date> cellsData = new LinkedList<>();

        int rowCount = sheet.getPhysicalNumberOfRows() - 1;

        for (int i = 1; i <= rowCount; i++) {
            Date cellData = sheet.getRow(i).getCell(columnIndex).getDateCellValue();
            cellsData.add(cellData);
        }
        inputStream.close();

        return cellsData;
    }

    public void deleteTemporaryExcelFiles() {
        File outputPath = new File(TEMPORARY_DOWNLOAD_EXCEL_PATH);

        for (File excelFile : Objects.requireNonNull(outputPath.listFiles())) {
            if (!excelFile.isDirectory() && excelFile.getName().endsWith(".xlsx")) {
                excelFile.delete();
            }
        }
    }

    public void getReportExcelFile(byte[] fileContents, String className) throws IOException {
        File outputFile = new File(TEMPORARY_DOWNLOAD_EXCEL_PATH, getTemporaryExcelName(className));
        outputFile.deleteOnExit();
        OutputStream outStream = new FileOutputStream(outputFile);
        outStream.write(fileContents);
        outStream.close();
    }

    private String getTemporaryExcelName(String className) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmssSSSZ").format(new Date());
        newTempExcelFileName = "tempExcel_" + className + timeStamp + ".xlsx";
        return newTempExcelFileName;
    }
}
