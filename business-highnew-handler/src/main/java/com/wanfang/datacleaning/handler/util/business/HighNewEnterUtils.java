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

/**
 *    
 *  @Description 高新企业工具类
 *  @Author   luqs   
 *  @Date 2018/8/30 11:02 
 *  @Version  V1.0   
 */
public class HighNewEnterUtils {

    private static final Logger logger = LoggerFactory.getLogger(HighNewEnterUtils.class);
    private static final Logger abnormalDataLogger = LoggerFactory.getLogger(LoggerEnum.ABNORMAL_CODE_DATA.getValue());

    /**
     * 高新企业
     */
    private static final HashMap<String, String> highNewEnterMap = new HashMap<>(16);

    /**
     * 高新企业文件classpath
     */
    public static final String HIGH_NEW_ENTER_FILE_PATH = PropertiesUtils.getValue("highNewEnter.file.path");
    /**
     * 高新企业所在sheet名称
     */
    public static final String HIGH_NEW_ENTER_SHEET_NAME = PropertiesUtils.getValue("highNewEnter.sheet.name");

    private HighNewEnterUtils() {
    }

    static {
        // 缓存高新企业信息
        cacheHighNewEnterInfo();
    }

    /**
     * 是否高新企业
     *
     * @param entName 企业名称
     * @return boolean
     */
    public static boolean isHighNewEnter(String entName) {
        if (StringUtils.isBlank(entName)) {
            return false;
        }

        // 去除空白字符
        entName = StringUtils.deleteWhitespace(entName);
        if (highNewEnterMap.containsKey(entName)) {
            return true;
        }
        return false;
    }

    /**
     * 获取缓存高新企业信息数量
     *
     * @return int
     */
    public static int getCacheHighNewEnterInfoSize() {
        return highNewEnterMap.size();
    }

    /**
     * 缓存高新企业信息
     */
    private static void cacheHighNewEnterInfo() {
        try {
            Workbook workbook = ExcelUtils.readExcel(HIGH_NEW_ENTER_FILE_PATH);
            if (workbook != null) {
                Sheet stdCodeSheet = workbook.getSheet(HIGH_NEW_ENTER_SHEET_NAME);
                int totalRow = stdCodeSheet.getLastRowNum();

                String entName;
                Row row;
                for (int i = 0; i <= totalRow; i++) {
                    row = stdCodeSheet.getRow(i);
                    entName = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(0)));
                    // 判断企业名称是否为空
                    if (StringUtils.isEmpty(entName)) {
                        abnormalDataLogger.warn("文件：【{}】，sheet：【{}】，行号：【{}】，企业名称为空", HIGH_NEW_ENTER_FILE_PATH, HIGH_NEW_ENTER_SHEET_NAME, i + 1);
                        continue;
                    }
                    // 判断企业名称是否已存在
                    if (highNewEnterMap.containsKey(entName)) {
                        abnormalDataLogger.warn("文件：【{}】，sheet：【{}】，行号：【{}】，企业名称已存在", HIGH_NEW_ENTER_FILE_PATH, HIGH_NEW_ENTER_SHEET_NAME, i + 1);
                        continue;
                    }

                    highNewEnterMap.put(entName, (i + 1) + "");
                }
            }
        } catch (IOException e) {
            logger.error("文件：【{}】，sheet：【{}】，cacheHighNewEnterInfo()出现异常：", HIGH_NEW_ENTER_FILE_PATH, HIGH_NEW_ENTER_SHEET_NAME, e);
        }
    }

}
