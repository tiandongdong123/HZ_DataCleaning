package com.wanfang.datacleaning.handler.model.bo;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/11/7 18:37 
 *  @Version  V1.0   
 */
public class UsCreditCodeBO {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 主体身份代码
     */
    private String pripid;
    /**
     * 统一社会信用代码
     */
    private String usCreditCode;
    /**
     * 组织结构代码
     */
    private String orgCode;
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

    public String getPripid() {
        return pripid;
    }

    public void setPripid(String pripid) {
        this.pripid = pripid;
    }

    public String getUsCreditCode() {
        return usCreditCode;
    }

    public void setUsCreditCode(String usCreditCode) {
        this.usCreditCode = usCreditCode;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UsCreditCodeBO{" +
                "id=" + id +
                ", pripid='" + pripid + '\'' +
                ", usCreditCode='" + usCreditCode + '\'' +
                ", orgCode='" + orgCode + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
