package com.tangzhe.mamabike.bike.controller;

import com.tangzhe.mamabike.bike.entity.Bike;
import com.tangzhe.mamabike.bike.entity.BikeLocation;
import com.tangzhe.mamabike.bike.entity.Point;
import com.tangzhe.mamabike.bike.service.BikeGeoService;
import com.tangzhe.mamabike.bike.service.BikeService;
import com.tangzhe.mamabike.common.constants.Constants;
import com.tangzhe.mamabike.common.exception.MaMaBikeException;
import com.tangzhe.mamabike.common.resp.ApiResult;
import com.tangzhe.mamabike.common.rest.BaseController;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * bike控制层
 */
@RestController
@RequestMapping("bike")
public class BikeController extends BaseController {

    private Logger log = LoggerFactory.getLogger(BikeController.class);

    @Autowired
    @Qualifier("bikeServiceImpl")
    private BikeService bikeService;
    @Autowired
    private BikeGeoService bikeGeoService;

    /**
     * 生成单车
     */
    @RequestMapping("/generateBike")
    public ApiResult generateBike() {
        ApiResult apiResult = new ApiResult();
        try {
            bikeService.generateBike();
        } catch (MaMaBikeException e) {
            apiResult.setCode(e.getStatusCode());
            apiResult.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("error to generate bike", e);
            apiResult.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            apiResult.setMessage("内部错误");
        }
        return apiResult;
    }

    /**
     * 查找某坐标附近单车
     */
    @ApiOperation(value="查找附近单车",notes = "根据用户APP定位坐标来查找附近单车",httpMethod = "POST")
    @ApiImplicitParam(name = "point",value = "用户定位坐标",required = true,dataType = "Point")
    @RequestMapping("/findAroundBike")
    public ApiResult findAroundBike(@RequestBody Point point ){

        ApiResult<List<BikeLocation>> resp = new ApiResult<>();
        try {
            List<BikeLocation> bikeList = bikeGeoService.geoNear("bike-position",null,point,10,50);
            resp.setMessage("查询附近单车成功");
            resp.setData(bikeList);
        } catch (MaMaBikeException e) {
            resp.setCode(e.getStatusCode());
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to find around bike info", e);
            resp.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误");
        }

        return resp;
    }

    /**
     * 解锁单车，准备骑行
     */
    @RequestMapping("/unLockBike")
    public ApiResult unLockBike(@RequestBody Bike bike) {
        ApiResult apiResult = new ApiResult();

        try {
            bikeService.unLockBike(getCurrentUser(), bike.getNumber());
            apiResult.setMessage("等待单车解锁");
        } catch (MaMaBikeException e) {
            apiResult.setCode(e.getStatusCode());
            apiResult.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to unlock bike", e);
            apiResult.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            apiResult.setMessage("内部错误");
        }

        return apiResult;
    }

    /**
     * 锁车，骑行结束
     */
    @RequestMapping("/lockBike")
    public ApiResult lockBike(@RequestBody BikeLocation bikeLocation){

        ApiResult resp = new ApiResult();
        try {
            bikeService.lockBike(bikeLocation);
            resp.setMessage("锁车成功");
        } catch (MaMaBikeException e) {
            resp.setCode(e.getStatusCode());
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to lock bike", e);
            resp.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误");
        }
        return resp;
    }

    /**
     * 单车上报坐标
     */
    @RequestMapping("/reportLocation")
    public ApiResult reportLocation(@RequestBody BikeLocation bikeLocation){

        ApiResult<List<BikeLocation>> resp = new ApiResult<>();
        try {
            bikeService.reportLocation(bikeLocation);
            resp.setMessage("上报坐标成功");
        } catch (MaMaBikeException e) {
            resp.setCode(e.getStatusCode());
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to report location", e);
            resp.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误");
        }

        return resp;
    }

}
