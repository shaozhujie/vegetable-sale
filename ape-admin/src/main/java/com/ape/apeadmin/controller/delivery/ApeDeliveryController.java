package com.ape.apeadmin.controller.delivery;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeDelivery;
import com.ape.apesystem.service.ApeDeliveryService;
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
 * @description: 配送controller
 * @date 2024/03/28 05:03
 */
@Controller
@ResponseBody
@RequestMapping("delivery")
public class ApeDeliveryController {

    @Autowired
    private ApeDeliveryService apeDeliveryService;

    /** 分页获取配送 */
    @Log(name = "分页获取配送", type = BusinessType.OTHER)
    @PostMapping("getApeDeliveryPage")
    public Result getApeDeliveryPage(@RequestBody ApeDelivery apeDelivery) {
        Page<ApeDelivery> page = new Page<>(apeDelivery.getPageNumber(),apeDelivery.getPageSize());
        QueryWrapper<ApeDelivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeDelivery.getName()),ApeDelivery::getName,apeDelivery.getName())
                .eq(StringUtils.isNotBlank(apeDelivery.getTel()),ApeDelivery::getTel,apeDelivery.getTel());
        Page<ApeDelivery> apeDeliveryPage = apeDeliveryService.page(page, queryWrapper);
        return Result.success(apeDeliveryPage);
    }

    @GetMapping("getApeDeliveryList")
    public Result getApeDeliveryList() {
        List<ApeDelivery> deliveryList = apeDeliveryService.list();
        return Result.success(deliveryList);
    }

    /** 根据id获取配送 */
    @Log(name = "根据id获取配送", type = BusinessType.OTHER)
    @GetMapping("getApeDeliveryById")
    public Result getApeDeliveryById(@RequestParam("id")String id) {
        ApeDelivery apeDelivery = apeDeliveryService.getById(id);
        return Result.success(apeDelivery);
    }

    /** 保存配送 */
    @Log(name = "保存配送", type = BusinessType.INSERT)
    @PostMapping("saveApeDelivery")
    public Result saveApeDelivery(@RequestBody ApeDelivery apeDelivery) {
        boolean save = apeDeliveryService.save(apeDelivery);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑配送 */
    @Log(name = "编辑配送", type = BusinessType.UPDATE)
    @PostMapping("editApeDelivery")
    public Result editApeDelivery(@RequestBody ApeDelivery apeDelivery) {
        boolean save = apeDeliveryService.updateById(apeDelivery);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除配送 */
    @GetMapping("removeApeDelivery")
    @Log(name = "删除配送", type = BusinessType.DELETE)
    public Result removeApeDelivery(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeDeliveryService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("配送id不能为空！");
        }
    }

}