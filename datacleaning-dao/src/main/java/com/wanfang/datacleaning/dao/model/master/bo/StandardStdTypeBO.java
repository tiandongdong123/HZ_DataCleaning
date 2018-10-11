package com.wanfang.datacleaning.dao.model.master.bo;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/12 15:11 
 *  @Version  V1.0   
 */
public class StandardStdTypeBO {
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

    @Override
    public String toString() {
        return "StandardStdTypeBO{" +
                "draftUnit='" + draftUnit + '\'' +
                ", standardStatus='" + standardStatus + '\'' +
                ", standardType='" + standardType + '\'' +
                '}';
    }
}
