package com.tangzhe.mamabike.record.service;

import com.tangzhe.mamabike.common.exception.MaMaBikeException;
import com.tangzhe.mamabike.record.entity.RideRecord;

import java.util.List;

public interface RideRecordService {
    List<RideRecord> listRideRecord(Long userId, Long lastId) throws MaMaBikeException;
}
