package com.wanfang.datacleaning.handler.model.entity.master;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/29 18:29 
 *  @Version  V1.0   
 */
public class TblEntResultEntity {

    /**
     * 主体身份代码
     */
    private String pripid;
    /**
     * 企业名称
     */
    private String entName;
    /**
     * 成果编号
     */
    private String resultNum;
    /**
     * 成果类型
     */
    private Integer resultType;
    /**
     * 更新时间（10位时间戳）
     */
    private Long updateTime;

    public String getPripid() {
        return pripid;
    }

    public void setPripid(String pripid) {
        this.pripid = pripid;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public String getResultNum() {
        return resultNum;
    }

    public void setResultNum(String resultNum) {
        this.resultNum = resultNum;
    }

    public Integer getResultType() {
        return resultType;
    }

    public void setResultType(Integer resultType) {
        this.resultType = resultType;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "TblEntResultEntity{" +
                "pripid='" + pripid + '\'' +
                ", entName='" + entName + '\'' +
                ", resultNum='" + resultNum + '\'' +
                ", resultType=" + resultType +
                ", updateTime=" + updateTime +
                '}';
    }
}
