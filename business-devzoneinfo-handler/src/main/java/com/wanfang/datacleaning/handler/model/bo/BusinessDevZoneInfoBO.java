package com.wanfang.datacleaning.handler.model.bo;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/12 15:22 
 *  @Version  V1.0   
 */
public class BusinessDevZoneInfoBO {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 开发区
     */
    private String ecoTecDevZone;
    /**
     * 经纬度， 格式：lat,lon
     */
    private String latLon;
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

    public String getEcoTecDevZone() {
        return ecoTecDevZone;
    }

    public void setEcoTecDevZone(String ecoTecDevZone) {
        this.ecoTecDevZone = ecoTecDevZone;
    }

    public String getLatLon() {
        return latLon;
    }

    public void setLatLon(String latLon) {
        this.latLon = latLon;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "BusinessDevZoneInfoBO{" +
                "id=" + id +
                ", ecoTecDevZone='" + ecoTecDevZone + '\'' +
                ", latLon='" + latLon + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
