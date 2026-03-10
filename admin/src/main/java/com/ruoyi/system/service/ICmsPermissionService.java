package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.common.core.domain.entity.CmsPermission;

/**
 * CMS权限 业务层
 * 
 * @author ruoyi
 */
public interface ICmsPermissionService
{
    /**
     * 根据用户查询系统权限列表
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    public List<CmsPermission> selectPermissionList(Long userId);

    /**
     * 根据用户查询系统权限列表
     * 
     * @param permission 权限信息
     * @param userId 用户ID
     * @return 权限列表
     */
    public List<CmsPermission> selectPermissionList(CmsPermission permission, Long userId);

    /**
     * 根据用户ID查询权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    public List<String> selectPermissionPermsByUserId(Long userId);

    /**
     * 根据用户ID查询权限树信息
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    public List<CmsPermission> selectPermissionTreeByUserId(Long userId);

    /**
     * 构建前端路由所需要的权限
     * 
     * @param permissions 权限列表
     * @return 路由列表
     */
    public List<Map<String, Object>> buildMenus(List<CmsPermission> permissions);

    /**
     * 构建前端所需要树结构
     * 
     * @param permissions 权限列表
     * @return 树结构列表
     */
    public List<CmsPermission> buildPermissionTree(List<CmsPermission> permissions);

    /**
     * 构建前端所需要下拉树结构
     * 
     * @param permissions 权限列表
     * @return 下拉树结构列表
     */
    public List<CmsPermission> buildPermissionTreeSelect(List<CmsPermission> permissions);

    /**
     * 根据权限ID查询信息
     * 
     * @param permissionId 权限ID
     * @return 权限信息
     */
    public CmsPermission selectPermissionById(Long permissionId);

    /**
     * 是否存在权限子节点
     * 
     * @param permissionId 权限ID
     * @return 结果
     */
    public boolean hasChildByPermissionId(Long permissionId);

    /**
     * 新增保存权限信息
     * 
     * @param permission 权限信息
     * @return 结果
     */
    public int insertPermission(CmsPermission permission);

    /**
     * 修改保存权限信息
     * 
     * @param permission 权限信息
     * @return 结果
     */
    public int updatePermission(CmsPermission permission);

    /**
     * 删除权限管理信息
     * 
     * @param permissionId 权限ID
     * @return 结果
     */
    public int deletePermissionById(Long permissionId);

    /**
     * 校验权限名称是否唯一
     * 
     * @param permission 权限信息
     * @return 结果
     */
    public String checkPermissionNameUnique(CmsPermission permission);
}
