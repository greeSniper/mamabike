package com.tangzhe.mamabike.bike.dao;

import com.tangzhe.mamabike.bike.entity.Bike;
import com.tangzhe.mamabike.bike.entity.BikeExample;
import java.util.List;
import com.tangzhe.mamabike.bike.entity.BikeNoGen;
import org.apache.ibatis.annotations.Param;

public interface BikeMapper {
    int countByExample(BikeExample example);

    int deleteByExample(BikeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Bike record);

    int insertSelective(Bike record);

    List<Bike> selectByExample(BikeExample example);

    Bike selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Bike record, @Param("example") BikeExample example);

    int updateByExample(@Param("record") Bike record, @Param("example") BikeExample example);

    int updateByPrimaryKeySelective(Bike record);

    int updateByPrimaryKey(Bike record);

    void insertBikeNo(BikeNoGen bikeNoGen);

    Bike selectByBikeNo(Long bikeNo);
}