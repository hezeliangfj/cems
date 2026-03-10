package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 系统配置 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class SysConfigServiceImpl implements ISysConfigService
{
    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 查询系统配置列表
     * 
     * @param config 系统配置信息
     * @return 系统配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config)
    {
        return configMapper.selectConfigList(config);
    }

    /**
     * 查询系统配置信息
     * 
     * @param configId 系统配置ID
     * @return 系统配置信息
     */
    @Override
    public SysConfig selectConfigById(Long configId)
    {
        return configMapper.selectConfigById(configId);
    }

    /**
     * 根据键名查询系统配置信息
     * 
     * @param configKey 系统配置键名
     * @return 系统配置信息
     */
    @Override
    public String selectConfigByKey(String configKey)
    {
        String configValue = redisCache.getCacheObject(getCacheKey(configKey));
        if (StringUtils.isNotEmpty(configValue))
        {
            return configValue;
        }
        SysConfig config = configMapper.selectConfigByKey(configKey);
        if (StringUtils.isNotNull(config))
        {
            redisCache.setCacheObject(getCacheKey(configKey), config.getConfigValue(), 30, TimeUnit.MINUTES);
            return config.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 新增系统配置
     * 
     * @param config 系统配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config)
    {
        int row = configMapper.insertConfig(config);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue(), 30, TimeUnit.MINUTES);
        }
        return row;
    }

    /**
     * 修改系统配置
     * 
     * @param config 系统配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config)
    {
        int row = configMapper.updateConfig(config);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue(), 30, TimeUnit.MINUTES);
        }
        return row;
    }

    /**
     * 批量删除系统配置
     * 
     * @param configIds 需要删除的系统配置ID
     * @return 结果
     */
    @Override
    public void deleteConfigByIds(Long[] configIds)
    {
        for (Long configId : configIds)
        {
            SysConfig config = selectConfigById(configId);
            redisCache.deleteObject(getCacheKey(config.getConfigKey()));
        }
        configMapper.deleteConfigByIds(configIds);
    }

    /**
     * 重置系统配置缓存
     */
    @Override
    public void resetConfigCache()
    {
        List<SysConfig> configList = configMapper.selectConfigList(new SysConfig());
        for (SysConfig config : configList)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue(), 30, TimeUnit.MINUTES);
        }
    }

    /**
     * 校验配置键名是否唯一
     * 
     * @param config 系统配置信息
     * @return 结果
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfig config)
    {
        Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue())
        {
            return false;
        }
        return true;
    }

    /**
     * 获取验证码开关
     * 
     * @return true 开启，false 关闭
     */
    @Override
    public boolean selectCaptchaEnabled()
    {
        String captchaEnabled = selectConfigByKey("sys.account.captchaEnabled");
        if (StringUtils.isEmpty(captchaEnabled))
        {
            return true;
        }
        return Boolean.parseBoolean(captchaEnabled);
    }

    /**
     * 缓存键名
     * 
     * @param configKey 配置键名
     * @return 缓存键名
     */
    private String getCacheKey(String configKey)
    {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }
}
