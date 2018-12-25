package com.wanfang.datacleaning.handler.model.bo;

import java.util.Date;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/6 18:31 
 *  @Version  V1.0   
 */
public class BusinessEntNameBO {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 主体身份代码
     */
    private String pripid;
    /**
     * 企业（机构）名称
     */
    private String entName;
    /**
     * 经营(驻在)期限自
     */
    private Date opFrom;
    /**
     * 经营(驻在)期限至
     */
    private Date opTo;

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

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public Date getOpFrom() {
        return opFrom;
    }

    public void setOpFrom(Date opFrom) {
        this.opFrom = opFrom;
    }

    public Date getOpTo() {
        return opTo;
    }

    public void setOpTo(Date opTo) {
        this.opTo = opTo;
    }

    @Override
    public String toString() {
        return "BusinessEntNameBO{" +
                "id=" + id +
                ", pripid='" + pripid + '\'' +
                ", entName='" + entName + '\'' +
                ", opFrom=" + opFrom +
                ", opTo=" + opTo +
                '}';
    }
}
