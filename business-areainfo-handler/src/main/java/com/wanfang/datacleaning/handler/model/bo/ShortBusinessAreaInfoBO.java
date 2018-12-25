package com.wanfang.datacleaning.handler.model.bo;

/**
 * @author yifei
 * @date 2018/11/25
 */
public class ShortBusinessAreaInfoBO {

    /**
     * 主键id
     */
    private Long id;
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
        return "BusinessAreaInfoBO{" +
                "id=" + id +
                ", province=" + province +
                ", city=" + city +
                ", area=" + area +
                ", updateTime=" + updateTime +
                '}';
    }
}
