package com.wanfang.datacleaning.dao.model.master.bo;

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

    @Override
    public String toString() {
        return "BusinessEntNameBO{" +
                "id=" + id +
                ", pripid='" + pripid + '\'' +
                ", entName='" + entName + '\'' +
                '}';
    }
}
