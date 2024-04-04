package com.ape.apeadmin.controller.type;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeVegetableType;
import com.ape.apesystem.service.ApeVegetableTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 蔬菜分类controller
 * @date 2024/03/28 09:49
 */
@Controller
@ResponseBody
@RequestMapping("type")
public class ApeVegetableTypeController {

    @Autowired
    private ApeVegetableTypeService apeVegetableTypeService;

    /** 分页获取蔬菜分类 */
    @Log(name = "分页获取蔬菜分类", type = BusinessType.OTHER)
    @PostMapping("getApeVegetableTypePage")
    public Result getApeVegetableTypePage(@RequestBody ApeVegetableType apeVegetableType) {
        Page<ApeVegetableType> page = new Page<>(apeVegetableType.getPageNumber(),apeVegetableType.getPageSize());
        QueryWrapper<ApeVegetableType> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeVegetableType.getName()),ApeVegetableType::getName,apeVegetableType.getName());
        Page<ApeVegetableType> apeVegetableTypePage = apeVegetableTypeService.page(page, queryWrapper);
        return Result.success(apeVegetableTypePage);
    }

    /** 根据id获取蔬菜分类 */
    @Log(name = "根据id获取蔬菜分类", type = BusinessType.OTHER)
    @GetMapping("getApeVegetableTypeById")
    public Result getApeVegetableTypeById(@RequestParam("id")String id) {
        ApeVegetableType apeVegetableType = apeVegetableTypeService.getById(id);
        return Result.success(apeVegetableType);
    }

    @GetMapping("getApeVegetableTypeList")
    public Result getApeVegetableTypeList() {
        List<ApeVegetableType> typeList = apeVegetableTypeService.list();
        return Result.success(typeList);
    }

    /** 保存蔬菜分类 */
    @Log(name = "保存蔬菜分类", type = BusinessType.INSERT)
    @PostMapping("saveApeVegetableType")
    public Result saveApeVegetableType(@RequestBody ApeVegetableType apeVegetableType) {
        boolean save = apeVegetableTypeService.save(apeVegetableType);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑蔬菜分类 */
    @Log(name = "编辑蔬菜分类", type = BusinessType.UPDATE)
    @PostMapping("editApeVegetableType")
    public Result editApeVegetableType(@RequestBody ApeVegetableType apeVegetableType) {
        boolean save = apeVegetableTypeService.updateById(apeVegetableType);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除蔬菜分类 */
    @GetMapping("removeApeVegetableType")
    @Log(name = "删除蔬菜分类", type = BusinessType.DELETE)
    public Result removeApeVegetableType(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeVegetableTypeService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("蔬菜分类id不能为空！");
        }
    }

}