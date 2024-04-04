package com.ape.apeadmin.controller.common;

import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.domain.Result;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.domain.ApeVegetableOrder;
import com.ape.apesystem.service.ApeUserService;
import com.ape.apesystem.service.ApeVegetableOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: TODO
 * @date 2023/8/28 11:35
 */
@Controller
@ResponseBody
@RequestMapping("common")
public class CommonController {

    @Autowired
    private ApeVegetableOrderService apeVegetableOrderService;
    @Autowired
    ApeUserService apeUserService;

    /**
    * @description: 错误转发地址
    * @param: code
    	msg
    * @return:
    * @author shaozhujie
    * @date: 2023/9/14 15:05
    */
    @GetMapping("/error/{code}/{msg}")
    public Result error (@PathVariable("code")Integer code, @PathVariable("msg") String msg){
        return Result.alert(code,msg);
    }

    /**
     * @description: 上传图片
     * @param: file
     * @return:
     * @author shaozhujie
     * @date: 2023/10/13 10:44
     */
    @PostMapping("uploadImg")
    public Result uploadImg(@RequestParam("file") MultipartFile img) {
        if(img.isEmpty()){
            return Result.fail("上传的图片不能为空!");
        }
        String coverType = img.getOriginalFilename().substring(img.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        if ("jpeg".equals(coverType)  || "gif".equals(coverType) || "png".equals(coverType) || "bmp".equals(coverType)  || "jpg".equals(coverType)) {
            //文件名=当前时间到毫秒+原来的文件名
            int index = img.getOriginalFilename().lastIndexOf(".");
            String substring = img.getOriginalFilename().substring(index);
            String fileName = System.currentTimeMillis() + substring;
            //文件路径
            String filePath = System.getProperty("user.dir")+System.getProperty("file.separator")+"img";
            //如果文件路径不存在，新增该路径
            File file1 = new File(filePath);
            if(!file1.exists()){
                boolean mkdir = file1.mkdir();
            }
            //实际的文件地址
            File dest = new File(filePath + System.getProperty("file.separator") + fileName);
            //存储到数据库里的相对文件地址
            String storeImgPath = "/img/"+fileName;
            try {
                img.transferTo(dest);
                return Result.success(storeImgPath);
            } catch (IOException e) {
                return Result.fail("上传失败");
            }
        } else {
            return Result.fail("请选择正确的图片格式");
        }
    }

    /**
     * @description: 上传视频
     * @param: file
     * @return:
     * @author shaozhujie
     * @date: 2023/10/13 10:44
     */
    @PostMapping("uploadVideo")
    public Result uploadVideo(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()){
            return Result.fail("上传的视频不能为空!");
        }
        //文件名=当前时间到毫秒+原来的文件名
        int index = file.getOriginalFilename().lastIndexOf(".");
        String substring = file.getOriginalFilename().substring(index);
        String fileName = System.currentTimeMillis() + substring;
        //文件路径
        String filePath = System.getProperty("user.dir")+System.getProperty("file.separator")+"video";
        //如果文件路径不存在，新增该路径
        File file1 = new File(filePath);
        if(!file1.exists()){
            boolean mkdir = file1.mkdir();
        }
        //实际的文件地址
        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        //存储到数据库里的相对文件地址
        String storeVideoPath = "/video/"+fileName;
        try {
            file.transferTo(dest);
            Result success = Result.success(storeVideoPath);
            success.setData(fileName);
            return success;
        } catch (IOException e) {
            return Result.fail("上传失败");
        }
    }

    /**
    * @description: 上传文件
    * @param: file
    * @return:
    * @author shaozhujie
    * @date: 2023/10/13 10:44
    */
    @PostMapping("uploadFile")
    public Result uploadFile(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()){
            return Result.fail("上传的文件不能为空!");
        }
        //文件名=当前时间到毫秒+原来的文件名
        int index = file.getOriginalFilename().lastIndexOf(".");
        String substring = file.getOriginalFilename().substring(index);
        String fileName = System.currentTimeMillis() + substring;
        //文件路径
        String filePath = System.getProperty("user.dir")+System.getProperty("file.separator")+"file";
        //如果文件路径不存在，新增该路径
        File file1 = new File(filePath);
        if(!file1.exists()){
            boolean mkdir = file1.mkdir();
        }
        //实际的文件地址
        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        //存储到数据库里的相对文件地址
        String storeFilePath = "/file/"+fileName;
        try {
            file.transferTo(dest);
            Result success = Result.success(storeFilePath);
            success.setData(fileName);
            return success;
        } catch (IOException e) {
            return Result.fail("上传失败");
        }
    }

    @GetMapping("getIndexManage")
    public Result getIndexManage() {
        JSONObject json = new JSONObject();
        //获取用户数量
        QueryWrapper<ApeUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeUser::getUserType,1);
        int count = apeUserService.count(queryWrapper);
        json.put("user",count);
        //获取订单数量
        int count1 = apeVegetableOrderService.count();
        json.put("order",count1);
        //获取销售额
        QueryWrapper<ApeVegetableOrder> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.select("sum(price) as totalPrice").lambda().ne(ApeVegetableOrder::getState,3);
        Map<String,Object> list = apeVegetableOrderService.getMap(queryWrapper1);
        json.put("price",list.get("totalPrice"));

        //获取最近七日数据
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dates = new ArrayList<>();
        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Date date = calendar.getTime();
            String formattedDate = dateFormat.format(date);
            QueryWrapper<ApeVegetableOrder> wrapper = new QueryWrapper<>();
            wrapper.lambda().ge(ApeVegetableOrder::getCreateTime,formattedDate + " 00:00:00")
                    .le(ApeVegetableOrder::getCreateTime,formattedDate + " 23:59:59");
            int count2 = apeVegetableOrderService.count(wrapper);
            nums.add(count2);
            dates.add(formattedDate);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        json.put("dates",dates);
        json.put("nums",nums);
        QueryWrapper<ApeVegetableOrder> wrapper1 = new QueryWrapper<>();
        wrapper1.select("count(*) as count,name,sum(price) as price").lambda().ne(ApeVegetableOrder::getState,3).groupBy(ApeVegetableOrder::getVegetableId);
        List<Map<String, Object>> listMaps = apeVegetableOrderService.listMaps(wrapper1);
        List<Object> names = new ArrayList<>();
        List<Object> countList = new ArrayList<>();
        List<Object> price = new ArrayList<>();
        for (Map<String, Object> map : listMaps) {
            names.add(map.get("name"));
            countList.add(map.get("count"));
            price.add(map.get("price"));
        }
        json.put("names",names);
        json.put("countList",countList);
        json.put("priceTotal",price);
        return Result.success(json);
    }

}
