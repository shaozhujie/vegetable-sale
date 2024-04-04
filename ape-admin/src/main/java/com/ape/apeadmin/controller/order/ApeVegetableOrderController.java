package com.ape.apeadmin.controller.order;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.*;
import com.ape.apesystem.service.ApeDeliveryService;
import com.ape.apesystem.service.ApeVegetableOrderService;
import com.ape.apesystem.service.ApeVegetableService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 订单controller
 * @date 2024/03/28 03:37
 */
@Controller
@ResponseBody
@RequestMapping("order")
public class ApeVegetableOrderController {

    @Autowired
    private ApeVegetableOrderService apeVegetableOrderService;
    @Autowired
    private ApeVegetableService apeVegetableService;
    @Autowired
    private ApeDeliveryService apeDeliveryService;

    /** 分页获取订单 */
    @Log(name = "分页获取订单", type = BusinessType.OTHER)
    @PostMapping("getApeVegetableOrderPage")
    public Result getApeVegetableOrderPage(@RequestBody ApeVegetableOrder apeVegetableOrder) {
        Page<ApeVegetableOrder> page = new Page<>(apeVegetableOrder.getPageNumber(),apeVegetableOrder.getPageSize());
        QueryWrapper<ApeVegetableOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeVegetableOrder.getUserId()),ApeVegetableOrder::getUserId,apeVegetableOrder.getUserId())
                .like(StringUtils.isNotBlank(apeVegetableOrder.getOrderNumber()),ApeVegetableOrder::getOrderNumber,apeVegetableOrder.getOrderNumber())
                .like(StringUtils.isNotBlank(apeVegetableOrder.getName()),ApeVegetableOrder::getName,apeVegetableOrder.getName())
                .eq(apeVegetableOrder.getState() != null,ApeVegetableOrder::getState,apeVegetableOrder.getState())
                .eq(StringUtils.isNotBlank(apeVegetableOrder.getCreateBy()),ApeVegetableOrder::getCreateBy,apeVegetableOrder.getCreateBy())
                .eq(apeVegetableOrder.getCreateTime() != null,ApeVegetableOrder::getCreateTime,apeVegetableOrder.getCreateTime())
                .orderByDesc(ApeVegetableOrder::getCreateTime);
        Page<ApeVegetableOrder> apeVegetableOrderPage = apeVegetableOrderService.page(page, queryWrapper);
        return Result.success(apeVegetableOrderPage);
    }

    /** 根据id获取订单 */
    @Log(name = "根据id获取订单", type = BusinessType.OTHER)
    @GetMapping("getApeVegetableOrderById")
    public Result getApeVegetableOrderById(@RequestParam("id")String id) {
        ApeVegetableOrder apeVegetableOrder = apeVegetableOrderService.getById(id);
        return Result.success(apeVegetableOrder);
    }

    /** 保存订单 */
    @Log(name = "保存订单", type = BusinessType.INSERT)
    @PostMapping("saveApeVegetableOrder")
    public Result saveApeVegetableOrder(@RequestBody ApeVegetableOrder apeVegetableOrder) {
        ApeVegetable vegetable = apeVegetableService.getById(apeVegetableOrder.getVegetableId());
        apeVegetableOrder.setName(vegetable.getName());
        apeVegetableOrder.setOrderNumber(IdWorker.getMillisecond());
        apeVegetableOrder.setUnit(vegetable.getUnit());
        apeVegetableOrder.setImages(vegetable.getImages());
        ApeUser user = ShiroUtils.getUserInfo();
        apeVegetableOrder.setUserId(user.getId());
        boolean save = apeVegetableOrderService.save(apeVegetableOrder);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("saveApeVegetableCarOrder")
    public Result saveApeVegetableCarOrder(@RequestBody JSONObject jsonObject) {
        String address = jsonObject.getString("address");
        String tel = jsonObject.getString("tel");
        String realName = jsonObject.getString("realName");
        Date arrivalTime = jsonObject.getDate("arrivalTime");
        JSONArray array = jsonObject.getJSONArray("arr");
        List<ApeVegetableOrder> list = new ArrayList<>();
        for (int i = 0; i < array.size();i++) {
            JSONObject object = array.getJSONObject(i);
            ApeCar apeCar = object.toJavaObject(ApeCar.class);
            ApeVegetable vegetable = apeVegetableService.getById(apeCar.getVegetableId());
            ApeVegetableOrder apeVegetableOrder = new ApeVegetableOrder();
            apeVegetableOrder.setVegetableId(vegetable.getId());
            apeVegetableOrder.setOrderNumber(IdWorker.getMillisecond());
            apeVegetableOrder.setName(vegetable.getName());
            apeVegetableOrder.setNum(apeCar.getNum());
            apeVegetableOrder.setPrice(vegetable.getPrice() * apeCar.getNum());
            apeVegetableOrder.setUnit(vegetable.getUnit());
            apeVegetableOrder.setImages(vegetable.getImages());
            apeVegetableOrder.setUserId(ShiroUtils.getUserInfo().getId());
            apeVegetableOrder.setRealName(realName);
            apeVegetableOrder.setTel(tel);
            apeVegetableOrder.setArrivalTime(arrivalTime);
            apeVegetableOrder.setAddress(address);
            apeVegetableOrderService.save(apeVegetableOrder);
        }
        return Result.success();
    }

    /** 编辑订单 */
    @Log(name = "编辑订单", type = BusinessType.UPDATE)
    @PostMapping("editApeVegetableOrder")
    public Result editApeVegetableOrder(@RequestBody ApeVegetableOrder apeVegetableOrder) {
        if (StringUtils.isNotBlank(apeVegetableOrder.getDeliveryId())) {
            ApeDelivery delivery = apeDeliveryService.getById(apeVegetableOrder.getDeliveryId());
            apeVegetableOrder.setDeliveryName(delivery.getName());
            apeVegetableOrder.setDeliveryTel(delivery.getTel());
        }
        boolean save = apeVegetableOrderService.updateById(apeVegetableOrder);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除订单 */
    @GetMapping("removeApeVegetableOrder")
    @Log(name = "删除订单", type = BusinessType.DELETE)
    public Result removeApeVegetableOrder(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeVegetableOrderService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("订单id不能为空！");
        }
    }

}