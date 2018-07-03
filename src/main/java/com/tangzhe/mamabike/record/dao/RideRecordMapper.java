package com.tangzhe.mamabike.record.dao;

import com.tangzhe.mamabike.record.entity.RideRecord;

import java.util.List;

public interface RideRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RideRecord record);

    int insertSelective(RideRecord record);

    RideRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RideRecord record);

    int updateByPrimaryKey(RideRecord record);

    RideRecord selectRecordNotClosed(Long userId);

    RideRecord selectBikeRecordOnGoing(Long bikeNo);

    List<RideRecord> selectRideRecordPage(Long userId, Long lastId);
}