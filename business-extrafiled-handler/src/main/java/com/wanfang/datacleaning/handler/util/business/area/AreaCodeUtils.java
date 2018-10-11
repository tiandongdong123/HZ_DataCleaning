package com.wanfang.datacleaning.handler.util.business.area;

import com.wanfang.datacleaning.handler.util.business.area.model.Area;
import com.wanfang.datacleaning.handler.util.business.area.model.AreaCode;
import com.wanfang.datacleaning.util.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;

/**
 *    
 *  @Description 行政区代码工具类
 *  @Author   luqs   
 *  @Date 2018/7/18 14:59 
 *  @Version  V1.0   
 */
public class AreaCodeUtils {

    /**
     * 数据代码文件
     */
    private static final String DATA_CODE_FILE_PATH = "datafile/dataCodeCollection.xlsx";
    /**
     * 行政区划区代码的等级起始码
     */
    private static final String AREA_LEVEL_START_CODE = "00";

    private static Map<String, String> areaCodeMap = new HashMap<>();

    private AreaCodeUtils() {
    }

    /**
     * 缓存行政区划区代码信息
     *
     * @throws Exception
     */
    public static void cacheAreaCode() throws Exception {
        Workbook workbook = ExcelUtils.readExcel(AreaCodeUtils.class.getClassLoader().getResource(DATA_CODE_FILE_PATH).getPath());
        if (workbook != null) {
            Sheet areaCodeSheet = workbook.getSheet("CA01");
            int totalRow = areaCodeSheet.getLastRowNum();
            Row row;
            String code;
            String codeCnName;

            for (int i = 0; i <= totalRow; i++) {
                row = areaCodeSheet.getRow(i);
                code = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(0)));
                codeCnName = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(1)).trim());

                if (isMeetAreaCodeFormat(code)) {
                    areaCodeMap.put(code, codeCnName);
                }
            }
        }

    }

    /**
     * 获取缓存的行政区划区代码信息
     *
     * @return Map<String, String>
     * @throws Exception
     */
    public static Map<String, String> getCacheAreaCode() throws Exception {
        if (areaCodeMap == null || areaCodeMap.isEmpty()) {
            cacheAreaCode();
        }

        return areaCodeMap;
    }

    /**
     * 通过区代码获取区域信息
     *
     * @param areaCode 行政区划代码,如：110000
     * @return Area 若areaCode为空或，则返回null
     * @throws Exception
     */
    public static Area getAreaInfoByCode(String areaCode) throws Exception {
        Area area = new Area();

        if (!isMeetAreaCodeFormat(areaCode)) {
            return null;
        }

        Workbook workbook = ExcelUtils.readExcel(AreaCodeUtils.class.getClassLoader().getResource(DATA_CODE_FILE_PATH).getPath());
        if (workbook != null) {
            Sheet areaCodeSheet = workbook.getSheet("CA01");
            int totalRow = areaCodeSheet.getLastRowNum();

            String codeValue;
            String codeNameValue;
            AreaCode areaCodeObj = getAreaCodeWithoutCheck(areaCode);
            String provinceCode = areaCodeObj.getProvinceCode();
            String cityCode = areaCodeObj.getCityCode();
            String districtCode = areaCodeObj.getDistrictCode();
            Row row;

            for (int i = 0; i <= totalRow; i++) {
                row = areaCodeSheet.getRow(i);
                codeValue = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(0)));
                codeNameValue = StringUtils.deleteWhitespace(ExcelUtils.getStringValue(row.getCell(1)).trim());

                if (isMeetAreaCodeFormat(codeValue)) {
                    // 省（/直辖市）名
                    if (StringUtils.isNotEmpty(provinceCode) && provinceCode.equals(codeValue)) {
                        area.setProvinceCode(provinceCode);
                        area.setProvinceName(codeNameValue);
                        continue;
                    }

                    // 市（/县）名
                    if (StringUtils.isNotEmpty(cityCode) && cityCode.equals(codeValue)) {
                        area.setCityCode(cityCode);
                        area.setCityName(codeNameValue);
                        continue;
                    }

                    // 区（/县）名
                    if (StringUtils.isNotEmpty(districtCode) && districtCode.equals(codeValue)) {
                        area.setDistrictCode(districtCode);
                        area.setDistrictName(codeNameValue);
                        continue;
                    }
                }
            }
        }

        return area;
    }

    /**
     * 获取行政区划区代码的等级码
     *
     * @param areaCode 行政区划代码,如：110000
     * @return AreaCode 若不符合行政区划代码格式，则返回null
     */
    public static AreaCode getAreaCode(String areaCode) {
        return getAreaCode(areaCode, true);
    }

    /**
     * 获取区代码的等级码
     *
     * @param areaCode 行政区划代码,如：110000
     * @return AreaCode
     */
    public static AreaCode getAreaCodeWithoutCheck(String areaCode) {
        return getAreaCode(areaCode, false);
    }

    /**
     * 获取区代码的等级码
     *
     * @param areaCode 行政区划代码,如：110000
     * @param checkFlg 是否校验行政区划代码格式
     * @return AreaCode 若不符合行政区划代码格式，则返回null
     */
    private static AreaCode getAreaCode(String areaCode, boolean checkFlg) {
        AreaCode areaCodeObj = new AreaCode();

        if (checkFlg && !isMeetAreaCodeFormat(areaCode)) {
            return null;
        } else {
            // 省（/直辖市）码
            String provinceCode = null;
            // 市（/县）码
            String cityCode = null;
            // 区（/县）码
            String districtCode = null;

            // 省（/直辖市）级码
            String provinceLevelCode = areaCode.substring(0, 2);
            // 市（/县）级码
            String cityLevelCode = areaCode.substring(2, 4);
            // 区（/县）级码
            String districtLevelCode = areaCode.substring(4, 6);

            if (!AREA_LEVEL_START_CODE.equals(districtLevelCode)) {
                provinceCode = provinceLevelCode + AREA_LEVEL_START_CODE + AREA_LEVEL_START_CODE;
                cityCode = provinceLevelCode + cityLevelCode + AREA_LEVEL_START_CODE;
                districtCode = areaCode;
            } else if (!AREA_LEVEL_START_CODE.equals(cityLevelCode)) {
                provinceCode = provinceLevelCode + AREA_LEVEL_START_CODE + AREA_LEVEL_START_CODE;
                cityCode = areaCode;
            } else {
                provinceCode = areaCode;
            }

            areaCodeObj.setProvinceCode(provinceCode);
            areaCodeObj.setCityCode(cityCode);
            areaCodeObj.setDistrictCode(districtCode);
        }

        return areaCodeObj;
    }

    /**
     * 是否符合行政区划代码格式
     *
     * @param areaCode 行政区划代码,如：110000
     * @return boolean 若符合行政区划代码格式，则返回true
     */
    public static boolean isMeetAreaCodeFormat(String areaCode) {
        final int codeStandardLength = 6;
        boolean meetFormatFlg = StringUtils.isNotBlank(areaCode) && StringUtils.isNumeric(areaCode) && areaCode.length() == codeStandardLength;

        return meetFormatFlg;
    }
}
