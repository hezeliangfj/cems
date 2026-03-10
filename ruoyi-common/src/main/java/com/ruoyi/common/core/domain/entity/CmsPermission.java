package com.ruoyi.common.core.domain.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * CMS权限实体类
 * 
 * @author ruoyi
 */
public class CmsPermission extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 权限ID */
    private Long id;

    /** 权限编码 */
    private Long permId;

    /** 权限名称 */
    private String permName;

    /** 权限代码 */
    private String permCode;

    /** 权限类型 */
    private Integer permType;

    /** 父权限ID */
    private Long parentId;

    /** 路径 */
    private String path;

    /** 状态 */
    private Integer status;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /** 创建时间 */
    private Date createAt;

    /** 更新时间 */
    private Date updateAt;

    /** 备注 */
    private String memo;

    /** 子权限 */
    private List<CmsPermission> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPermId() {
        return permId;
    }

    public void setPermId(Long permId) {
        this.permId = permId;
    }

    public String getPermName() {
        return permName;
    }

    public void setPermName(String permName) {
        this.permName = permName;
    }

    public String getPermCode() {
        return permCode;
    }

    public void setPermCode(String permCode) {
        this.permCode = permCode;
    }

    public Integer getPermType() {
        return permType;
    }

    public void setPermType(Integer permType) {
        this.permType = permType;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public List<CmsPermission> getChildren() {
        return children;
    }

    public void setChildren(List<CmsPermission> children) {
        this.children = children;
    }
}
