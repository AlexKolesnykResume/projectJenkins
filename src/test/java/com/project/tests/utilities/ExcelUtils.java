package com.project.tests.utilities;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ExcelUtils {


    private static Sheet excelWSheet;
    private static Workbook workbook;
    ArrayList<String> xlHeaderList = new ArrayList<>();
    ArrayList<String> tdTempValueList = new ArrayList<>();
    ArrayList<String> tdValueList = new ArrayList<>();
    FileInputStream file;
    String key = "";
    Row row;
    Cell cell;

    public void print(String printText) {
        System.out.println(printText);
    }

    //Used to read Workbook and report Sheet
    private Sheet readWorkSheet(String filePath, String sheetName) throws IOException, InvalidFormatException {
        try {
            file = new FileInputStream(new File(filePath));
            workbook = WorkbookFactory.create(file);
            excelWSheet = workbook.getSheet(sheetName);
        } catch (FileNotFoundException e) {
            Assertions.fail("Exception in the 'readWorkSheet' method: " + e);
            e.printStackTrace();
        }
        return excelWSheet;
    }

    //ReadTestData ---> readExcelRowData
    //This method is used to read the Excel Row with Respect to RowFlag
    public HashMap<String, String> readExcelRowData(String filePath, String sheetName, String rowFlag) {
        HashMap<String, String> excelRowData = new HashMap<>();
        try {
            excelWSheet = readWorkSheet(filePath, sheetName);
            xlHeaderList = readExcelHeaderList(filePath, sheetName);
            int flagRowNum = getRowNumForRowFlag(filePath, sheetName, rowFlag);
            row = excelWSheet.getRow(flagRowNum);
            if (row.getRowNum() == flagRowNum) {
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    cell.setCellType(CellType.STRING);
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            Double a = cell.getNumericCellValue();
                            key = a.toString();
                            break;
                        case STRING:
                            key = cell.getStringCellValue();
                            break;
                    }
                    tdTempValueList.add(key);
                }
            }
            for (int i = 0; i < tdTempValueList.size(); i++) {
                String s2 = tdTempValueList.get(i);
                if (s2 != null) {
                    s2 = s2.contains(".0") ? s2.replace(".0", "") : s2;
                }
                tdValueList.add(s2);
            }
            if (xlHeaderList.size() == tdValueList.size()) {
                for (int i = 0; i < xlHeaderList.size(); i++) {
                    excelRowData.put(xlHeaderList.get(i), tdValueList.get(i));
                }
                Iterator iter = excelRowData.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry or = (Map.Entry) iter.next();
                }
            } else {
                print("Column count is not equal for header and data. " + "Header contains: " + xlHeaderList.size() + " elements and column contains: " + tdValueList.size() + " elements");

            }
        } catch (IOException e) {
            excelRowData = null;
            print("Exception in 'readExcelRowData' method: " + e);
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } finally {
            try {
                tdValueList.clear();
                tdTempValueList.clear();
                xlHeaderList.clear();
                file.close();
            } catch (IOException e) {
                print("Exception trying to close the file in [readExcelRowData] method: " + e);
            }
        }
        return excelRowData;
    }

    //This Method is used to get Header list(Row zero records in the sheet)
    public ArrayList<String> readExcelHeaderList(String filePath, String sheetName) throws IOException, InvalidFormatException {
        try {
            excelWSheet = readWorkSheet(filePath, sheetName);
            Iterator<Row> rowIterator = excelWSheet.iterator();
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        key = cell.getStringCellValue();
                        xlHeaderList.add(key);
                    }
                }
            }
        } catch (Exception e) {
            print("Exception in 'readExcelHeaderList' method: " + e);
            xlHeaderList = null;
        }
        return xlHeaderList;
    }

    //This method is used to get the number of Rows present in the whole sheet (Rows which are not Empty)
    public int getRowCount(String filePath, String sheetName) throws InvalidFormatException {
        int rowCount = 0;
        try {
            excelWSheet = readWorkSheet(filePath, sheetName);
            rowCount = excelWSheet.getLastRowNum();
        } catch (IOException e) {
            print("Exception in 'getRowCount' method: " + e);
            rowCount = 0;
        }
        return rowCount;
    }

    //This method returns the Row number in which the RowFlag is present
    public int getRowNumForRowFlag(String filePath, String sheetName, String rowFlag) throws IOException, InvalidFormatException {
        int rowFlagNum = 0;
        try {
            excelWSheet = readWorkSheet(filePath, sheetName);
            Iterator<Row> rowIterator = excelWSheet.iterator();
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                if (row.getRowNum() != 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        cell.setCellType(CellType.STRING);
                        key = cell.getStringCellValue();
                        if (key.equals(rowFlag)) {
                            rowFlagNum = row.getRowNum();
                            return rowFlagNum;
                        }
                    }
                }
            }
        } catch (Exception e) {
            print("Exception in 'getRowNumForRowFlag' method: " + e);
            rowFlagNum = 0;
        }
        return rowFlagNum;
    }

    //getColumnNum --> getExcelColumnPosition
    //This method is used to give the column position with respect to the cell Value Match (First Occurrence)
    public int getExcelColumnPosition(String filePath, String sheetName, String cellValue) throws IOException, InvalidFormatException {
        int colPosition = 0;
        try {
            excelWSheet = readWorkSheet(filePath, sheetName);
            Iterator<Row> rowIterator = excelWSheet.iterator();
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    cell.setCellType(CellType.STRING);
                    key = cell.getStringCellValue();
                    colPosition = colPosition + 1;
                    if (key.equals(cellValue)) {
                        print("Column No.: " + colPosition);
                        return colPosition;
                    }
                }
            }
        } catch (Exception e) {
            print("Exception in 'getExcelColumnPosition' method: " + e);
            colPosition = 0;
        }
        return colPosition;
    }

    //This method is used to set cell Value is a particular place
    public void setCellData(String filePath, String sheetName, int rowNum, int colPosition, String result) throws Exception {
        FileOutputStream fileOut = new FileOutputStream(filePath);
        try {
            excelWSheet = readWorkSheet(filePath, sheetName);
            row = excelWSheet.getRow(rowNum);
            cell = row.getCell(colPosition, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell == null) {
                cell = row.createCell(colPosition);
                cell.setCellValue(result);
                print("Updating Cell value in Sheet");
            } else {
                cell.setCellValue(result);
                print("Updating Cell value in Sheet");
            }
            workbook.write(fileOut);
        } catch (Exception e) {
            print(e.getMessage());
            Assertions.fail("Exception in 'setCellData' method: " + e);
        } finally {
            fileOut.flush();
            fileOut.close();
        }
    }

    //Do we need this? used in Polar ICUE
    //This method will return the Cell Values(Headers) of particular Color LightBlue
    public ArrayList<String> returnHeaderColorLightBlue(String filePath, String sheetName) throws IOException, InvalidFormatException {
        try {
            excelWSheet = readWorkSheet(filePath, sheetName);
            Iterator<Row> rowIterator = excelWSheet.iterator();
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        short[] color = getRGBColorCode(cell.getCellStyle().getFillForegroundColor());
                        if ((color[0] == 153) & (color[1] == 204) & (color[2] == 255)) {
                            key = cell.getStringCellValue();
                            print("Key: " + key);
                            xlHeaderList.add(key);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            print("Exception in 'returnHeaderColorLightBlue' method: " + e);
            xlHeaderList = null;
        }
        return xlHeaderList;
    }

    //returnTestDataColumns --> returnHeaderColorYellow
    public ArrayList<String> returnHeaderColorYellow(String filePath, String sheetName) throws IOException, InvalidFormatException {
        try {
            excelWSheet = readWorkSheet(filePath, sheetName);
            Iterator<Row> rowIterator = excelWSheet.iterator();
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        short[] color = getRGBColorCode(cell.getCellStyle().getFillForegroundColor());
                        if ((color[0] == 255) & (color[1] == 255) & (color[2] == 0)) {
                            key = cell.getStringCellValue();
                            print("Key: " + key);
                            xlHeaderList.add(key);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            print("Exception in 'returnHeaderColorYellow' method: " + e);
            xlHeaderList = null;
        }
        return xlHeaderList;
    }

    //enum IndexedColors{RED,BLUE}
    //getColorPattern --> getRGBColorCode
    //This method is used to generate RGB color coding triplet of the cell with respect to properties of the cell
    private short[] getRGBColorCode(short colorIndex) {
        short[] triplet = null;
//        short[] color = new IndexedColors(colorIndex).getIndex();
        HSSFPalette palette = null; // workbook.getCustomPalette();
        HSSFColor color = palette.getColor(colorIndex);
        triplet = color.getTriplet();
        return triplet;
    }
}
