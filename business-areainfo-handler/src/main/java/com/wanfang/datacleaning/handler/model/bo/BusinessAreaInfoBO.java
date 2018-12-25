package com.wanfang.datacleaning.handler.model.bo;

/**
 * @author yifei
 * @date 2018/11/25
 */
public class BusinessAreaInfoBO {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 登记机关
     */
    private String regOrg;
    /**
     * 邮编
     */
    private String postalCode;
    /**
     * 住所
     */
    private String dom;
    /**
     * 住所行政区划码
     */
    private String domDistrict;
    /**
     * 经营场所
     */
    private String opLoc;
    /**
     * 经营场所所在行政区划
     */
    private String opLocDistrict;
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

    public String getRegOrg() {
        return regOrg;
    }

    public void setRegOrg(String regOrg) {
        this.regOrg = regOrg;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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

    public String getOpLoc() {
        return opLoc;
    }

    public void setOpLoc(String opLoc) {
        this.opLoc = opLoc;
    }

    public String getOpLocDistrict() {
        return opLocDistrict;
    }

    public void setOpLocDistrict(String opLocDistrict) {
        this.opLocDistrict = opLocDistrict;
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

    /**
     * 初始化空对象
     *
     * @param id 主键id
     * @return BusinessAreaInfoBO
     */
    public static BusinessAreaInfoBO initEmptyBO(long id) {
        return init(id, 0, 0, 0);
    }

    /**
     * 初始化对象
     *
     * @param id       主键id
     * @param province 省
     * @param city     市
     * @param area     区
     * @return BusinessAreaInfoBO
     */
    public static BusinessAreaInfoBO init(long id, int province, int city, int area) {
        BusinessAreaInfoBO areaInfoBO = new BusinessAreaInfoBO();
        areaInfoBO.setId(id);
        areaInfoBO.setProvince(province);
        areaInfoBO.setCity(city);
        areaInfoBO.setArea(area);

        return areaInfoBO;
    }

    @Override
    public String toString() {
        return "BusinessAreaInfoBO{" +
                "id=" + id +
                ", regOrg=" + regOrg +
                ", postalCode='" + postalCode + '\'' +
                ", dom='" + dom + '\'' +
                ", domDistrict='" + domDistrict + '\'' +
                ", opLoc='" + opLoc + '\'' +
                ", opLocDistrict='" + opLocDistrict + '\'' +
                ", province=" + province +
                ", city=" + city +
                ", area=" + area +
                ", updateTime=" + updateTime +
                '}';
    }
}
