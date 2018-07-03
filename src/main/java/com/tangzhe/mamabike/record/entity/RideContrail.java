package com.tangzhe.mamabike.record.entity;

import com.tangzhe.mamabike.bike.entity.Point;

import java.util.List;

/**
 * Created by JackWangon[www.coder520.com] 2017/8/23.
 * 骑行轨迹
 */
public class RideContrail {

    private String rideRecordNo;

    private Long bikeNo;

    private List<Point> contrail;

    public String getRideRecordNo() {
        return rideRecordNo;
    }

    public void setRideRecordNo(String rideRecordNo) {
        this.rideRecordNo = rideRecordNo;
    }

    public Long getBikeNo() {
        return bikeNo;
    }

    public void setBikeNo(Long bikeNo) {
        this.bikeNo = bikeNo;
    }

    public List<Point> getContrail() {
        return contrail;
    }

    public void setContrail(List<Point> contrail) {
        this.contrail = contrail;
    }

}
