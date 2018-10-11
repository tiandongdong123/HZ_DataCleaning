package com.wanfang.datacleaning.dao.model.master.bo;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/6 18:35 
 *  @Version  V1.0   
 */
public class PatentProposerNameBO {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 申请号
     */
    private String patentId;
    /**
     * 申请（专利权）人
     */
    private String proposerName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatentId() {
        return patentId;
    }

    public void setPatentId(String patentId) {
        this.patentId = patentId;
    }

    public String getProposerName() {
        return proposerName;
    }

    public void setProposerName(String proposerName) {
        this.proposerName = proposerName;
    }

    @Override
    public String toString() {
        return "PatentProposerNameBO{" +
                "id=" + id +
                ", patentId='" + patentId + '\'' +
                ", proposerName='" + proposerName + '\'' +
                '}';
    }
}
