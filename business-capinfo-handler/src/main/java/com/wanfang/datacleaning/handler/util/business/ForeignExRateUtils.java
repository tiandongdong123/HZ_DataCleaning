package com.wanfang.datacleaning.handler.util.business;

import com.wanfang.datacleaning.util.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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

    private static Map<String, BigDecimal> foreignExRateMap = new HashMap<>(16);

    /**
     * 中行外汇牌价文件
     */
    private static final String FOREIGN_EX_RATE_FILE_PATH = "datafile/foreignExchangeRate_boc.xlsx";

    private ForeignExRateUtils() {
    }

    static {
        try {
            cacheForeignExRateInfo();
        } catch (Exception e) {

        }
    }

    /**
     * 缓存外汇牌价信息
     *
     * @throws Exception
     */
    public static void cacheForeignExRateInfo() throws Exception {
        Workbook workbook = ExcelUtils.readExcel(ForeignExRateUtils.class.getClassLoader().getResource(FOREIGN_EX_RATE_FILE_PATH).getPath());
        if (workbook != null) {
            Sheet stdCodeSheet = workbook.getSheet("Sheet1");
            int totalRow = stdCodeSheet.getLastRowNum();

            String capName;
            BigDecimal exRate;
            Row row;
            for (int i = 1; i <= totalRow; i++) {
                row = stdCodeSheet.getRow(i);
                capName = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(0)));
                exRate = new BigDecimal(ExcelUtils.getNumericValue(row.getCell(1)));

                foreignExRateMap.put(capName, exRate);
            }
        }
    }

    /**
     * 获取缓存的外汇牌价信息
     *
     * @return List<String>
     */
    public static Map<String, BigDecimal> getCacheForeignExRateMap() {
        if (foreignExRateMap == null || foreignExRateMap.isEmpty()) {
            try {
                cacheForeignExRateInfo();
            } catch (Exception e) {
                return new HashMap<>(0);
            }
        }
        return foreignExRateMap;
    }

    /**
     * 获取人民币(6位小数)
     *
     * @param currencyName 币种名称
     * @param capital      资金
     * @return BigDecimal 若无此币种，则返回null
     */
    public static BigDecimal getRmb(String currencyName, BigDecimal capital) {
        Map<String, BigDecimal> cacheForeignExRateMap = getCacheForeignExRateMap();

        if (cacheForeignExRateMap != null && cacheForeignExRateMap.containsKey(currencyName)) {
            BigDecimal exRate = cacheForeignExRateMap.get(currencyName);

            return capital.multiply(exRate).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);
        }

        return null;
    }
}
