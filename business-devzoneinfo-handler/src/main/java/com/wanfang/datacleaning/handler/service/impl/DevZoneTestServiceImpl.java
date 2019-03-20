package com.wanfang.datacleaning.handler.service.impl;

import com.wanfang.datacleaning.handler.constant.CmnConstant;
import com.wanfang.datacleaning.handler.dao.master.TblBusinessDao;
import com.wanfang.datacleaning.handler.model.bo.DevZoneInfoBO;
import com.wanfang.datacleaning.handler.service.DevZoneTestService;
import com.wanfang.datacleaning.handler.util.business.YangZhouDevZoneUtils;
import com.wanfang.datacleaning.util.ExcelUtils;
import com.wanfang.datacleaning.util.business.CmnUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.List;

/**
 * @author yifei
 * @date 2018/12/18
 */
@Service("devZoneTestService")
public class DevZoneTestServiceImpl implements DevZoneTestService {

    private static final Logger logger = LoggerFactory.getLogger(DevZoneServiceImpl.class);

    private static final String FILE_PATH = "datafile/devZoneEntData_YangZhou.xlsx";

    /**
     * 更新开发区信息成功数量
     */
    private int successCount;
    /**
     * 更新开发区信息失败数量
     */
    private int failCount;

    @Resource
    private TblBusinessDao tblBusinessDao;

    /**
     * 查找开发区企业
     */
    @Override
    public void findDevZoneInfo() {
        long startTime = System.currentTimeMillis();
        logger.info("递归更新DB工商表的开发区信息开始，id更新区间为：[{},{}]", CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION);
        // 递归更新开发区信息
        updateDevZoneInfoByRecursion(CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
        logger.info("递归更新DB工商表的开发区信息结束，id更新区间为：[{},{}]，共更新【{}】条数据，成功【{}】条，失败【{}】条，总耗时【{}】ms",
                CmnConstant.ID_START_POSITION, CmnConstant.ID_END_POSITION, (successCount + failCount), successCount, failCount, System.currentTimeMillis() - startTime);
    }

    /**
     * 递归更新开发区信息
     *
     * @param idStartPosition id起始位置
     * @param idEndPosition   id结束位置
     * @param pageSize        每页数量
     */
    private void updateDevZoneInfoByRecursion(int idStartPosition, int idEndPosition, int pageSize) {
        long startTime = System.currentTimeMillis();
        int pageSizeLimit = CmnUtils.handlePageSize(idStartPosition, idEndPosition, pageSize);
        logger.info("查询DB工商表的开发区信息开始，idStartPosition：【{}】，pageSize：【{}】", idStartPosition, pageSize);
        List<DevZoneInfoBO> devZoneInfoBOList = tblBusinessDao.getLatLonInfoByPage(idStartPosition, idEndPosition, pageSizeLimit);
        int qryResultSize = devZoneInfoBOList.size();
        logger.info("查询DB工商表的开发区信息结束，idStartPosition：【{}】，pageSize：【{}】，共查询到【{}】条数据，耗时【{}】ms", idStartPosition, pageSize, qryResultSize, System.currentTimeMillis() - startTime);
        if (devZoneInfoBOList.isEmpty()) {
            return;
        }

        startTime = System.currentTimeMillis();
        int lastPosition = devZoneInfoBOList.get(qryResultSize - 1).getId().intValue();
        logger.info("记录DB工商表的开发区信息开始，id区间为：[{},{}]", idStartPosition, lastPosition);
        readWriteExcel(devZoneInfoBOList);
        logger.info("记录DB工商表的开发区信息结束，id区间为：[{},{}]，共更新【{}】条数据，耗时【{}】ms", idStartPosition, lastPosition, successCount, System.currentTimeMillis() - startTime);

        // 若查到的当前页数据数量等于每页数量，则往后再查
        if (qryResultSize == pageSize && lastPosition < idEndPosition) {
            updateDevZoneInfoByRecursion(lastPosition + 1, CmnConstant.ID_END_POSITION, CmnConstant.PAGE_SIZE);
        }
    }

    /**
     * 读写excel文件
     */
    private void readWriteExcel(List<DevZoneInfoBO> devZoneInfoBOList) {
        FileOutputStream fileOutputStream = null;
        try {
            // 工作表
            Workbook workbook = ExcelUtils.readExcel(FILE_PATH);
            Sheet sheet = workbook.getSheet("扬州高新区企业");
            if (sheet == null || sheet.getLastRowNum() == 0) {
                // 创建sheet
                sheet = workbook.createSheet("扬州高新区企业");
                // 创建标题行
                Row titleRow = sheet.createRow(0);
                titleRow.createCell(0).setCellValue("序号");
                titleRow.createCell(1).setCellValue("id");
                titleRow.createCell(2).setCellValue("主体身份代码");
                titleRow.createCell(3).setCellValue("企业名称");
                titleRow.createCell(4).setCellValue("登记机关");
                titleRow.createCell(5).setCellValue("住所");
                titleRow.createCell(6).setCellValue("住所行政区划");
                titleRow.createCell(7).setCellValue("省");
                titleRow.createCell(8).setCellValue("市");
                titleRow.createCell(9).setCellValue("区");
                titleRow.createCell(10).setCellValue("经纬度");
            }

            // 处理源文件信息
            handleInputWorkbook(sheet, devZoneInfoBOList);

            // 输出流
            String outFileFullPath = DevZoneTestServiceImpl.class.getClassLoader().getResource(FILE_PATH).getPath();
            fileOutputStream = new FileOutputStream(createHandlingResultFile(outFileFullPath));

            // 写入输出流
            workbook.write(fileOutputStream);
            workbook.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            logger.error("文件：【{}】，文件找不到：", FILE_PATH, e);
        } catch (IOException e) {
            logger.error("文件：【{}】，readWriteExcel()出现IOException异常：", FILE_PATH, e);
        } catch (Exception e) {
            logger.error("文件：【{}】，readWriteExcel()出现异常：", FILE_PATH, e);
        } finally {
            closeOutStream(fileOutputStream);
        }
    }

    /**
     * 创建处理结果文件
     *
     * @param sourceFilePath 文件路径
     * @return File
     */
    private File createHandlingResultFile(String sourceFilePath) throws IOException {
        // 若文件不存在，则新建
        File resultFile = new File(sourceFilePath);
        if (!resultFile.exists()) {
            resultFile.createNewFile();
        }

        return resultFile;
    }

    /**
     * 处理源文件信息
     *
     * @param sheet             sheet
     * @param devZoneInfoBOList 开发区信息集合
     * @return Workbook
     */
    private Sheet handleInputWorkbook(Sheet sheet, List<DevZoneInfoBO> devZoneInfoBOList) {
        for (DevZoneInfoBO devZoneInfoBO : devZoneInfoBOList) {
            Point2D.Double point = splitLatLon(devZoneInfoBO.getLatLon());
            if (point != null && YangZhouDevZoneUtils.isInDevZone(point)) {
                int lastRowNum = sheet.getLastRowNum();
                Row row = sheet.createRow(lastRowNum + 1);
                row.createCell(0).setCellValue(lastRowNum + 1);
                row.createCell(1).setCellValue(devZoneInfoBO.getId());
                row.createCell(2).setCellValue(devZoneInfoBO.getPripid());
                row.createCell(3).setCellValue(devZoneInfoBO.getEntName());
                row.createCell(4).setCellValue(devZoneInfoBO.getRegOrg());
                row.createCell(5).setCellValue(devZoneInfoBO.getDom());
                row.createCell(6).setCellValue(devZoneInfoBO.getDomDistrict());
                row.createCell(7).setCellValue(devZoneInfoBO.getProvince());
                row.createCell(8).setCellValue(devZoneInfoBO.getCity());
                row.createCell(9).setCellValue(devZoneInfoBO.getArea());
                row.createCell(10).setCellValue(devZoneInfoBO.getLatLon());
                successCount++;
            }
        }
        return sheet;
    }

    /**
     * 分割经纬度
     *
     * @param latLon 经纬度
     * @return Point2D.Double
     */
    private Point2D.Double splitLatLon(String latLon) {
        if (StringUtils.isBlank(latLon)) {
            return null;
        }

        String[] array = StringUtils.split(latLon, ",");
        if (array.length == 2) {
            return new Point2D.Double(Double.parseDouble(array[1]), Double.parseDouble(array[0]));
        }
        return null;
    }

    /**
     * 关闭输出流
     *
     * @param outputStream 输出流
     */
    private void closeOutStream(OutputStream outputStream) {
        if (outputStream != null) {
            return;
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            logger.error("输出流关闭异常：", e);
        }
    }
}
