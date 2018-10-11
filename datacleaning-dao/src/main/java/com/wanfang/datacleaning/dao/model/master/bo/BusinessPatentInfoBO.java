package com.wanfang.datacleaning.dao.model.master.bo;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/12 15:10 
 *  @Version  V1.0   
 */
public class BusinessPatentInfoBO {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 企业（机构）名称
     */
    private String entName;
    /**
     * 是否拥有专利（0：无专利；1：有专利；）
     */
    private Integer hasPatent;
    /**
     * 专利类型枚举值列表（1：外观专利、2：发明专利、3：实用新型专利），格式：1,2,3
     */
    private String patentTypeList;
    /**
     * 专利数量
     */
    private Long patentNum;
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

    public Integer getHasPatent() {
        return hasPatent;
    }

    public void setHasPatent(Integer hasPatent) {
        this.hasPatent = hasPatent;
    }

    public String getPatentTypeList() {
        return patentTypeList;
    }

    public void setPatentTypeList(String patentTypeList) {
        this.patentTypeList = patentTypeList;
    }

    public Long getPatentNum() {
        return patentNum;
    }

    public void setPatentNum(Long patentNum) {
        this.patentNum = patentNum;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "BusinessPatentInfoBO{" +
                "id=" + id +
                ", entName='" + entName + '\'' +
                ", hasPatent=" + hasPatent +
                ", patentTypeList='" + patentTypeList + '\'' +
                ", patentNum=" + patentNum +
                ", updateTime=" + updateTime +
                '}';
    }
}
