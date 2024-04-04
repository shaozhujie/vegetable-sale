package com.ape.apeadmin.controller.appointment;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeShed;
import com.ape.apesystem.domain.ApeShedAppointment;
import com.ape.apesystem.service.ApeShedAppointmentService;
import com.ape.apesystem.service.ApeShedService;
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
 * @description: 菜棚预约controller
 * @date 2024/03/28 08:33
 */
@Controller
@ResponseBody
@RequestMapping("appointment")
public class ApeShedAppointmentController {

    @Autowired
    private ApeShedAppointmentService apeShedAppointmentService;
    @Autowired
    private ApeShedService apeShedService;

    /** 分页获取菜棚预约 */
    @Log(name = "分页获取菜棚预约", type = BusinessType.OTHER)
    @PostMapping("getApeShedAppointmentPage")
    public Result getApeShedAppointmentPage(@RequestBody ApeShedAppointment apeShedAppointment) {
        Page<ApeShedAppointment> page = new Page<>(apeShedAppointment.getPageNumber(),apeShedAppointment.getPageSize());
        QueryWrapper<ApeShedAppointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeShedAppointment.getName()),ApeShedAppointment::getName,apeShedAppointment.getName())
                .eq(StringUtils.isNotBlank(apeShedAppointment.getUserId()),ApeShedAppointment::getUserId,apeShedAppointment.getUserId())
                .eq(apeShedAppointment.getState() != null,ApeShedAppointment::getState,apeShedAppointment.getState())
                .like(StringUtils.isNotBlank(apeShedAppointment.getCreateBy()),ApeShedAppointment::getCreateBy,apeShedAppointment.getCreateBy());
        Page<ApeShedAppointment> apeShedAppointmentPage = apeShedAppointmentService.page(page, queryWrapper);
        return Result.success(apeShedAppointmentPage);
    }

    /** 根据id获取菜棚预约 */
    @Log(name = "根据id获取菜棚预约", type = BusinessType.OTHER)
    @GetMapping("getApeShedAppointmentById")
    public Result getApeShedAppointmentById(@RequestParam("id")String id) {
        ApeShedAppointment apeShedAppointment = apeShedAppointmentService.getById(id);
        return Result.success(apeShedAppointment);
    }

    /** 保存菜棚预约 */
    @Log(name = "保存菜棚预约", type = BusinessType.INSERT)
    @PostMapping("saveApeShedAppointment")
    public Result saveApeShedAppointment(@RequestBody ApeShedAppointment apeShedAppointment) {
        ApeShed shed = apeShedService.getById(apeShedAppointment.getShedId());
        apeShedAppointment.setImages(shed.getImages());
        apeShedAppointment.setName(shed.getName());
        apeShedAppointment.setIntroduce(shed.getIntroduce());
        apeShedAppointment.setUserId(ShiroUtils.getUserInfo().getId());
        boolean save = apeShedAppointmentService.save(apeShedAppointment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑菜棚预约 */
    @Log(name = "编辑菜棚预约", type = BusinessType.UPDATE)
    @PostMapping("editApeShedAppointment")
    public Result editApeShedAppointment(@RequestBody ApeShedAppointment apeShedAppointment) {
        boolean save = apeShedAppointmentService.updateById(apeShedAppointment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除菜棚预约 */
    @GetMapping("removeApeShedAppointment")
    @Log(name = "删除菜棚预约", type = BusinessType.DELETE)
    public Result removeApeShedAppointment(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeShedAppointmentService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("菜棚预约id不能为空！");
        }
    }

}