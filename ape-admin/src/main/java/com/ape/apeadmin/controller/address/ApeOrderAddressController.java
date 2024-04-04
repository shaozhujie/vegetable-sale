package com.ape.apeadmin.controller.address;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeOrderAddress;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeOrderAddressService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
 * @description: 配送地址controller
 * @date 2024/03/28 10:11
 */
@Controller
@ResponseBody
@RequestMapping("address")
public class ApeOrderAddressController {

    @Autowired
    private ApeOrderAddressService apeOrderAddressService;

    /** 分页获取配送地址 */
    @Log(name = "分页获取配送地址", type = BusinessType.OTHER)
    @PostMapping("getApeOrderAddressPage")
    public Result getApeOrderAddressPage(@RequestBody ApeOrderAddress apeOrderAddress) {
        Page<ApeOrderAddress> page = new Page<>(apeOrderAddress.getPageNumber(),apeOrderAddress.getPageSize());
        QueryWrapper<ApeOrderAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeOrderAddress.getName()),ApeOrderAddress::getName,apeOrderAddress.getName())
                .eq(StringUtils.isNotBlank(apeOrderAddress.getTel()),ApeOrderAddress::getTel,apeOrderAddress.getTel())
                .eq(StringUtils.isNotBlank(apeOrderAddress.getAddress()),ApeOrderAddress::getAddress,apeOrderAddress.getAddress())
                .eq(StringUtils.isNotBlank(apeOrderAddress.getUserId()),ApeOrderAddress::getUserId,apeOrderAddress.getUserId());
        Page<ApeOrderAddress> apeOrderAddressPage = apeOrderAddressService.page(page, queryWrapper);
        return Result.success(apeOrderAddressPage);
    }

    @GetMapping("getAddressList")
    public Result getAddressList() {
        ApeUser user = ShiroUtils.getUserInfo();
        QueryWrapper<ApeOrderAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ApeOrderAddress::getUserId,user.getId()).orderByAsc(ApeOrderAddress::getFirst);
        List<ApeOrderAddress> addressList = apeOrderAddressService.list(queryWrapper);
        return Result.success(addressList);
    }

    /** 根据id获取配送地址 */
    @Log(name = "根据id获取配送地址", type = BusinessType.OTHER)
    @GetMapping("getApeOrderAddressById")
    public Result getApeOrderAddressById(@RequestParam("id")String id) {
        ApeOrderAddress apeOrderAddress = apeOrderAddressService.getById(id);
        return Result.success(apeOrderAddress);
    }

    /** 保存配送地址 */
    @Log(name = "保存配送地址", type = BusinessType.INSERT)
    @PostMapping("saveApeOrderAddress")
    public Result saveApeOrderAddress(@RequestBody ApeOrderAddress apeOrderAddress) {
        ApeUser user = ShiroUtils.getUserInfo();
        apeOrderAddress.setUserId(user.getId());
        if (apeOrderAddress.getFirst() == 0) {
            UpdateWrapper<ApeOrderAddress> query = new UpdateWrapper<>();
            query.lambda().set(ApeOrderAddress::getFirst,1);
            apeOrderAddressService.update(query);
        }
        boolean save = apeOrderAddressService.save(apeOrderAddress);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑配送地址 */
    @Log(name = "编辑配送地址", type = BusinessType.UPDATE)
    @PostMapping("editApeOrderAddress")
    public Result editApeOrderAddress(@RequestBody ApeOrderAddress apeOrderAddress) {
        if (apeOrderAddress.getFirst() == 0) {
            UpdateWrapper<ApeOrderAddress> query = new UpdateWrapper<>();
            query.lambda().set(ApeOrderAddress::getFirst,1).ne(ApeOrderAddress::getId,apeOrderAddress.getId());
            apeOrderAddressService.update(query);
        }
        boolean save = apeOrderAddressService.updateById(apeOrderAddress);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除配送地址 */
    @GetMapping("removeApeOrderAddress")
    @Log(name = "删除配送地址", type = BusinessType.DELETE)
    public Result removeApeOrderAddress(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeOrderAddressService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("配送地址id不能为空！");
        }
    }

}