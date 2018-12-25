package com.wanfang.datacleaning.handler.model.bo;

import java.util.Date;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/6 18:35 
 *  @Version  V1.0   
 */
public class StandardDraftUnitBO {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 编号
     */
    private String standNum;
    /**
     * 起草单位
     */
    private String draftUnit;
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

    public String getStandNum() {
        return standNum;
    }

    public void setStandNum(String standNum) {
        this.standNum = standNum;
    }

    public String getDraftUnit() {
        return draftUnit;
    }

    public void setDraftUnit(String draftUnit) {
        this.draftUnit = draftUnit;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    @Override
    public String toString() {
        return "StandardDraftUnitBO{" +
                "id=" + id +
                ", standNum='" + standNum + '\'' +
                ", draftUnit='" + draftUnit + '\'' +
                ", issueDate=" + issueDate +
                '}';
    }
}
