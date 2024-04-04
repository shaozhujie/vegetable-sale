package com.ape.apeadmin.controller.shed;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
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
 * @description: 菜棚controller
 * @date 2024/03/28 02:27
 */
@Controller
@ResponseBody
@RequestMapping("shed")
public class ApeShedController {

    @Autowired
    private ApeShedService apeShedService;
    @Autowired
    private ApeShedAppointmentService apeShedAppointmentService;

    /** 分页获取菜棚 */
    @Log(name = "分页获取菜棚", type = BusinessType.OTHER)
    @PostMapping("getApeShedPage")
    public Result getApeShedPage(@RequestBody ApeShed apeShed) {
        Page<ApeShed> page = new Page<>(apeShed.getPageNumber(),apeShed.getPageSize());
        QueryWrapper<ApeShed> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(apeShed.getState() != null,ApeShed::getState,apeShed.getState())
                .eq(StringUtils.isNotBlank(apeShed.getName()),ApeShed::getName,apeShed.getName())
                .eq(StringUtils.isNotBlank(apeShed.getIntroduce()),ApeShed::getIntroduce,apeShed.getIntroduce())
                .eq(StringUtils.isNotBlank(apeShed.getAddress()),ApeShed::getAddress,apeShed.getAddress());
        Page<ApeShed> apeShedPage = apeShedService.page(page, queryWrapper);
        return Result.success(apeShedPage);
    }

    /** 根据id获取菜棚 */
    @Log(name = "根据id获取菜棚", type = BusinessType.OTHER)
    @GetMapping("getApeShedById")
    public Result getApeShedById(@RequestParam("id")String id) {
        ApeShed apeShed = apeShedService.getById(id);
        return Result.success(apeShed);
    }

    /** 保存菜棚 */
    @Log(name = "保存菜棚", type = BusinessType.INSERT)
    @PostMapping("saveApeShed")
    public Result saveApeShed(@RequestBody ApeShed apeShed) {
        boolean save = apeShedService.save(apeShed);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑菜棚 */
    @Log(name = "编辑菜棚", type = BusinessType.UPDATE)
    @PostMapping("editApeShed")
    public Result editApeShed(@RequestBody ApeShed apeShed) {
        boolean save = apeShedService.updateById(apeShed);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除菜棚 */
    @Transactional(rollbackFor = Exception.class)
    @GetMapping("removeApeShed")
    @Log(name = "删除菜棚", type = BusinessType.DELETE)
    public Result removeApeShed(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeShedService.removeById(id);
                QueryWrapper<ApeShedAppointment> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ApeShedAppointment::getShedId,id);
                apeShedAppointmentService.remove(queryWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("菜棚id不能为空！");
        }
    }

}