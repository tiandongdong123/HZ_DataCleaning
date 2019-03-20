package com.wanfang.datacleaning.handler.model.bo;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/12 15:09 
 *  @Version  V1.0   
 */
public class PatentPatTypeBO {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 申请（专利权）人
     */
    private String proposerName;
    /**
     * 专利类型
     */
    private String patentType;
    /**
     * 申请日
     */
    private String appDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProposerName() {
        return proposerName;
    }

    public void setProposerName(String proposerName) {
        this.proposerName = proposerName;
    }

    public String getPatentType() {
        return patentType;
    }

    public void setPatentType(String patentType) {
        this.patentType = patentType;
    }

    public String getAppDate() {
        return appDate;
    }

    public void setAppDate(String appDate) {
        this.appDate = appDate;
    }

    @Override
    public String toString() {
        return "PatentPatTypeBO{" +
                "id=" + id +
                ", proposerName='" + proposerName + '\'' +
                ", patentType='" + patentType + '\'' +
                ", appDate='" + appDate + '\'' +
                '}';
    }
}
