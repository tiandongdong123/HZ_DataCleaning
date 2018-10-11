package com.wanfang.datacleaning.dao.model.master.bo;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/12 15:09 
 *  @Version  V1.0   
 */
public class PatentPatTypeBO {
    /**
     * 申请（专利权）人
     */
    private String proposerName;
    /**
     * 专利类型
     */
    private String patentType;

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

    @Override
    public String toString() {
        return "PatentPatTypeBO{" +
                "proposerName='" + proposerName + '\'' +
                ", patentType='" + patentType + '\'' +
                '}';
    }
}
