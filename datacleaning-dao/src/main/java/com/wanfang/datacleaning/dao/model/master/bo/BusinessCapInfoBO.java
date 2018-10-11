package com.wanfang.datacleaning.dao.model.master.bo;

import java.math.BigDecimal;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/12 15:22 
 *  @Version  V1.0   
 */
public class BusinessCapInfoBO {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 注册资金
     */
    private BigDecimal regCap;
    /**
     * 注册资本(金)币种
     */
    private String regCapCur;
    /**
     * 注册资金(人民币)
     */
    private BigDecimal regCapRmb;
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

    public BigDecimal getRegCap() {
        return regCap;
    }

    public void setRegCap(BigDecimal regCap) {
        this.regCap = regCap;
    }

    public String getRegCapCur() {
        return regCapCur;
    }

    public void setRegCapCur(String regCapCur) {
        this.regCapCur = regCapCur;
    }

    public BigDecimal getRegCapRmb() {
        return regCapRmb;
    }

    public void setRegCapRmb(BigDecimal regCapRmb) {
        this.regCapRmb = regCapRmb;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "BusinessCapInfoBO{" +
                "id=" + id +
                ", regCap=" + regCap +
                ", regCapCur='" + regCapCur + '\'' +
                ", regCapRmb=" + regCapRmb +
                ", updateTime=" + updateTime +
                '}';
    }
}
