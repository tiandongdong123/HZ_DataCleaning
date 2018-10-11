package com.wanfang.datacleaning.dao.model.master.bo;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/12 15:12 
 *  @Version  V1.0   
 */
public class BusinessStandardInfoBO {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 企业（机构）名称
     */
    private String entName;
    /**
     * 是否参与起草标准（0：未参与；1：参与；）
     */
    private Integer hasDrStandard;
    /**
     * 起草标准类型（1：国家标准；2：行业标准；3：企业标准），格式：1,2,3
     */
    private String drStandardTypeList;
    /**
     * 标准数量
     */
    private Long drStandardNum;
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

    public Integer getHasDrStandard() {
        return hasDrStandard;
    }

    public void setHasDrStandard(Integer hasDrStandard) {
        this.hasDrStandard = hasDrStandard;
    }

    public String getDrStandardTypeList() {
        return drStandardTypeList;
    }

    public void setDrStandardTypeList(String drStandardTypeList) {
        this.drStandardTypeList = drStandardTypeList;
    }

    public Long getDrStandardNum() {
        return drStandardNum;
    }

    public void setDrStandardNum(Long drStandardNum) {
        this.drStandardNum = drStandardNum;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "BusinessStandardInfoBO{" +
                "id=" + id +
                ", entName='" + entName + '\'' +
                ", hasDrStandard=" + hasDrStandard +
                ", drStandardTypeList='" + drStandardTypeList + '\'' +
                ", drStandardNum=" + drStandardNum +
                ", updateTime=" + updateTime +
                '}';
    }
}
