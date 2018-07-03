package com.tangzhe.mamabike.bike.service;

import com.tangzhe.mamabike.bike.entity.BikeLocation;
import com.tangzhe.mamabike.common.exception.MaMaBikeException;
import com.tangzhe.mamabike.user.entity.UserElement;

public interface BikeService {

    void generateBike() throws MaMaBikeException;

    void unLockBike(UserElement currentUser, Long number) throws MaMaBikeException;

    void lockBike(BikeLocation bikeLocation) throws MaMaBikeException;

    void reportLocation(BikeLocation bikeLocation) throws MaMaBikeException;
}
