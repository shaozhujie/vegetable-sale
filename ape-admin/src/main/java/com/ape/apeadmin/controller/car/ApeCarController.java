package com.ape.apeadmin.controller.car;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeCar;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.domain.ApeVegetable;
import com.ape.apesystem.service.ApeCarService;
import com.ape.apesystem.service.ApeVegetableService;
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
 * @description: 购物车controller
 * @date 2024/03/28 09:41
 */
@Controller
@ResponseBody
@RequestMapping("car")
public class ApeCarController {

    @Autowired
    private ApeCarService apeCarService;
    @Autowired
    private ApeVegetableService apeVegetableService;

    /** 分页获取购物车 */
    @Log(name = "分页获取购物车", type = BusinessType.OTHER)
    @PostMapping("getApeCarList")
    public Result getApeCarPage(@RequestBody ApeCar apeCar) {
        QueryWrapper<ApeCar> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeCar.getVegetableId()),ApeCar::getVegetableId,apeCar.getVegetableId())
                .eq(StringUtils.isNotBlank(apeCar.getName()),ApeCar::getName,apeCar.getName())
                .eq(StringUtils.isNotBlank(apeCar.getImages()),ApeCar::getImages,apeCar.getImages())
                .eq(apeCar.getPrice() != null,ApeCar::getPrice,apeCar.getPrice())
                .eq(StringUtils.isNotBlank(apeCar.getUnit()),ApeCar::getUnit,apeCar.getUnit())
                .eq(ApeCar::getUserId,ShiroUtils.getUserInfo().getId())
                .eq(StringUtils.isNotBlank(apeCar.getCreateBy()),ApeCar::getCreateBy,apeCar.getCreateBy())
                .eq(apeCar.getCreateTime() != null,ApeCar::getCreateTime,apeCar.getCreateTime())
                .eq(StringUtils.isNotBlank(apeCar.getUpdateBy()),ApeCar::getUpdateBy,apeCar.getUpdateBy())
                .eq(apeCar.getUpdateTime() != null,ApeCar::getUpdateTime,apeCar.getUpdateTime())
                .orderByDesc(ApeCar::getCreateTime);
        List<ApeCar> apeCarPage = apeCarService.list(queryWrapper);
        return Result.success(apeCarPage);
    }

    /** 根据id获取购物车 */
    @Log(name = "根据id获取购物车", type = BusinessType.OTHER)
    @GetMapping("getApeCarById")
    public Result getApeCarById(@RequestParam("id")String id) {
        ApeCar apeCar = apeCarService.getById(id);
        return Result.success(apeCar);
    }

    /** 保存购物车 */
    @Log(name = "保存购物车", type = BusinessType.INSERT)
    @PostMapping("saveApeCar")
    public Result saveApeCar(@RequestBody ApeCar apeCar) {
        ApeVegetable vegetable = apeVegetableService.getById(apeCar.getVegetableId());
        ApeUser userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ApeCar> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeCar::getVegetableId,vegetable.getId())
                        .eq(ApeCar::getUserId,userInfo.getId()).last("limit 1");
        ApeCar car = apeCarService.getOne(queryWrapper);
        if (car == null) {
            apeCar.setName(vegetable.getName());
            apeCar.setImages(vegetable.getImages());
            apeCar.setPrice(vegetable.getPrice());
            apeCar.setUnit(vegetable.getUnit());
            apeCar.setUserId(userInfo.getId());
            boolean save = apeCarService.save(apeCar);
            if (save) {
                return Result.success();
            } else {
                return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
            }
        } else {
            car.setNum(car.getNum() + 1);
            boolean update = apeCarService.updateById(car);
            if (update) {
                return Result.success();
            } else {
                return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
            }
        }
    }

    /** 编辑购物车 */
    @Log(name = "编辑购物车", type = BusinessType.UPDATE)
    @PostMapping("editApeCar")
    public Result editApeCar(@RequestBody ApeCar apeCar) {
        boolean save = apeCarService.updateById(apeCar);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除购物车 */
    @GetMapping("removeApeCar")
    @Log(name = "删除购物车", type = BusinessType.DELETE)
    public Result removeApeCar(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeCarService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("购物车id不能为空！");
        }
    }

}