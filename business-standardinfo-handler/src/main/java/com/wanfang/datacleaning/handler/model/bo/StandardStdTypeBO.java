package com.wanfang.datacleaning.handler.model.bo;


import java.util.Date;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/12 15:11 
 *  @Version  V1.0   
 */
public class StandardStdTypeBO {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 起草单位
     */
    private String draftUnit;
    /**
     * 标准状态
     */
    private String standardStatus;
    /**
     * 标准类型
     */
    private String standardType;
    /**
     * 发布日期
     */
    private Date issueDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDraftUnit() {
        return draftUnit;
    }

    public void setDraftUnit(String draftUnit) {
        this.draftUnit = draftUnit;
    }

    public String getStandardStatus() {
        return standardStatus;
    }

    public void setStandardStatus(String standardStatus) {
        this.standardStatus = standardStatus;
    }

    public String getStandardType() {
        return standardType;
    }

    public void setStandardType(String standardType) {
        this.standardType = standardType;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    @Override
    public String toString() {
        return "StandardStdTypeBO{" +
                "id=" + id +
                ", draftUnit='" + draftUnit + '\'' +
                ", standardStatus='" + standardStatus + '\'' +
                ", standardType='" + standardType + '\'' +
                ", issueDate=" + issueDate +
                '}';
    }
}
