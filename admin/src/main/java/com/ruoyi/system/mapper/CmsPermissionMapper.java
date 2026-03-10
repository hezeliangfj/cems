package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.common.core.domain.entity.CmsPermission;

/**
 * CMS权限 数据层
 * 
 * @author ruoyi
 */
public interface CmsPermissionMapper
{
    /**
     * 查询权限列表
     * 
     * @param permission 权限信息
     * @return 权限列表
     */
    public List<CmsPermission> selectPermissionList(CmsPermission permission);

    /**
     * 根据用户ID查询权限列表
     * 
     * @param permission 权限信息
     * @param userId 用户ID
     * @return 权限列表
     */
    public List<CmsPermission> selectPermissionListByUserId(@Param("permission") CmsPermission permission, @Param("userId") Long userId);

    /**
     * 根据权限ID查询信息
     * 
     * @param permissionId 权限ID
     * @return 权限信息
     */
    public CmsPermission selectPermissionById(Long permissionId);

    /**
     * 根据权限名称查询信息
     * 
     * @param permName 权限名称
     * @param parentId 父权限ID
     * @return 权限信息
     */
    public CmsPermission checkPermissionNameUnique(String permName, Long parentId);

    /**
     * 新增权限
     * 
     * @param permission 权限信息
     * @return 结果
     */
    public int insertPermission(CmsPermission permission);

    /**
     * 修改权限
     * 
     * @param permission 权限信息
     * @return 结果
     */
    public int updatePermission(CmsPermission permission);

    /**
     * 删除权限
     * 
     * @param permissionId 权限ID
     * @return 结果
     */
    public int deletePermissionById(Long permissionId);

    /**
     * 检查权限是否有子节点
     * 
     * @param permissionId 权限ID
     * @return 结果
     */
    public int hasChildByPermissionId(Long permissionId);
}
