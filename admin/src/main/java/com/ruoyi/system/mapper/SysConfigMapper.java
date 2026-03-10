package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysConfig;

/**
 * 系统配置 数据层
 * 
 * @author ruoyi
 */
public interface SysConfigMapper
{
    /**
     * 查询系统配置列表
     * 
     * @param config 系统配置信息
     * @return 系统配置集合
     */
    public List<SysConfig> selectConfigList(SysConfig config);

    /**
     * 查询系统配置信息
     * 
     * @param configId 系统配置ID
     * @return 系统配置信息
     */
    public SysConfig selectConfigById(Long configId);

    /**
     * 根据键名查询系统配置信息
     * 
     * @param configKey 系统配置键名
     * @return 系统配置信息
     */
    public SysConfig selectConfigByKey(String configKey);

    /**
     * 校验配置键名是否唯一
     * 
     * @param configKey 系统配置键名
     * @return 系统配置信息
     */
    public SysConfig checkConfigKeyUnique(String configKey);

    /**
     * 新增系统配置
     * 
     * @param config 系统配置信息
     * @return 结果
     */
    public int insertConfig(SysConfig config);

    /**
     * 修改系统配置
     * 
     * @param config 系统配置信息
     * @return 结果
     */
    public int updateConfig(SysConfig config);

    /**
     * 删除系统配置
     * 
     * @param configId 系统配置ID
     * @return 结果
     */
    public int deleteConfigById(Long configId);

    /**
     * 批量删除系统配置
     * 
     * @param configIds 需要删除的系统配置ID
     * @return 结果
     */
    public int deleteConfigByIds(Long[] configIds);
}
