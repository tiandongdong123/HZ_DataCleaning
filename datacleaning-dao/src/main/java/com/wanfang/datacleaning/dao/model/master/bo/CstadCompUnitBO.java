package com.wanfang.datacleaning.dao.model.master.bo;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/6 18:35 
 *  @Version  V1.0   
 */
public class CstadCompUnitBO {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 编号
     */
    private Integer resultId;
    /**
     * 完成单位
     */
    private String compUnit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getResultId() {
        return resultId;
    }

    public void setResultId(Integer resultId) {
        this.resultId = resultId;
    }

    public String getCompUnit() {
        return compUnit;
    }

    public void setCompUnit(String compUnit) {
        this.compUnit = compUnit;
    }

    @Override
    public String toString() {
        return "CstadCompUnitBO{" +
                "id=" + id +
                ", resultId='" + resultId + '\'' +
                ", compUnit='" + compUnit + '\'' +
                '}';
    }
}
