package com.ape.apeadmin.controller.vegetable;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeCar;
import com.ape.apesystem.domain.ApeVegetable;
import com.ape.apesystem.domain.ApeVegetableFavor;
import com.ape.apesystem.domain.ApeVegetableOrder;
import com.ape.apesystem.service.ApeCarService;
import com.ape.apesystem.service.ApeVegetableFavorService;
import com.ape.apesystem.service.ApeVegetableOrderService;
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
 * @description: 蔬菜controller
 * @date 2024/03/28 10:39
 */
@Controller
@ResponseBody
@RequestMapping("vegetable")
public class ApeVegetableController {

    @Autowired
    private ApeVegetableService apeVegetableService;
    @Autowired
    private ApeVegetableFavorService apeVegetableFavorService;
    @Autowired
    private ApeVegetableOrderService apeVegetableOrderService;
    @Autowired
    private ApeCarService apeCarService;

    /** 分页获取蔬菜 */
    @Log(name = "分页获取蔬菜", type = BusinessType.OTHER)
    @PostMapping("getApeVegetablePage")
    public Result getApeVegetablePage(@RequestBody ApeVegetable apeVegetable) {
        Page<ApeVegetable> page = new Page<>(apeVegetable.getPageNumber(),apeVegetable.getPageSize());
        QueryWrapper<ApeVegetable> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(apeVegetable.getState() != null,ApeVegetable::getState,apeVegetable.getState())
                .like(StringUtils.isNotBlank(apeVegetable.getName()),ApeVegetable::getName,apeVegetable.getName())
                .eq(StringUtils.isNotBlank(apeVegetable.getType()),ApeVegetable::getType,apeVegetable.getType());
        if (apeVegetable.getSort() == 1) {
            queryWrapper.lambda().orderByDesc(ApeVegetable::getCreateTime);
        }
        if (apeVegetable.getSort() == 2) {
            queryWrapper.lambda().orderByAsc(ApeVegetable::getPrice);
        }
        if (apeVegetable.getSort() == 3) {
            queryWrapper.lambda().orderByDesc(ApeVegetable::getPrice);
        }
        Page<ApeVegetable> apeVegetablePage = apeVegetableService.page(page, queryWrapper);
        return Result.success(apeVegetablePage);
    }

    @GetMapping("getApeVegetableIndex")
    public Result getApeVegetableIndex() {
        QueryWrapper<ApeVegetable> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeVegetable::getState,1).orderByDesc(ApeVegetable::getCreateTime).last("limit 8");
        List<ApeVegetable> vegetableList = apeVegetableService.list(queryWrapper);
        return Result.success(vegetableList);
    }

    @GetMapping("/getApeVegetableList")
    public Result getApeVegetableList() {
        List<ApeVegetable> vegetableList = apeVegetableService.list();
        return Result.success(vegetableList);
    }

    /** 根据id获取蔬菜 */
    @Log(name = "根据id获取蔬菜", type = BusinessType.OTHER)
    @GetMapping("getApeVegetableById")
    public Result getApeVegetableById(@RequestParam("id")String id) {
        ApeVegetable apeVegetable = apeVegetableService.getById(id);
        return Result.success(apeVegetable);
    }

    /** 保存蔬菜 */
    @Log(name = "保存蔬菜", type = BusinessType.INSERT)
    @PostMapping("saveApeVegetable")
    public Result saveApeVegetable(@RequestBody ApeVegetable apeVegetable) {
        boolean save = apeVegetableService.save(apeVegetable);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑蔬菜 */
    @Log(name = "编辑蔬菜", type = BusinessType.UPDATE)
    @PostMapping("editApeVegetable")
    public Result editApeVegetable(@RequestBody ApeVegetable apeVegetable) {
        boolean save = apeVegetableService.updateById(apeVegetable);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除蔬菜 */
    @Transactional(rollbackFor = Exception.class)
    @GetMapping("removeApeVegetable")
    @Log(name = "删除蔬菜", type = BusinessType.DELETE)
    public Result removeApeVegetable(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeVegetableService.removeById(id);
                QueryWrapper<ApeVegetableFavor> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ApeVegetableFavor::getVegetableId,id);
                apeVegetableFavorService.remove(queryWrapper);

                QueryWrapper<ApeVegetableOrder> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.lambda().eq(ApeVegetableOrder::getVegetableId,id);
                apeVegetableOrderService.remove(queryWrapper1);

                QueryWrapper<ApeCar> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.lambda().eq(ApeCar::getVegetableId,id);
                apeCarService.remove(queryWrapper2);
            }
            return Result.success();
        } else {
            return Result.fail("蔬菜id不能为空！");
        }
    }

}