package com.wanfang.datacleaning.handler.util.business;

import com.wanfang.datacleaning.handler.constant.CmnEnum;
import com.wanfang.datacleaning.handler.util.PropertiesUtils;
import com.wanfang.datacleaning.util.ExcelUtils;
import com.wanfang.datacleaning.util.LoggerUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *    
 *  @Description 标准代号工具类
 *  @Author   luqs   
 *  @Date 2018/8/12 16:41 
 *  @Version  V1.0   
 */
public class StandardCodeUtils {

    private static final Logger abCodeDataLogger = LoggerFactory.getLogger(CmnEnum.LoggerEnum.ABNORMAL_CODE_DATA.getValue());

    private static Map<String, String> codeMap = new HashMap<>(16);

    /**
     * 标准代号文件
     */
    public static final String STANDARD_CODE_FILE_PATH = PropertiesUtils.getValue("standardCode.file.path");
    /**
     * 标准代号所在sheet名称
     */
    public static final String STANDARD_CODE_SHEET_NAME = PropertiesUtils.getValue("standardCode.sheet.name");

    static {
        // 缓存标准代码信息
        cacheStandardCodeInfo();
    }

    private StandardCodeUtils() {
    }

    /**
     * 获取缓存的标准信息
     *
     * @return List<Standard>
     */
    public static String getEffectLevelByCode(String code) {
        if (codeMap == null || codeMap.isEmpty()) {
            cacheStandardCodeInfo();
        }
        return codeMap.get(code);
    }

    /**
     * 获取缓存标准代码数量
     *
     * @return int
     */
    public static int getCacheCodeMapSize() {
        return codeMap.size();
    }

    /**
     * 缓存标准代码信息
     */
    private static void cacheStandardCodeInfo() {
        try {
            Workbook workbook = ExcelUtils.readExcel(STANDARD_CODE_FILE_PATH);
            if (workbook != null) {
                Sheet stdCodeSheet = workbook.getSheet(STANDARD_CODE_SHEET_NAME);
                int totalRow = stdCodeSheet.getLastRowNum();

                String code;
                Row row;
                // 过滤掉标题行（行索引从0开始）
                for (int i = 1; i <= totalRow; i++) {
                    row = stdCodeSheet.getRow(i);
                    code = ExcelUtils.getStringValue(row.getCell(2));
                    if (StringUtils.isBlank(code)) {
                        LoggerUtils.appendWarnLog(abCodeDataLogger, "文件：【{}】，行号：【{}】，标准代码为空", STANDARD_CODE_FILE_PATH, i + 1);
                        continue;
                    }

                    codeMap.put(code, StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(5))));
                }
            }
        } catch (IOException e) {
            LoggerUtils.appendErrorLog(abCodeDataLogger, "cacheStandardCodeInfo()出现异常：", e);
        }
    }

}
