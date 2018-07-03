package com.tangzhe.mamabike.record.service;

import com.tangzhe.mamabike.common.exception.MaMaBikeException;
import com.tangzhe.mamabike.record.dao.RideRecordMapper;
import com.tangzhe.mamabike.record.entity.RideRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 骑行记录业务层
 */
@Service("rideRecordServiceImpl")
public class RideRecordServiceImpl implements RideRecordService {

    @Autowired
    private RideRecordMapper rideRecordMapper;

    /**
     * 查询骑行历史
     */
    public List<RideRecord> listRideRecord(Long userId, Long lastId) throws MaMaBikeException {
        List<RideRecord> list = rideRecordMapper.selectRideRecordPage(userId,lastId);
        return list;
    }
}
