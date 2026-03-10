package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.system.mapper.SysDictTypeMapper;
import com.ruoyi.system.service.ISysDictTypeService;

/**
 * 字典类型 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class SysDictTypeServiceImpl implements ISysDictTypeService
{
    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 查询字典类型列表
     * 
     * @param dictType 字典类型信息
     * @return 字典类型集合
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType)
    {
        return dictTypeMapper.selectDictTypeList(dictType);
    }

    /**
     * 查询字典类型信息
     * 
     * @param dictId 字典类型ID
     * @return 字典类型信息
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId)
    {
        return dictTypeMapper.selectDictTypeById(dictId);
    }

    /**
     * 根据字典类型查询字典数据
     * 
     * @param dictType 字典类型
     * @return 字典类型信息
     */
    @Override
    public SysDictType selectDictTypeByType(String dictType)
    {
        return dictTypeMapper.selectDictTypeByType(dictType);
    }

    /**
     * 新增字典类型
     * 
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    public int insertDictType(SysDictType dictType)
    {
        return dictTypeMapper.insertDictType(dictType);
    }

    /**
     * 修改字典类型
     * 
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    public int updateDictType(SysDictType dictType)
    {
        return dictTypeMapper.updateDictType(dictType);
    }

    /**
     * 批量删除字典类型
     * 
     * @param dictIds 需要删除的字典类型ID
     * @return 结果
     */
    @Override
    public void deleteDictTypeByIds(Long[] dictIds)
    {
        for (Long dictId : dictIds)
        {
            SysDictType dictType = selectDictTypeById(dictId);
            dictTypeMapper.deleteDictTypeById(dictId);
        }
    }

    /**
     * 重置字典缓存
     */
    @Override
    public void resetDictCache()
    {
        // 清除字典缓存
        redisCache.deleteObject(CacheConstants.SYS_DICT_KEY + "*");
    }

    /**
     * 校验字典类型称是否唯一
     * 
     * @param dictType 字典类型
     * @return 结果
     */
    @Override
    public boolean checkDictTypeUnique(SysDictType dictType)
    {
        Long dictId = StringUtils.isNull(dictType.getDictId()) ? -1L : dictType.getDictId();
        SysDictType info = dictTypeMapper.checkDictTypeUnique(dictType.getDictType());
        if (StringUtils.isNotNull(info) && info.getDictId().longValue() != dictId.longValue())
        {
            return false;
        }
        return true;
    }

    /**
     * 获取所有字典类型
     * 
     * @return 字典类型列表
     */
    @Override
    public List<SysDictType> selectDictTypeAll()
    {
        return dictTypeMapper.selectDictTypeList(new SysDictType());
    }
}
