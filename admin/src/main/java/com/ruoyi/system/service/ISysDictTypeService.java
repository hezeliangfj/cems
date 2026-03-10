package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.common.core.domain.entity.SysDictType;

/**
 * 字典类型 服务层
 * 
 * @author ruoyi
 */
public interface ISysDictTypeService
{
    /**
     * 查询字典类型列表
     * 
     * @param dictType 字典类型信息
     * @return 字典类型集合
     */
    public List<SysDictType> selectDictTypeList(SysDictType dictType);

    /**
     * 查询字典类型信息
     * 
     * @param dictId 字典类型ID
     * @return 字典类型信息
     */
    public SysDictType selectDictTypeById(Long dictId);

    /**
     * 根据字典类型查询字典数据
     * 
     * @param dictType 字典类型
     * @return 字典类型信息
     */
    public SysDictType selectDictTypeByType(String dictType);

    /**
     * 新增字典类型
     * 
     * @param dictType 字典类型信息
     * @return 结果
     */
    public int insertDictType(SysDictType dictType);

    /**
     * 修改字典类型
     * 
     * @param dictType 字典类型信息
     * @return 结果
     */
    public int updateDictType(SysDictType dictType);

    /**
     * 批量删除字典类型
     * 
     * @param dictIds 需要删除的字典类型ID
     * @return 结果
     */
    public void deleteDictTypeByIds(Long[] dictIds);

    /**
     * 重置字典缓存
     */
    public void resetDictCache();

    /**
     * 校验字典类型称是否唯一
     * 
     * @param dictType 字典类型
     * @return 结果
     */
    public boolean checkDictTypeUnique(SysDictType dictType);

    /**
     * 获取所有字典类型
     * 
     * @return 字典类型列表
     */
    public List<SysDictType> selectDictTypeAll();
}
