package com.ape.apesystem.service.impl;

import com.ape.apesystem.domain.ApeOrderAddress;
import com.ape.apesystem.mapper.ApeOrderAddressMapper;
import com.ape.apesystem.service.ApeOrderAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 配送地址service实现类
 * @date 2024/03/28 10:11
 */
@Service
public class ApeOrderAddressServiceImpl extends ServiceImpl<ApeOrderAddressMapper, ApeOrderAddress> implements ApeOrderAddressService {
}