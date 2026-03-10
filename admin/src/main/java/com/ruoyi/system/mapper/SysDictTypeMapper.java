package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.common.core.domain.entity.SysDictType;

/**
 * 字典类型 数据层
 * 
 * @author ruoyi
 */
public interface SysDictTypeMapper
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
     * 校验字典类型称是否唯一
     * 
     * @param dictType 字典类型
     * @return 字典类型信息
     */
    public SysDictType checkDictTypeUnique(String dictType);

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
     * 删除字典类型
     * 
     * @param dictId 字典类型ID
     * @return 结果
     */
    public int deleteDictTypeById(Long dictId);

    /**
     * 批量删除字典类型
     * 
     * @param dictIds 需要删除的字典类型ID
     * @return 结果
     */
    public int deleteDictTypeByIds(Long[] dictIds);

    /**
     * 获取所有字典类型
     * 
     * @return 字典类型列表
     */
    public List<SysDictType> selectDictTypeAll();
}
