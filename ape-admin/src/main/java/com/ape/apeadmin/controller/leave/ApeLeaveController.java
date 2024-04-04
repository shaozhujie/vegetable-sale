package com.ape.apeadmin.controller.leave;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeLeave;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeLeaveService;
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
 * @description: 留言咨询controller
 * @date 2024/03/28 02:54
 */
@Controller
@ResponseBody
@RequestMapping("leave")
public class ApeLeaveController {

    @Autowired
    private ApeLeaveService apeLeaveService;

    /** 分页获取留言咨询 */
    @Log(name = "分页获取留言咨询", type = BusinessType.OTHER)
    @PostMapping("getApeLeavePage")
    public Result getApeLeavePage(@RequestBody ApeLeave apeLeave) {
        Page<ApeLeave> page = new Page<>(apeLeave.getPageNumber(),apeLeave.getPageSize());
        QueryWrapper<ApeLeave> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeLeave.getContent()),ApeLeave::getContent,apeLeave.getContent())
                .like(StringUtils.isNotBlank(apeLeave.getReply()),ApeLeave::getReply,apeLeave.getReply())
                .eq(StringUtils.isNotBlank(apeLeave.getUserId()),ApeLeave::getUserId,apeLeave.getUserId())
                .like(StringUtils.isNotBlank(apeLeave.getCreateBy()),ApeLeave::getCreateBy,apeLeave.getCreateBy())
                .orderByDesc(ApeLeave::getCreateTime);
        Page<ApeLeave> apeLeavePage = apeLeaveService.page(page, queryWrapper);
        return Result.success(apeLeavePage);
    }

    /** 根据id获取留言咨询 */
    @Log(name = "根据id获取留言咨询", type = BusinessType.OTHER)
    @GetMapping("getApeLeaveById")
    public Result getApeLeaveById(@RequestParam("id")String id) {
        ApeLeave apeLeave = apeLeaveService.getById(id);
        return Result.success(apeLeave);
    }

    /** 保存留言咨询 */
    @Log(name = "保存留言咨询", type = BusinessType.INSERT)
    @PostMapping("saveApeLeave")
    public Result saveApeLeave(@RequestBody ApeLeave apeLeave) {
        ApeUser user = ShiroUtils.getUserInfo();
        apeLeave.setUserId(user.getId());
        apeLeave.setReply("未回复");
        boolean save = apeLeaveService.save(apeLeave);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑留言咨询 */
    @Log(name = "编辑留言咨询", type = BusinessType.UPDATE)
    @PostMapping("editApeLeave")
    public Result editApeLeave(@RequestBody ApeLeave apeLeave) {
        boolean save = apeLeaveService.updateById(apeLeave);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除留言咨询 */
    @GetMapping("removeApeLeave")
    @Log(name = "删除留言咨询", type = BusinessType.DELETE)
    public Result removeApeLeave(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeLeaveService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("留言咨询id不能为空！");
        }
    }

}