package com.wanfang.datacleaning.handler.util.business;

import com.wanfang.datacleaning.handler.util.PolygonUtils;
import com.wanfang.datacleaning.handler.util.PropertiesUtils;
import com.wanfang.datacleaning.util.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yifei
 * @date 2018/12/16
 */
public class YangZhouDevZoneUtils {

    private static final Logger logger = LoggerFactory.getLogger(YangZhouDevZoneUtils.class);

    private static List<Point2D.Double> lonLatList = new ArrayList<>();

    private static final String FILE_PATH = PropertiesUtils.getValue("devZone.lonLat.YangZhou.file.path");
    private static final String SHEET_NAME = PropertiesUtils.getValue("devZone.lonLat.YangZhou.sheet.name");

    static {
        // 缓存经纬度信息
        cacheLonLatList();
    }

    /**
     * 判断点是否在开发区内
     *
     * @param lon 经度
     * @param lat 维度
     * @return boolean
     */
    public static boolean isInDevZone(double lon, double lat) {
        Point2D.Double pointDouble = new Point2D.Double(lon, lat);
        return isInDevZone(pointDouble);
    }

    /**
     * 判断点是否在开发区内
     *
     * @param pointDouble
     * @return boolean
     */
    public static boolean isInDevZone(Point2D.Double pointDouble) {
        return PolygonUtils.isPtInPoly(pointDouble, lonLatList);
    }

    /**
     * 获取缓存经纬度信息数量
     *
     * @return int
     */
    public static int getLonLatListSize() {
        return lonLatList.size();
    }

    /**
     * 缓存经纬度信息
     */
    private static void cacheLonLatList() {
        try {
            Workbook workbook = ExcelUtils.readExcel(FILE_PATH);
            if (workbook != null) {
                Sheet sheet = workbook.getSheet(SHEET_NAME);
                int totalRow = sheet.getLastRowNum();
                double lon;
                double lat;
                Row row;
                for (int i = 1; i <= totalRow; i++) {
                    row = sheet.getRow(i);
                    if (StringUtils.isNumeric(ExcelUtils.getStringValue(row.getCell(0)))) {
                        lon = row.getCell(3).getNumericCellValue();
                        lat = row.getCell(2).getNumericCellValue();
                        Point2D.Double aDouble = new Point2D.Double(lon, lat);
                        lonLatList.add(aDouble);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("文件：【{}】，sheet：【{}】，缓存扬州高新区经纬度出现异常：", FILE_PATH, SHEET_NAME, e);
        }
    }
}
