package com.jiaokang.progress.excel;

import static com.jiaokang.progress.Enter.EXCEL_PATH;
import static com.jiaokang.progress.Enter.NEED_TRANSLATE_EXCEL_PATH;

import com.jiaokang.progress.Enter;
import com.jiaokang.progress.LocalConstants;
import com.jiaokang.progress.TaskDataQueue;
import com.jiaokang.progress.entities.StringLocalData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by jiaokang on 2022/2/25
 */
public class ExcelWriter {
    private final Logger logger = LogManager.getLogger();
    private Workbook mWorkBook = null;
    /**
     * 需要翻译的工作表
     */
    private Workbook mNeedTranslateBook = null;

    public void init() {
        mWorkBook = new XSSFWorkbook();
        mNeedTranslateBook = new XSSFWorkbook();
    }

    private int processCount = 0;

    public void startWrite() {
        try {
            long start = System.currentTimeMillis();
            init();
            long past = System.currentTimeMillis();
            while (!TaskDataQueue.isEmpty() || !Enter.isParseEnd.get()) {
                if (processCount >0 && processCount % 10000 == 0) {
                    long dur = System.currentTimeMillis() - past;
                    int size = TaskDataQueue.getSize();
                    logger.error("process 10000 data cast " + dur + " ms" + " , all count is " + processCount);
                    logger.error("left size " + size);
                    past = System.currentTimeMillis();
                }
                StringLocalData data = TaskDataQueue.getData();
                if (data == null) {
                    continue;
                }
                processCount++;
                writeData(data);
            }
            long writeEndTime = System.currentTimeMillis();
            long cast = writeEndTime - start;
            logger.error("process app "+processCount+" data cast " + cast + " ms");
            filterAllData();
            long filterTime = System.currentTimeMillis() - writeEndTime;
            logger.error("filter "+processCount+" data cast " + filterTime + " ms");
            mNeedTranslateBook.write(new FileOutputStream((NEED_TRANSLATE_EXCEL_PATH)));
            mNeedTranslateBook.close();
            mWorkBook.write(new FileOutputStream(EXCEL_PATH));
            mWorkBook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void filterAllData() {
        int numberOfSheets = mWorkBook.getNumberOfSheets();
        for (int i= 0;i<numberOfSheets;i++){
            Sheet sheet = mWorkBook.getSheetAt(i);
            logger.error("filter sheet "+sheet.getSheetName());
            filterSheet(sheet);
        }
    }

    private void filterSheet(Sheet allDataSheet) {
        String sheetName = allDataSheet.getSheetName();
        //要写入的表格
        Sheet needTranslateBookSheet = mNeedTranslateBook.createSheet(sheetName);
        Row headerRow = needTranslateBookSheet.createRow(0);
        CellStyle headerStyle = mNeedTranslateBook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = mNeedTranslateBook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 14);
        font.setBold(true);
        headerStyle.setFont(font);
        for (int i=0 , lgh = LocalConstants.NEED_TRANSLATE_SHEET.length;i<lgh;i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(LocalConstants.NEED_TRANSLATE_SHEET[i]);
            cell.setCellStyle(headerStyle);
        }
        int rowNum = allDataSheet.getLastRowNum();
        int englishIndex = LocalConstants.COL_LIST.indexOf(LocalConstants.EN);
        Row allDataHeaderRow = allDataSheet.getRow(0);
        StringBuilder missLocal = new StringBuilder();
        //遍历数据
        for (int i=1;i<=rowNum;i++){ //第一行是表头，从第二行开始
            Row row = allDataSheet.getRow(i);
            //当前行的string key
            missLocal.setLength(0);
            String key = row.getCell(0).getStringCellValue();
            //遍历一行
            for (int j = 0,len = LocalConstants.COL_LIST.size() - 1;j<len;j++){ //去除最后一行quota
                Cell cell = row.getCell(j);
                String cellValue = cell.getStringCellValue();
                if (cellValue == null || cellValue.isEmpty() || cellValue.trim().isEmpty()){
                    String local = allDataHeaderRow.getCell(j).getStringCellValue();
                    missLocal.append(local);
                    missLocal.append(',');
                }
            }
            if (missLocal.length() > 0){
                missLocal.deleteCharAt(missLocal.length() - 1);
                Row newTranslateRow = needTranslateBookSheet.createRow(needTranslateBookSheet.getLastRowNum() + 1);
                Cell nameCell = newTranslateRow.createCell(0);
                nameCell.setCellValue(key);
                Cell engCell = newTranslateRow.createCell(1);
                engCell.setCellValue(row.getCell(englishIndex).getStringCellValue());
                Cell missLocalCell = newTranslateRow.createCell(2);
                missLocalCell.setCellValue(missLocal.toString());
            }
        }
    }

    private void writeData(StringLocalData data) {
        if (mWorkBook == null) return;
        String module = data.getModule();
        String local = data.getLocal();
        String key = data.getKey();
        String dataValue = data.getValue();
        Sheet sheet = getSheet(module);
        int index = getKeyIndex(sheet, key);
        Row row;
        if (index == -1) {
            int lastRowNum = sheet.getLastRowNum() + 1;
            row = sheet.createRow(lastRowNum);
            for (int i = 0, len = LocalConstants.COL_LIST.size(); i < len; i++) {
                row.createCell(i);
            }
            row.getCell(0).setCellValue(key);
        } else {
            row = sheet.getRow(index);
        }
        int cellIndex = LocalConstants.COL_LIST.indexOf(local);
        if (cellIndex == -1) {
            short lastCellNum = row.getLastCellNum();
            Cell cell = row.getCell(lastCellNum - 1);
            cell.setCellValue(local + " : " + dataValue);
            return;
        }
        Cell cell = row.getCell(cellIndex);
        cell.setCellValue(dataValue);
    }

    private int getKeyIndex(Sheet sheet, String key) {
        int rowNum = sheet.getLastRowNum();
        for (int i = 1; i <= rowNum; i++) { //去除表头一行
            Row row = sheet.getRow(i);
            Cell cell = row.getCell(0);
            if (cell == null) {
                return -1;
            }
            String stringCellValue = cell.getStringCellValue();
            if (key.equals(stringCellValue)) {
                return i;
            }
        }
        return -1;
    }

    private Sheet getSheet(String sheetName) {
        Sheet sheet = mWorkBook.getSheet(sheetName);
        if (sheet == null) {
            sheet = mWorkBook.createSheet(sheetName);
            Row row = sheet.createRow(0);
            List<String> colName = LocalConstants.COL_LIST;
            CellStyle headerStyle = mWorkBook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = mWorkBook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 14);
            font.setBold(true);
            headerStyle.setFont(font);
            for (int i = 0, len = colName.size(); i < len; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(colName.get(i));
                cell.setCellStyle(headerStyle);
            }
        }
        return sheet;
    }
}
