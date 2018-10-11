package com.wanfang.datacleaning.handler.util.business.standard.model;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/12 16:48 
 *  @Version  V1.0   
 */
public class Standard {
    /**
     * 序号
     */
    private String serialNum;
    /**
     * 检索用代号
     */
    private String searchCode;
    /**
     * 代号
     */
    private String code;
    /**
     * 含义
     */
    private String meaning;
    /**
     * 管理部门
     */
    private String adminDepartment;
    /**
     * 效力级别
     */
    private String effectLevel;

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getSearchCode() {
        return searchCode;
    }

    public void setSearchCode(String searchCode) {
        this.searchCode = searchCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getAdminDepartment() {
        return adminDepartment;
    }

    public void setAdminDepartment(String adminDepartment) {
        this.adminDepartment = adminDepartment;
    }

    public String getEffectLevel() {
        return effectLevel;
    }

    public void setEffectLevel(String effectLevel) {
        this.effectLevel = effectLevel;
    }

    @Override
    public String toString() {
        return "Standard{" +
                "serialNum='" + serialNum + '\'' +
                ", searchCode='" + searchCode + '\'' +
                ", code='" + code + '\'' +
                ", meaning='" + meaning + '\'' +
                ", adminDepartment='" + adminDepartment + '\'' +
                ", effectLevel='" + effectLevel + '\'' +
                '}';
    }
}
