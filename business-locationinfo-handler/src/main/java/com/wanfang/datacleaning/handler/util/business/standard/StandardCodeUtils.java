package com.wanfang.datacleaning.handler.util.business.standard;

import com.wanfang.datacleaning.handler.util.business.standard.model.Standard;
import com.wanfang.datacleaning.util.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.List;

/**
 *    
 *  @Description 标准代号工具类
 *  @Author   luqs   
 *  @Date 2018/8/12 16:41 
 *  @Version  V1.0   
 */
public class StandardCodeUtils {

    private static List<Standard> standardList = new ArrayList<>();

    /**
     * 标准代号文件
     */
    private static final String STANDARD_CODE_FILE_PATH = "datafile/standardCode.xls";

    private StandardCodeUtils() {
    }

    /**
     * 缓存标准信息
     *
     * @throws Exception
     */
    public static void cacheStandardInfo() throws Exception {
        Workbook workbook = ExcelUtils.readExcel(StandardCodeUtils.class.getClassLoader().getResource(STANDARD_CODE_FILE_PATH).getPath());
        if (workbook != null) {
            Sheet stdCodeSheet = workbook.getSheet("Sheet1");
            int totalRow = stdCodeSheet.getLastRowNum();

            String serialNum;
            String searchCode;
            String code;
            String meaning;
            String adminDepartment;
            String effectLevel;
            Row row;
            for (int i = 0; i <= totalRow; i++) {
                row = stdCodeSheet.getRow(i);
                if (i > 1 && StringUtils.isNotBlank(ExcelUtils.getStringValue(row.getCell(0)))) {
                    serialNum = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(0)));
                    searchCode = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(1)));
                    code = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(2)));
                    meaning = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(3)).trim());
                    adminDepartment = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(4)).trim());
                    effectLevel = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(5)).trim());

                    Standard standard = new Standard();
                    standard.setSerialNum(serialNum);
                    standard.setSearchCode(searchCode);
                    standard.setCode(code);
                    standard.setMeaning(meaning);
                    standard.setAdminDepartment(adminDepartment);
                    standard.setEffectLevel(effectLevel);
                    standardList.add(standard);
                }
            }
        }
    }

    /**
     * 获取缓存的标准信息
     *
     * @return List<Standard>
     */
    public static List<Standard> getCacheStandardInfo() throws Exception {
        if (standardList == null || standardList.size() == 0) {
            cacheStandardInfo();
        }
        return standardList;
    }
}
