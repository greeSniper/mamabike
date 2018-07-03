package com.tangzhe.mamabike.record.entity;

import java.math.BigDecimal;
import java.util.Date;

public class RideRecord {
    private Long id;

    private Long userid;

    private String recordNo;

    private Long bikeNo;

    private Date startTime;

    private Date endTime;

    private Integer rideTime;

    private BigDecimal rideCost;

    private Byte status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(String recordNo) {
        this.recordNo = recordNo == null ? null : recordNo.trim();
    }

    public Long getBikeNo() {
        return bikeNo;
    }

    public void setBikeNo(Long bikeNo) {
        this.bikeNo = bikeNo;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getRideTime() {
        return rideTime;
    }

    public void setRideTime(Integer rideTime) {
        this.rideTime = rideTime;
    }

    public BigDecimal getRideCost() {
        return rideCost;
    }

    public void setRideCost(BigDecimal rideCost) {
        this.rideCost = rideCost;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}