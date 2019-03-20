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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *    
 *  @Description 外汇牌价
 *  @Author   luqs   
 *  @Date 2018/9/6 13:57 
 *  @Version  V1.0   
 */
public class ForeignExRateUtils {
    private static final Logger logger = LoggerFactory.getLogger(ForeignExRateUtils.class);
    private static final Logger abnormalDataLogger = LoggerFactory.getLogger(LoggerEnum.ABNORMAL_CODE_DATA.getValue());

    private static Map<String, BigDecimal> foreignExRateMap = new HashMap<>(16);

    /**
     * 中行外汇牌价文件classpath
     */
    public static final String FOREIGN_EX_RATE_FILE_PATH = PropertiesUtils.getValue("foreignExchangeRate.file.path");
    /**
     * 中行外汇牌价所在sheet名称
     */
    public static final String FOREIGN_EX_RATE_SHEET_NAME = PropertiesUtils.getValue("foreignExchangeRate.sheet.name");

    private ForeignExRateUtils() {
    }

    static {
        // 缓存外汇牌价信息
        cacheForeignExRateInfo();
    }

    /**
     * 获取缓存的外汇牌价信息数量
     *
     * @return int
     */
    public static int getCacheForeignExRateSize() {
        return foreignExRateMap.size();
    }

    /**
     * 获取人民币(6位小数)
     *
     * @param currencyName 币种名称
     * @param capital      资金
     * @return BigDecimal 若无此币种，则返回null
     */
    public static BigDecimal getRmb(String currencyName, BigDecimal capital) {
        if (foreignExRateMap.containsKey(StringUtils.deleteWhitespace(currencyName))) {
            BigDecimal exRate = foreignExRateMap.get(currencyName);
            return capital.multiply(exRate).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
        }

        abnormalDataLogger.warn("文件：【{}】，sheet：【{}】，不存在此币种【{}】", FOREIGN_EX_RATE_FILE_PATH, FOREIGN_EX_RATE_SHEET_NAME, currencyName);
        return null;
    }

    /**
     * 缓存外汇牌价信息
     *
     * @throws Exception
     */
    private static void cacheForeignExRateInfo() {
        try {
            Workbook workbook = ExcelUtils.readExcel(FOREIGN_EX_RATE_FILE_PATH);
            Sheet sheet = workbook.getSheet(FOREIGN_EX_RATE_SHEET_NAME);
            int totalRow = sheet.getLastRowNum();

            // 过滤标题行
            for (int i = 1; i <= totalRow; i++) {
                Row row = sheet.getRow(i);
                String capName = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(0)));
                BigDecimal exRate = new BigDecimal(ExcelUtils.getNumericValue(row.getCell(1)));
                // 判断币种是否已存在
                if (foreignExRateMap.containsKey(capName)) {
                    abnormalDataLogger.warn("文件：【{}】，sheet：【{}】，行号：【{}】，此币种【{}】已存在", FOREIGN_EX_RATE_FILE_PATH, FOREIGN_EX_RATE_SHEET_NAME, i + 1, capName);
                    continue;
                }

                foreignExRateMap.put(capName, exRate);
            }
        } catch (IOException e) {
            logger.error("文件：【{}】，sheet：【{}】，cacheForeignExRateInfo（）出现异常：", FOREIGN_EX_RATE_FILE_PATH, FOREIGN_EX_RATE_SHEET_NAME, e);
        }
    }
}
