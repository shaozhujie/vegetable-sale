package com.ape.apeadmin.controller.information;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeInformation;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeInformationService;
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
 * @description: 资讯controller
 * @date 2024/03/28 11:26
 */
@Controller
@ResponseBody
@RequestMapping("information")
public class ApeInformationController {

    @Autowired
    private ApeInformationService apeInformationService;

    /** 分页获取资讯 */
    @Log(name = "分页获取资讯", type = BusinessType.OTHER)
    @PostMapping("getApeInformationPage")
    public Result getApeInformationPage(@RequestBody ApeInformation apeInformation) {
        Page<ApeInformation> page = new Page<>(apeInformation.getPageNumber(),apeInformation.getPageSize());
        QueryWrapper<ApeInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeInformation.getTitle()),ApeInformation::getTitle,apeInformation.getTitle());
        Page<ApeInformation> apeInformationPage = apeInformationService.page(page, queryWrapper);
        return Result.success(apeInformationPage);
    }

    @GetMapping("getApeInformationList")
    public Result getApeInformationList() {
        QueryWrapper<ApeInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(ApeInformation::getCreateTime).last("limit 3");
        List<ApeInformation> informationList = apeInformationService.list(queryWrapper);
        return Result.success(informationList);
    }

    /** 根据id获取资讯 */
    @Log(name = "根据id获取资讯", type = BusinessType.OTHER)
    @GetMapping("getApeInformationById")
    public Result getApeInformationById(@RequestParam("id")String id) {
        ApeInformation apeInformation = apeInformationService.getById(id);
        return Result.success(apeInformation);
    }

    /** 保存资讯 */
    @Log(name = "保存资讯", type = BusinessType.INSERT)
    @PostMapping("saveApeInformation")
    public Result saveApeInformation(@RequestBody ApeInformation apeInformation) {
        boolean save = apeInformationService.save(apeInformation);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑资讯 */
    @Log(name = "编辑资讯", type = BusinessType.UPDATE)
    @PostMapping("editApeInformation")
    public Result editApeInformation(@RequestBody ApeInformation apeInformation) {
        boolean save = apeInformationService.updateById(apeInformation);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除资讯 */
    @GetMapping("removeApeInformation")
    @Log(name = "删除资讯", type = BusinessType.DELETE)
    public Result removeApeInformation(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeInformationService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("资讯id不能为空！");
        }
    }

}