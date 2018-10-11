package com.wanfang.datacleaning.dao.model.master.bo;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/9/22 20:35 
 *  @Version  V1.0   
 */
public class BusinessHighNewInfoBO {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 企业(机构)名称
     */
    private String entName;
    /**
     * 是否高新技术企业
     */
    private Integer highNewEnter;
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

    public Integer getHighNewEnter() {
        return highNewEnter;
    }

    public void setHighNewEnter(Integer highNewEnter) {
        this.highNewEnter = highNewEnter;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "BusinessHighNewInfoBO{" +
                "id=" + id +
                ", entName='" + entName + '\'' +
                ", highNewEnter=" + highNewEnter +
                ", updateTime=" + updateTime +
                '}';
    }
}
