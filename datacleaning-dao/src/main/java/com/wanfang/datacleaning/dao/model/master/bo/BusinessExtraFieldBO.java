package com.wanfang.datacleaning.dao.model.master.bo;

import java.util.Date;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/8 9:58 
 *  @Version  V1.0   
 */
public class BusinessExtraFieldBO {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 企业(机构)名称
     */
    private String entName;
    /**
     * 企业状态
     */
    private String entStatus;
    /**
     * 许可经营项目
     */
    private String abuItem;
    /**
     * 成立日期
     */
    private Date esDate;
    /**
     * 登记机关
     */
    private String regOrg;
    /**
     * 属地监管工商所
     */
    private String localAdm;
    /**
     * 成立年份
     */
    private Integer esYear;
    /**
     * 成立月份
     */
    private Integer esMonth;
    /**
     * 住所产权
     */
    private String domProRight;
    /**
     * 是否有许可经营项目()
     */
    private Integer hasAbuItem;
    /**
     * 省份
     */
    private Integer province;
    /**
     * 市
     */
    private Integer city;
    /**
     * 区
     */
    private Integer area;
    /**
     * 更新时间（10位时间戳）
     */
    private Long updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public String getEntStatus() {
        return entStatus;
    }

    public void setEntStatus(String entStatus) {
        this.entStatus = entStatus;
    }

    public String getAbuItem() {
        return abuItem;
    }

    public void setAbuItem(String abuItem) {
        this.abuItem = abuItem;
    }

    public Date getEsDate() {
        return esDate;
    }

    public void setEsDate(Date esDate) {
        this.esDate = esDate;
    }

    public String getRegOrg() {
        return regOrg;
    }

    public void setRegOrg(String regOrg) {
        this.regOrg = regOrg;
    }

    public String getLocalAdm() {
        return localAdm;
    }

    public void setLocalAdm(String localAdm) {
        this.localAdm = localAdm;
    }

    public Integer getEsYear() {
        return esYear;
    }

    public void setEsYear(Integer esYear) {
        this.esYear = esYear;
    }

    public Integer getEsMonth() {
        return esMonth;
    }

    public void setEsMonth(Integer esMonth) {
        this.esMonth = esMonth;
    }

    public Integer getHasAbuItem() {
        return hasAbuItem;
    }

    public String getDomProRight() {
        return domProRight;
    }

    public void setDomProRight(String domProRight) {
        this.domProRight = domProRight;
    }

    public void setHasAbuItem(Integer hasAbuItem) {
        this.hasAbuItem = hasAbuItem;
    }

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "BusinessExtraFieldBO{" +
                "id=" + id +
                ", entName='" + entName + '\'' +
                ", entStatus='" + entStatus + '\'' +
                ", abuItem='" + abuItem + '\'' +
                ", esDate=" + esDate +
                ", regOrg='" + regOrg + '\'' +
                ", localAdm='" + localAdm + '\'' +
                ", esYear=" + esYear +
                ", esMonth=" + esMonth +
                ", domProRight='" + domProRight + '\'' +
                ", hasAbuItem=" + hasAbuItem +
                ", province=" + province +
                ", city=" + city +
                ", area=" + area +
                ", updateTime=" + updateTime +
                '}';
    }
}
