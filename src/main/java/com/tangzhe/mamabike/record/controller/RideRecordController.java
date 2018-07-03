package com.tangzhe.mamabike.record.controller;

import com.tangzhe.mamabike.bike.service.BikeGeoService;
import com.tangzhe.mamabike.common.constants.Constants;
import com.tangzhe.mamabike.common.exception.MaMaBikeException;
import com.tangzhe.mamabike.common.resp.ApiResult;
import com.tangzhe.mamabike.common.rest.BaseController;
import com.tangzhe.mamabike.record.entity.RideContrail;
import com.tangzhe.mamabike.record.entity.RideRecord;
import com.tangzhe.mamabike.record.service.RideRecordService;
import com.tangzhe.mamabike.user.entity.UserElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 骑行记录控制层
 */
@RestController
@RequestMapping("rideRecord")
public class RideRecordController extends BaseController {

    private Logger log = LoggerFactory.getLogger(RideRecordController.class);

    @Autowired
    @Qualifier("rideRecordServiceImpl")
    private RideRecordService rideRecordService;
    @Autowired
    private BikeGeoService bikeGeoService;

    /**
     * 查询骑行历史
     */
    @RequestMapping("/list/{id}")
    public ApiResult<List<RideRecord>> listRideRecord(@PathVariable("id") Long lastId){

        ApiResult<List<RideRecord>> resp = new ApiResult<>();
        try {
            UserElement ue = getCurrentUser();
            List<RideRecord> list = rideRecordService.listRideRecord(ue.getUserId(),lastId);
            resp.setData(list);
            resp.setMessage("查询成功");
        } catch (MaMaBikeException e) {
            resp.setCode(e.getStatusCode());
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to query ride record ", e);
            resp.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误");
        }

        return resp;
    }

    /**
     * 查询骑行轨迹
     */
    @RequestMapping("/contrail/{recordNo}")
    public ApiResult<RideContrail> rideContrail(@PathVariable("recordNo") String recordNo){

        ApiResult<RideContrail> resp = new ApiResult<>();
        try {
            UserElement ue = getCurrentUser();
            RideContrail contrail = bikeGeoService.rideContrail("ride_contrail", recordNo);
            resp.setData(contrail);
            resp.setMessage("查询成功");
        } catch (MaMaBikeException e) {
            resp.setCode(e.getStatusCode());
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to query ride record ", e);
            resp.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误");
        }

        return resp;
    }

}
