package com.ape.apeadmin.controller.favor;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.domain.ApeVegetable;
import com.ape.apesystem.domain.ApeVegetableFavor;
import com.ape.apesystem.service.ApeVegetableFavorService;
import com.ape.apesystem.service.ApeVegetableService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 收藏controller
 * @date 2024/03/28 09:23
 */
@Controller
@ResponseBody
@RequestMapping("favor")
public class ApeVegetableFavorController {

    @Autowired
    private ApeVegetableFavorService apeVegetableFavorService;
    @Autowired
    private ApeVegetableService apeVegetableService;

    /** 分页获取收藏 */
    @Log(name = "分页获取收藏", type = BusinessType.OTHER)
    @PostMapping("getApeVegetableFavorPage")
    public Result getApeVegetableFavorPage(@RequestBody ApeVegetableFavor apeVegetableFavor) {
        apeVegetableFavor.setUserId(ShiroUtils.getUserInfo().getId());
        Page<ApeVegetableFavor> page = new Page<>(apeVegetableFavor.getPageNumber(),apeVegetableFavor.getPageSize());
        QueryWrapper<ApeVegetableFavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeVegetableFavor.getName()),ApeVegetableFavor::getName,apeVegetableFavor.getName())
                .eq(StringUtils.isNotBlank(apeVegetableFavor.getUnit()),ApeVegetableFavor::getUnit,apeVegetableFavor.getUnit())
                .eq(apeVegetableFavor.getPrice() != null,ApeVegetableFavor::getPrice,apeVegetableFavor.getPrice())
                .eq(StringUtils.isNotBlank(apeVegetableFavor.getVegetableId()),ApeVegetableFavor::getVegetableId,apeVegetableFavor.getVegetableId())
                .eq(StringUtils.isNotBlank(apeVegetableFavor.getUserId()),ApeVegetableFavor::getUserId,apeVegetableFavor.getUserId());
        Page<ApeVegetableFavor> apeVegetableFavorPage = apeVegetableFavorService.page(page, queryWrapper);
        return Result.success(apeVegetableFavorPage);
    }

    /** 根据id获取收藏 */
    @Log(name = "根据id获取收藏", type = BusinessType.OTHER)
    @GetMapping("getApeVegetableFavorById")
    public Result getApeVegetableFavorById(@RequestParam("id")String id) {
        ApeVegetableFavor apeVegetableFavor = apeVegetableFavorService.getById(id);
        return Result.success(apeVegetableFavor);
    }

    /** 保存收藏 */
    @Log(name = "保存收藏", type = BusinessType.INSERT)
    @PostMapping("saveApeVegetableFavor")
    public Result saveApeVegetableFavor(@RequestBody ApeVegetableFavor apeVegetableFavor) {
        ApeVegetable vegetable = apeVegetableService.getById(apeVegetableFavor.getVegetableId());
        apeVegetableFavor.setName(vegetable.getName());
        apeVegetableFavor.setUnit(vegetable.getUnit());
        apeVegetableFavor.setPrice(vegetable.getPrice());
        apeVegetableFavor.setImages(vegetable.getImages());
        ApeUser user = ShiroUtils.getUserInfo();
        apeVegetableFavor.setUserId(user.getId());
        boolean save = apeVegetableFavorService.save(apeVegetableFavor);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑收藏 */
    @Log(name = "编辑收藏", type = BusinessType.UPDATE)
    @PostMapping("editApeVegetableFavor")
    public Result editApeVegetableFavor(@RequestBody ApeVegetableFavor apeVegetableFavor) {
        boolean save = apeVegetableFavorService.updateById(apeVegetableFavor);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除收藏 */
    @GetMapping("removeApeVegetableFavor")
    @Log(name = "删除收藏", type = BusinessType.DELETE)
    public Result removeApeVegetableFavor(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeVegetableFavorService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("收藏id不能为空！");
        }
    }

    @GetMapping("getFavor")
    public Result getFavor(@RequestParam("id")String id) {
        ApeUser user = ShiroUtils.getUserInfo();
        QueryWrapper<ApeVegetableFavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeVegetableFavor::getVegetableId,id)
                .eq(ApeVegetableFavor::getUserId,user.getId()).last("limit 1");
        ApeVegetableFavor favor = apeVegetableFavorService.getOne(queryWrapper);
        if (favor != null) {
            return Result.success(favor);
        } else {
            return Result.fail();
        }
    }

}