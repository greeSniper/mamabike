package com.tangzhe.mamabike.fee.dao;

import com.tangzhe.mamabike.fee.entity.RideFee;

public interface RideFeeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RideFee record);

    int insertSelective(RideFee record);

    RideFee selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RideFee record);

    int updateByPrimaryKey(RideFee record);

    RideFee selectBikeTypeFee(Byte type);
}