package com.ape.apeadmin.controller.rotation;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeRotation;
import com.ape.apesystem.service.ApeRotationService;
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
 * @description: 轮播图controller
 * @date 2024/03/29 09:12
 */
@Controller
@ResponseBody
@RequestMapping("rotation")
public class ApeRotationController {

    @Autowired
    private ApeRotationService apeRotationService;

    /** 分页获取轮播图 */
    @Log(name = "分页获取轮播图", type = BusinessType.OTHER)
    @PostMapping("getApeRotationPage")
    public Result getApeRotationPage(@RequestBody ApeRotation apeRotation) {
        Page<ApeRotation> page = new Page<>(apeRotation.getPageNumber(),apeRotation.getPageSize());
        QueryWrapper<ApeRotation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeRotation.getContent()),ApeRotation::getContent,apeRotation.getContent());
        Page<ApeRotation> apeRotationPage = apeRotationService.page(page, queryWrapper);
        return Result.success(apeRotationPage);
    }

    @GetMapping("getApeRotationList")
    public Result getApeRotationList() {
        List<ApeRotation> rotationList = apeRotationService.list();
        return Result.success(rotationList);
    }

    /** 根据id获取轮播图 */
    @Log(name = "根据id获取轮播图", type = BusinessType.OTHER)
    @GetMapping("getApeRotationById")
    public Result getApeRotationById(@RequestParam("id")String id) {
        ApeRotation apeRotation = apeRotationService.getById(id);
        return Result.success(apeRotation);
    }

    /** 保存轮播图 */
    @Log(name = "保存轮播图", type = BusinessType.INSERT)
    @PostMapping("saveApeRotation")
    public Result saveApeRotation(@RequestBody ApeRotation apeRotation) {
        boolean save = apeRotationService.save(apeRotation);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑轮播图 */
    @Log(name = "编辑轮播图", type = BusinessType.UPDATE)
    @PostMapping("editApeRotation")
    public Result editApeRotation(@RequestBody ApeRotation apeRotation) {
        boolean save = apeRotationService.updateById(apeRotation);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除轮播图 */
    @GetMapping("removeApeRotation")
    @Log(name = "删除轮播图", type = BusinessType.DELETE)
    public Result removeApeRotation(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeRotationService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("轮播图id不能为空！");
        }
    }

}