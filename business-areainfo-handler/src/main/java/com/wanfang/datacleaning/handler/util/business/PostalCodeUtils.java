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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yifei
 * @date 2018/11/25
 */
public class PostalCodeUtils {

    private static final Logger abnormalDataLogger = LoggerFactory.getLogger(LoggerEnum.ABNORMAL_CODE_DATA.getValue());

    private static Map<String, List<PostalCodeInfo>> codeMap = new HashMap<>(16);

    /**
     * 邮编行政区划对应关系文件classpath
     */
    public static final String POSTAL_CODE_FILE_PATH = PropertiesUtils.getValue("postalCodeAdAreaRelationship.file.path");
    /**
     * 邮编行政区划对应关系文件sheet名称
     */
    public static final String POSTAL_CODE_FILE_SHEET_NAME = PropertiesUtils.getValue("postalCodeAdAreaRelationship.sheet.name");
    /**
     * 文件原始数据总数
     */
    private static int total = 0;
    /**
     * 异常数据总数
     */
    private static int abnormalDataCount = 0;

    /**
     * 内部类-邮编信息
     */
    public static class PostalCodeInfo {
        /**
         * 邮编
         */
        private String postalCode;
        /**
         * 行政区划名称
         */
        private String politicalName;
        /**
         * 行政区划码（6位）
         */
        private String areaCode;

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getPoliticalName() {
            return politicalName;
        }

        public void setPoliticalName(String politicalName) {
            this.politicalName = politicalName;
        }

        public String getAreaCode() {
            return areaCode;
        }

        public void setAreaCode(String areaCode) {
            this.areaCode = areaCode;
        }

        public boolean equals(PostalCodeInfo postalCodeInfo) {
            if (postalCode.equals(postalCodeInfo.getPostalCode()) && areaCode.equals(postalCodeInfo.getAreaCode())) {
                return true;
            }

            return false;
        }

        @Override
        public String toString() {
            return "PostalCodeInfo{" +
                    "postalCode='" + postalCode + '\'' +
                    ", politicalName='" + politicalName + '\'' +
                    ", areaCode='" + areaCode + '\'' +
                    '}';
        }
    }

    static {
        // 缓存邮编信息
        cacheCodeMap();
    }

    private PostalCodeUtils() {
    }

    /**
     * 获取文件原始数据总数
     *
     * @return int
     */
    public static int getTotal() {
        return total;
    }

    /**
     * 获取缓存的数据总数
     *
     * @return int
     */
    public static int getCacheTotal() {
        return total - abnormalDataCount;
    }

    /**
     * 获取缓存过程中的异常数据数量
     *
     * @return int
     */
    public static int getAbnormalDataCount() {
        return abnormalDataCount;
    }

    /**
     * 通过key获取缓存的邮编信息
     *
     * @return List
     */
    public static List<PostalCodeInfo> getCachePostalCodeInfoByKey(String key) {
        return codeMap.get(key);
    }

    /**
     * 处理邮编
     *
     * @param postalCode
     * @return String
     */
    public static String handlePostalCode(String postalCode) {
        postalCode = StringUtils.deleteWhitespace(postalCode);
        if (StringUtils.isBlank(postalCode)) {
            return postalCode;
        }

        final int standardLen = 6;
        StringBuilder postalCodeBuilder = new StringBuilder(postalCode);
        // 若邮编位数不够，则在前面加“0”
        for (int i = 0; i < (standardLen - postalCode.length()); i++) {
            postalCodeBuilder = new StringBuilder("0").append(postalCodeBuilder);
        }

        return postalCodeBuilder.toString();
    }

    /**
     * 缓存邮编信息
     */
    private static void cacheCodeMap() {
        try {
            Workbook workbook = ExcelUtils.readExcel(POSTAL_CODE_FILE_PATH);
            if (workbook != null) {
                Sheet sheet = workbook.getSheet(POSTAL_CODE_FILE_SHEET_NAME);
                int totalRow = sheet.getLastRowNum();
                total = totalRow;

                String postalCode;
                String politicalName;
                String areaCode;
                PostalCodeInfo postalCodeInfo;
                Row row;

                //  默认从第2行开始取值（跳过第1行标题）
                for (int i = 1; i <= totalRow; i++) {
                    row = sheet.getRow(i);
                    postalCode = StringUtils.deleteWhitespace(row.getCell(3).getStringCellValue());

                    // 判断邮编是否为空
                    if (StringUtils.isBlank(postalCode)) {
                        abnormalDataCount++;
                       abnormalDataLogger.warn( "文件：【{}】,sheet：【{}】,行号：【{}】，邮编为空！", POSTAL_CODE_FILE_PATH, POSTAL_CODE_FILE_SHEET_NAME, i + 1);
                        continue;
                    }

                    areaCode = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(0)));
                    politicalName = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(1)));
                    // 处理邮编
                    postalCode = handlePostalCode(postalCode);

                    postalCodeInfo = new PostalCodeInfo();
                    postalCodeInfo.setPostalCode(postalCode);
                    postalCodeInfo.setAreaCode(areaCode);
                    postalCodeInfo.setPoliticalName(politicalName);

                    // 判断邮编是否存在
                    if (codeMap.containsKey(postalCode)) {
                        if (existSameData(codeMap.get(postalCode), postalCodeInfo)) {
                            abnormalDataCount++;
                           abnormalDataLogger.warn( "文件：【{}】,sheet：【{}】，行号：【{}】，邮编：【{}】，邮编-行政区划码已存在！", POSTAL_CODE_FILE_PATH, POSTAL_CODE_FILE_SHEET_NAME, i + 1, postalCode);
                            continue;
                        }
                        codeMap.get(postalCode).add(postalCodeInfo);
                    }

                    List<PostalCodeInfo> postalCodeInfoList = new ArrayList<>();
                    postalCodeInfoList.add(postalCodeInfo);
                    codeMap.put(postalCode, postalCodeInfoList);
                }
            }
        } catch (IOException e) {
           abnormalDataLogger.warn( "文件：【{}】,sheet：【{}】，cacheCodeMap()出现异常：", POSTAL_CODE_FILE_PATH, POSTAL_CODE_FILE_SHEET_NAME, e);
        }
    }

    /**
     * 是否存在相同数据
     *
     * @param postalCodeInfoList
     * @param postalCodeInfo
     * @return boolean
     */
    private static boolean existSameData(List<PostalCodeInfo> postalCodeInfoList, PostalCodeInfo postalCodeInfo) {
        for (PostalCodeInfo codeInfo : postalCodeInfoList) {
            if (codeInfo.equals(postalCodeInfo)) {
                return true;
            }
        }

        return false;
    }

}
