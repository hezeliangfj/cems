/*
 * @Author: hezeliangfj
 * @Date: 2026-03-09 13:04:08
 * @LastEditors: hezeliangfj
 * @LastEditTime: 2026-03-09 13:05:52
 * @version: 0.0.1
 * @FilePath: \demo\admin\src\main\java\com\ruoyi\system\domain\SysConfig.java
 * @Descripttion: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 系统配置表 sys_config
 * 
 * @author ruoyi
 */
public class SysConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long configId;

    /** 配置键名 */
    private String configKey;

    /** 配置值 */
    private String configValue;

    /** 配置名称 */
    private String configName;

    /** 系统内置（Y是 N否） */
    private String configType;

    public Long getConfigId()
    {
        return configId;
    }

    public void setConfigId(Long configId)
    {
        this.configId = configId;
    }

    public String getConfigKey()
    {
        return configKey;
    }

    public void setConfigKey(String configKey)
    {
        this.configKey = configKey;
    }

    public String getConfigValue()
    {
        return configValue;
    }

    public void setConfigValue(String configValue)
    {
        this.configValue = configValue;
    }

    public String getConfigName()
    {
        return configName;
    }

    public void setConfigName(String configName)
    {
        this.configName = configName;
    }

    public String getConfigType()
    {
        return configType;
    }

    public void setConfigType(String configType)
    {
        this.configType = configType;
    }
}
