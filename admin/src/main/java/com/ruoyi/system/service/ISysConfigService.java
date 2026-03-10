package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysConfig;

/**
 * 系统配置 服务层
 * 
 * @author ruoyi
 */
public interface ISysConfigService
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
    public String selectConfigByKey(String configKey);

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
     * 批量删除系统配置
     * 
     * @param configIds 需要删除的系统配置ID
     * @return 结果
     */
    public void deleteConfigByIds(Long[] configIds);

    /**
     * 重置系统配置缓存
     */
    public void resetConfigCache();

    /**
     * 校验配置键名是否唯一
     * 
     * @param config 系统配置信息
     * @return 结果
     */
    public boolean checkConfigKeyUnique(SysConfig config);

    /**
     * 获取验证码开关
     * 
     * @return true 开启，false 关闭
     */
    public boolean selectCaptchaEnabled();
}
