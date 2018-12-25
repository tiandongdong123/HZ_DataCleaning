package com.wanfang.datacleaning.handler.util.business;

import org.apache.commons.lang3.StringUtils;

/**
 *    
 *  @Description 行政区代码工具类
 *  @Author   luqs   
 *  @Date 2018/7/18 14:59 
 *  @Version  V1.0   
 */
public class AreaCodeUtils {

    /**
     * 行政区划区代码的等级起始码
     */
    public static final String AREA_LEVEL_START_CODE = "00";

    private AreaCodeUtils() {
    }

    /**
     * 内部类-行政区划等级码
     */
    public static class AreaCode {

        /**
         * 省（/直辖市）码
         */
        private String provinceCode;
        /**
         * 市（/县）码
         */
        private String cityCode;
        /**
         * 区（/县）码
         */
        private String districtCode;

        public String getProvinceCode() {
            return provinceCode;
        }

        public void setProvinceCode(String provinceCode) {
            this.provinceCode = provinceCode;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public String getDistrictCode() {
            return districtCode;
        }

        public void setDistrictCode(String districtCode) {
            this.districtCode = districtCode;
        }

        @Override
        public String toString() {
            return "AreaCode{" +
                    "provinceCode='" + provinceCode + '\'' +
                    ", cityCode='" + cityCode + '\'' +
                    ", districtCode='" + districtCode + '\'' +
                    '}';
        }
    }

    /**
     * 获取区代码的等级码
     *
     * @param areaCode 行政区划代码,如：110000
     * @return AreaCode 若不符合行政区划代码格式，则返回null
     */
    public static AreaCode getAreaCode(String areaCode) {
        AreaCode areaCodeObj = new AreaCode();

        // 判断是否符合行政区划代码格式
        if (!isMeetAreaCodeFormat(areaCode)) {
            return null;
        }

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

        return areaCodeObj;
    }

    /**
     * 是否符合行政区划代码格式
     *
     * @param areaCode 行政区划代码,如：110000
     * @return boolean 若符合行政区划代码格式，则返回true
     */
    public static boolean isMeetAreaCodeFormat(String areaCode) {
        final int codeStandardLength_6 = 6;
        final int codeStandardLength_9 = 9;
        final int codeStandardLength_12 = 12;
        boolean meetFormatFlg = StringUtils.isNumeric(areaCode) && (areaCode.length() == codeStandardLength_6
                || areaCode.length() == codeStandardLength_9 || areaCode.length() == codeStandardLength_12);

        return meetFormatFlg;
    }
}
