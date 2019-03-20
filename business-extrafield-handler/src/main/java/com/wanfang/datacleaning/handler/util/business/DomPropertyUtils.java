package com.wanfang.datacleaning.handler.util.business;

import com.wanfang.datacleaning.handler.constant.LoggerEnum;
import com.wanfang.datacleaning.handler.util.PropertiesUtils;
import com.wanfang.datacleaning.util.ExcelUtils;
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
 *  @Description 住所产权工具类
 *  @Author   luqs   
 *  @Date 2018/8/28 19:34 
 *  @Version  V1.0   
 */
public class DomPropertyUtils {

    private static final Logger logger = LoggerFactory.getLogger(DomPropertyUtils.class);
    private static final Logger abnormalCodeDataLogger = LoggerFactory.getLogger(LoggerEnum.ABNORMAL_CODE_DATA.getValue());

    /**
     * 住所产权代码文件classPath
     */
    public static final String DOM_PROPERTY_CODE_FILE_PATH = PropertiesUtils.getValue("domPropertyCode.file.path");
    /**
     * 住所产权代码文件所在sheet名称
     */
    public static final String DOM_PROPERTY_CODE_SHEET_NAME = PropertiesUtils.getValue("domPropertyCode.sheet.name");
    private static Map<String, String> codeMap = new HashMap<>(16);

    static {
        // 缓存住所产权代码信息
        cacheDomPropertyCodeInfo();
    }

    private DomPropertyUtils() {
    }

    /**
     * 获取代码（首位不含0）
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @return String 若匹配不到name或者异常，则返回defaultValue
     */
    public static String getCodeWithoutZero(String name, String defaultValue) {
        String code = codeMap.get(StringUtils.deleteWhitespace(name));

        if (StringUtils.isBlank(code)) {
            abnormalCodeDataLogger.warn("name：【{}】，defaultValue：【{}】，住所产权代码匹配不到数据", name, defaultValue);
            return defaultValue;
        }

        return code.startsWith("0") ? code.replaceFirst("0", "") : code;
    }

    /**
     * 获取缓存住所产权代码数量
     *
     * @return int
     */
    public static int getCacheCodeMapSize() {
        return codeMap.size();
    }

    /**
     * 缓存住所产权代码信息
     */
    private static void cacheDomPropertyCodeInfo() {
        try {
            Workbook workbook = ExcelUtils.readExcel(DOM_PROPERTY_CODE_FILE_PATH);
            Sheet stdCodeSheet = workbook.getSheet(DOM_PROPERTY_CODE_SHEET_NAME);
            int totalRow = stdCodeSheet.getLastRowNum();

            for (int i = 0; i <= totalRow; i++) {
                Row row = stdCodeSheet.getRow(i);
                if (StringUtils.isNumeric(ExcelUtils.getStringValue(row.getCell(0)))) {
                    String code = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(0)));
                    String name = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(1)));
                    codeMap.put(name, code);
                }
            }
        } catch (IOException e) {
            logger.error("文件：【{}】，sheet：【{}】，缓存住所产权代码信息出现异常：", DOM_PROPERTY_CODE_FILE_PATH, DOM_PROPERTY_CODE_SHEET_NAME, e);
        }
    }

}
