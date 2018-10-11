package com.wanfang.datacleaning.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 *    
 *  @Description Excel工具类
 *  @Author   luqs   
 *  @Date 2018/7/18 15:43 
 *  @Version  V1.0   
 */
public class ExcelUtils {

    /**
     * excel文件拓展名：.xls
     */
    private static final String EXTENSION_XLS = ".xls";
    /**
     * excel文件拓展名：.xlsx
     */
    private static final String EXTENSION_XLSX = ".xlsx";

    private ExcelUtils() {
    }

    /**
     * 读取excel文件
     *
     * @param filePath 文件路径（含拓展名），如：testExcel.xls、testExcel.xlsx
     * @return Workbook 若filePath为空，则返回null
     * @throws IOException
     */
    public static Workbook readExcel(String filePath) throws IOException {

        if (StringUtils.isBlank(filePath)) {
            return null;
        }

        Workbook wb = null;
        // 文件拓展名
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = new FileInputStream(filePath);

        if (EXTENSION_XLS.equals(extString)) {
            wb = new HSSFWorkbook(is);
        } else if (EXTENSION_XLSX.equals(extString)) {
            wb = new XSSFWorkbook(is);
        }

        return wb;
    }

    /**
     * 获取单元格[字符串]值
     *
     * @param cell 单元格
     * @return String 若cell为null,则返回null
     */
    public static String getStringValue(Cell cell) {

        if (cell == null) {
            return null;
        }

        String strValue;
        CellType cellType = cell.getCellTypeEnum();

        if (CellType.STRING.equals(cellType)) {
            strValue = cell.getStringCellValue();
        } else if (CellType.NUMERIC.equals(cellType)) {
            strValue = new DecimalFormat("0").format(cell.getNumericCellValue());
        } else {
            strValue = "";
        }

        return strValue;
    }

    /**
     * 获取单元格[数字]值
     *
     * @param cell 单元格
     * @return String 若cell为null,则返回null
     */
    public static Double getNumericValue(Cell cell) {

        if (cell == null) {
            return null;
        }

        Double numValue;
        CellType cellType = cell.getCellTypeEnum();
        if (CellType.NUMERIC.equals(cellType)) {
            numValue = cell.getNumericCellValue();
        } else if (CellType.STRING.equals(cellType)) {
            numValue = Double.parseDouble(cell.getStringCellValue());
        } else {
            numValue = null;
        }

        return numValue;
    }
}
