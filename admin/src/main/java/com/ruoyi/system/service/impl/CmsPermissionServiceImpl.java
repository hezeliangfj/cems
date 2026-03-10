package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.entity.CmsPermission;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.mapper.CmsPermissionMapper;
import com.ruoyi.system.service.ICmsPermissionService;

/**
 * CMS权限 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class CmsPermissionServiceImpl implements ICmsPermissionService
{
    @Autowired
    private CmsPermissionMapper cmsPermissionMapper;

    /**
     * 根据用户查询系统权限列表
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public List<CmsPermission> selectPermissionList(Long userId)
    {
        return selectPermissionList(new CmsPermission(), userId);
    }

    /**
     * 根据用户查询系统权限列表
     * 
     * @param permission 权限信息
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public List<CmsPermission> selectPermissionList(CmsPermission permission, Long userId)
    {
        List<CmsPermission> permissions = null;
        // 管理员显示所有权限
        if (SecurityUtils.isAdmin(userId))
        {
            permissions = cmsPermissionMapper.selectPermissionList(permission);
        }
        else
        {
            permissions = cmsPermissionMapper.selectPermissionListByUserId(permission, userId);
        }
        return permissions;
    }

    /**
     * 根据用户ID查询权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public List<String> selectPermissionPermsByUserId(Long userId)
    {
        List<CmsPermission> permissions = selectPermissionList(userId);
        List<String> perms = new ArrayList<String>();
        for (CmsPermission permission : permissions)
        {
            if (permission != null && permission.getPermCode() != null && !"".equals(permission.getPermCode()))
            {
                perms.add(permission.getPermCode());
            }
        }
        return perms;
    }

    /**
     * 根据用户ID查询权限树信息
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public List<CmsPermission> selectPermissionTreeByUserId(Long userId)
    {
        List<CmsPermission> permissions = selectPermissionList(userId);
        return buildPermissionTree(permissions);
    }

    /**
     * 构建前端路由所需要的权限
     * 
     * @param permissions 权限列表
     * @return 路由列表
     */
    @Override
    public List<Map<String, Object>> buildMenus(List<CmsPermission> permissions)
    {
        List<Map<String, Object>> routers = new ArrayList<Map<String, Object>>();
        for (CmsPermission permission : permissions)
        {
            Map<String, Object> router = new HashMap<String, Object>();
            router.put("path", permission.getPath());
            
            // 构建 meta 对象，包含 parmName
            Map<String, Object> meta = new HashMap<String, Object>();
            meta.put("parmName", permission.getPermName());
            router.put("meta", meta);
            
            router.put("perms", permission.getPermCode());
            router.put("hidden", false);
            // 只有顶级菜单使用 Layout 组件，子菜单使用具体的组件路径
            if (permission.getParentId() == 0)
            {
                router.put("component", "Layout");
            }
            else
            {
                // 子菜单使用路径作为组件，前端会根据这个路径去加载对应的页面
                router.put("component", permission.getPath());
            }
            
            List<CmsPermission> cPermissions = permission.getChildren();
            if (cPermissions != null && !cPermissions.isEmpty() && permission.getPermType() == 1)
            {
                router.put("children", buildMenus(cPermissions));
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 构建前端所需要树结构
     * 
     * @param permissions 权限列表
     * @return 树结构列表
     */
    @Override
    public List<CmsPermission> buildPermissionTree(List<CmsPermission> permissions)
    {
        List<CmsPermission> returnList = new ArrayList<CmsPermission>();
        for (CmsPermission permission : permissions)
        {
            // 如果是顶级节点，开始递归构建
            if (permission.getParentId() == 0)
            {
                recursionFn(permissions, permission);
                returnList.add(permission);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = permissions;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     * 
     * @param permissions 权限列表
     * @return 下拉树结构列表
     */
    @Override
    public List<CmsPermission> buildPermissionTreeSelect(List<CmsPermission> permissions)
    {
        List<CmsPermission> returnList = new ArrayList<CmsPermission>();
        for (CmsPermission permission : permissions)
        {
            // 如果是顶级节点，开始递归构建
            if (permission.getParentId() == 0)
            {
                recursionFn(permissions, permission);
                returnList.add(permission);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = permissions;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<CmsPermission> list, CmsPermission permission)
    {
        // 得到子节点列表
        List<CmsPermission> childList = getChildList(list, permission);
        permission.setChildren(childList);
        for (CmsPermission tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<CmsPermission> getChildList(List<CmsPermission> list, CmsPermission permission)
    {
        List<CmsPermission> tlist = new ArrayList<CmsPermission>();
        for (CmsPermission n : list)
        {
            if (n.getParentId().equals(permission.getPermId()))
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<CmsPermission> list, CmsPermission permission)
    {
        return getChildList(list, permission).size() > 0;
    }

    /**
     * 根据权限ID查询信息
     * 
     * @param permissionId 权限ID
     * @return 权限信息
     */
    @Override
    public CmsPermission selectPermissionById(Long permissionId)
    {
        return cmsPermissionMapper.selectPermissionById(permissionId);
    }

    /**
     * 是否存在权限子节点
     * 
     * @param permissionId 权限ID
     * @return 结果
     */
    @Override
    public boolean hasChildByPermissionId(Long permissionId)
    {
        int result = cmsPermissionMapper.hasChildByPermissionId(permissionId);
        return result > 0;
    }

    /**
     * 新增保存权限信息
     * 
     * @param permission 权限信息
     * @return 结果
     */
    @Override
    public int insertPermission(CmsPermission permission)
    {
        return cmsPermissionMapper.insertPermission(permission);
    }

    /**
     * 修改保存权限信息
     * 
     * @param permission 权限信息
     * @return 结果
     */
    @Override
    public int updatePermission(CmsPermission permission)
    {
        return cmsPermissionMapper.updatePermission(permission);
    }

    /**
     * 删除权限管理信息
     * 
     * @param permissionId 权限ID
     * @return 结果
     */
    @Override
    public int deletePermissionById(Long permissionId)
    {
        return cmsPermissionMapper.deletePermissionById(permissionId);
    }

    /**
     * 校验权限名称是否唯一
     * 
     * @param permission 权限信息
     * @return 结果
     */
    @Override
    public String checkPermissionNameUnique(CmsPermission permission)
    {
        Long permissionId = permission.getId() == null ? -1L : permission.getId();
        CmsPermission info = cmsPermissionMapper.checkPermissionNameUnique(permission.getPermName(), permission.getParentId());
        if (info != null && info.getId().longValue() != permissionId.longValue())
        {
            return "1";
        }
        return "0";
    }
}
