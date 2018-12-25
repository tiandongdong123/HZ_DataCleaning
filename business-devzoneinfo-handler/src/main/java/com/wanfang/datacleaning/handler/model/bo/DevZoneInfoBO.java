package com.wanfang.datacleaning.handler.model.bo;

/**
 * @author yifei
 * @date 2018/12/18
 */
public class DevZoneInfoBO {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 主体身份代码
     */
    private String pripid;
    /**
     * 企业名称
     */
    private String entName;
    /**
     * 登记机关
     */
    private String regOrg;
    /**
     * 住所
     */
    private String dom;
    /**
     * 住所行政区划码
     */
    private String domDistrict;
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
     * 经纬度， 格式：lat,lon
     */
    private String latLon;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPripid() {
        return pripid;
    }

    public void setPripid(String pripid) {
        this.pripid = pripid;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public String getRegOrg() {
        return regOrg;
    }

    public void setRegOrg(String regOrg) {
        this.regOrg = regOrg;
    }

    public String getDom() {
        return dom;
    }

    public void setDom(String dom) {
        this.dom = dom;
    }

    public String getDomDistrict() {
        return domDistrict;
    }

    public void setDomDistrict(String domDistrict) {
        this.domDistrict = domDistrict;
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

    public String getLatLon() {
        return latLon;
    }

    public void setLatLon(String latLon) {
        this.latLon = latLon;
    }

    @Override
    public String toString() {
        return "DevZoneInfoBO{" +
                "id=" + id +
                ", pripid='" + pripid + '\'' +
                ", entName='" + entName + '\'' +
                ", regOrg='" + regOrg + '\'' +
                ", dom='" + dom + '\'' +
                ", domDistrict='" + domDistrict + '\'' +
                ", province=" + province +
                ", city=" + city +
                ", area=" + area +
                ", latLon='" + latLon + '\'' +
                '}';
    }
}
