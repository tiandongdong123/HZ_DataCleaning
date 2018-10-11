package com.wanfang.datacleaning.handler.util.business;

import com.wanfang.datacleaning.util.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *    
 *  @Description 高新企业工具类
 *  @Author   luqs   
 *  @Date 2018/8/30 11:02 
 *  @Version  V1.0   
 */
public class HighNewEnterUtils {

    private static final Logger logger = LoggerFactory.getLogger(HighNewEnterUtils.class);

    /**
     * 高新企业
     */
    private static final HashMap<String, String> highNewEnterMap = new HashMap<>(16);

    /**
     * 高新企业文件
     */
    private static final String HIGH_NEW_ENTER_FILE_PATH = "datafile/highNewEnter.xlsx";

    private HighNewEnterUtils() {
    }

    static {
        try {
            cacheHighNewEnterInfo();
        } catch (Exception e) {
            logger.error("缓存高新企业信息(cacheHighNewEnterInfo())出现异常：", e);
        }
    }

    /**
     * 缓存高新企业信息
     *
     * @throws Exception
     */
    public static void cacheHighNewEnterInfo() throws Exception {
        Workbook workbook = ExcelUtils.readExcel(HighNewEnterUtils.class.getClassLoader().getResource(HIGH_NEW_ENTER_FILE_PATH).getPath());
        if (workbook != null) {
            Sheet stdCodeSheet = workbook.getSheet("Sheet1");
            int totalRow = stdCodeSheet.getLastRowNum();

            String entName;
            Row row;
            for (int i = 0; i <= totalRow; i++) {
                row = stdCodeSheet.getRow(i);
                entName = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(0)));

                highNewEnterMap.put(entName, (i + 1) + "");
            }
        }
    }

    /**
     * 获取缓存的高新企业信息
     *
     * @return List<String>
     */
    public static HashMap<String, String> getCacheHighNewEnterMap() {
        if (highNewEnterMap == null || highNewEnterMap.isEmpty()) {
            try {
                cacheHighNewEnterInfo();
            } catch (Exception e) {
                logger.error("获取缓存的高新企业信息(getCacheHighNewEnterMap())出现异常：", e);
                return new HashMap<>(0);
            }
        }
        return highNewEnterMap;
    }

    /**
     * 是否高新企业
     *
     * @param entName 企业名称
     * @return boolean
     */
    public static boolean isHighNewEnter(String entName) {
        if (StringUtils.isEmpty(entName)) {
            return false;
        }

        HashMap<String, String> cacheHighNewEnterMap = getCacheHighNewEnterMap();
        if (cacheHighNewEnterMap != null && cacheHighNewEnterMap.containsKey(entName)) {
            return true;
        }
        return false;
    }
}
