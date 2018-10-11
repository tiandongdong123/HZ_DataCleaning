package com.wanfang.datacleaning.handler.util.business;

import com.wanfang.datacleaning.util.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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

    /**
     * 数据代码文件
     */
    private static final String DATA_CODE_FILE_PATH = "datafile/dataCodeCollection.xlsx";
    private static Map<String, String> codeMap = new HashMap<>(16);

    private DomPropertyUtils() {
    }

    /**
     * 缓存住所产权代码信息
     *
     * @throws Exception
     */
    public static void cacheDomPropertyCodeInfo() throws Exception {
        Workbook workbook = ExcelUtils.readExcel(DomPropertyUtils.class.getClassLoader().getResource(DATA_CODE_FILE_PATH).getPath());
        if (workbook != null) {
            Sheet stdCodeSheet = workbook.getSheet("CA44");
            int totalRow = stdCodeSheet.getLastRowNum();

            String code;
            String name;
            Row row;
            for (int i = 0; i <= totalRow; i++) {
                row = stdCodeSheet.getRow(i);
                if (i > 1 && StringUtils.isNotBlank(ExcelUtils.getStringValue(row.getCell(0)))) {
                    code = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(0)));
                    name = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(1)));

                    codeMap.put(name, code);
                }
            }
        }
    }

    /**
     * 获取缓存的住所产权代码信息
     *
     * @return List<Standard>
     */
    public static Map<String, String> getCacheDomPropertyCodeInfo() throws Exception {
        if (codeMap == null || codeMap.isEmpty()) {
            cacheDomPropertyCodeInfo();
        }
        return codeMap;
    }

    /**
     * 获取代码（首位不含0）
     *
     * @param name 名称
     * @return String
     */
    public static String getCodeWithoutZero(String name) {
        try {
            Map<String, String> cacheCodeMap = getCacheDomPropertyCodeInfo();
            String code = cacheCodeMap.get(name);

            return code.startsWith("0") ? code.replaceFirst("0", "") : code;
        } catch (Exception e) {
            return null;
        }
    }

}
