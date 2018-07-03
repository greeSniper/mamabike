package com.tangzhe.mamabike.bike.service;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.tangzhe.mamabike.bike.dao.BikeMapper;
import com.tangzhe.mamabike.bike.entity.Bike;
import com.tangzhe.mamabike.bike.entity.BikeLocation;
import com.tangzhe.mamabike.bike.entity.BikeNoGen;
import com.tangzhe.mamabike.common.exception.MaMaBikeException;
import com.tangzhe.mamabike.common.utils.BaiduPushUtil;
import com.tangzhe.mamabike.common.utils.DateUtil;
import com.tangzhe.mamabike.common.utils.RandomNumberCode;
import com.tangzhe.mamabike.fee.dao.RideFeeMapper;
import com.tangzhe.mamabike.fee.entity.RideFee;
import com.tangzhe.mamabike.record.dao.RideRecordMapper;
import com.tangzhe.mamabike.record.entity.RideRecord;
import com.tangzhe.mamabike.user.dao.UserMapper;
import com.tangzhe.mamabike.user.entity.User;
import com.tangzhe.mamabike.user.entity.UserElement;
import com.tangzhe.mamabike.wallet.dao.WalletMapper;
import com.tangzhe.mamabike.wallet.entity.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * bike业务层
 */
@Service("bikeServiceImpl")
public class BikeServiceImpl implements BikeService {

    private Logger log = LoggerFactory.getLogger(BikeServiceImpl.class);

    private static final Byte NOT_VERYFY = 1; //未认证
    private static final Object BIKE_UNLOCK = 2; //单车解锁
    private static final Object BIKE_LOCK = 1; //单车锁定
    private static final Byte RIDE_END = 2; //骑行结束

    @Autowired
    private BikeMapper bikeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RideRecordMapper rideRecordMapper;
    @Autowired
    private WalletMapper walletMapper;
    @Autowired
    private RideFeeMapper feeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 生成单车
     */
    @Transactional
    public void generateBike() throws MaMaBikeException {
        //插入单车编号对象，返回单车编号id，即是单车的编号
        BikeNoGen bikeNoGen = new BikeNoGen();
        bikeNoGen.setWhatEver((byte)1);
        bikeMapper.insertBikeNo(bikeNoGen);

        //插入单车对象
        Bike bike = new Bike();
        bike.setNumber(bikeNoGen.getAutoIncNo());
        bike.setType((byte)2);
        bikeMapper.insertSelective(bike);
    }

    /**
     * 解锁单车，准备骑行
     */
    @Transactional
    public void unLockBike(UserElement currentUser, Long number) throws MaMaBikeException {
        try {
            //检查用户是否已经认证（实名认证没  押金交了没 ）
            User user = userMapper.selectByPrimaryKey(currentUser.getUserId());
            if (user.getVerifyFlag() == NOT_VERYFY) {
                throw new MaMaBikeException("用户尚未认证");
            }

            //检查用户有没有未关闭的骑行记录
            RideRecord record = rideRecordMapper.selectRecordNotClosed(currentUser.getUserId());
            if (record != null) {
                throw new MaMaBikeException("存在未关闭骑行订单");
            }

            //检查用户钱包余额是否足够（大于一元）
            Wallet wallet = walletMapper.selectByUserId(currentUser.getUserId());
            if (wallet.getRemainSum().compareTo(new BigDecimal(1)) < 0) {
                throw new MaMaBikeException("余额不足");
            }

            //推送单车进行解锁
//            JSONObject notification = new JSONObject();
//            notification.put("unlock", "unlock");
//            BaiduPushUtil.pushMsgToSingleDevice(currentUser,"{\"title\":\"TEST\",\"description\":\"Hello Baidu push!\"}");
            //推送如果可靠性比较差 可以采用单车端开锁后 主动ACK服务器 再修改相关状态的方式

            //修改mongoDB中单车状态
            Query query = Query.query(Criteria.where("bike_no").is(number));
            Update update = Update.update("status", BIKE_UNLOCK);
            mongoTemplate.updateFirst(query, update, BikeLocation.class);

            //建立订单  记录开始骑行时间  同时骑行轨迹开始上报(另一个接口)
            RideRecord rideRecord = new RideRecord();
            rideRecord.setBikeNo(number);
            String recordNo = new Date().toString() + System.currentTimeMillis() + RandomNumberCode.randomNo();
            rideRecord.setRecordNo(recordNo);
            rideRecord.setStartTime(new Date());
            rideRecord.setUserid(currentUser.getUserId());
            rideRecordMapper.insertSelective(rideRecord);

        } catch (Exception e) {
            log.error("fail to lock bike", e);
            throw new MaMaBikeException("锁定单车失败");
        }
    }

    /**
     * 锁车，骑行结束
     */
    @Transactional
    public void lockBike(BikeLocation bikeLocation) throws MaMaBikeException {
        try {
            //结束订单 计算骑行时间存订单
            //查询未完成的订单
            RideRecord record = rideRecordMapper.selectBikeRecordOnGoing(bikeLocation.getBikeNumber());
            if(record==null){
                throw new MaMaBikeException("骑行记录不存在");
            }
            Long userid = record.getUserid();

            //查询单车类型 查询计价信息
            Bike bike = bikeMapper.selectByBikeNo(bikeLocation.getBikeNumber());
            if(bike==null){
                throw new MaMaBikeException("单车不存在");
            }
            RideFee fee = feeMapper.selectBikeTypeFee(bike.getType());
            if(fee==null){
                throw new MaMaBikeException("计费信息异常");
            }
            BigDecimal cost = BigDecimal.ZERO;
            record.setEndTime(new Date());
            record.setStatus(RIDE_END);
            Long min = DateUtil.getBetweenMin(new Date(),record.getStartTime());
            record.setRideTime(min.intValue());
            int minUnit =fee.getMinUnit();
            int intMin = min.intValue();
            if(intMin/minUnit==0){
                //不足一个时间单位 按照一个时间单位算
                cost = fee.getFee();
            }else if(intMin%minUnit==0){
                //整除了时间单位 直接计费
                cost = fee.getFee().multiply(new BigDecimal(intMin/minUnit));
            }else if(intMin%minUnit!=0){
                //不整除 +1 补足一个时间单位
                cost = fee.getFee().multiply(new BigDecimal((intMin/minUnit)+1));
            }
            record.setRideCost(cost);
            rideRecordMapper.updateByPrimaryKeySelective(record);

            //钱包扣费
            Wallet wallet = walletMapper.selectByUserId(userid);
            wallet.setRemainSum(wallet.getRemainSum().subtract(cost));
            walletMapper.updateByPrimaryKeySelective(wallet);

            //修改mongoDB中单车状态为锁定
            Query query = Query.query(Criteria.where("bike_no").is(bikeLocation.getBikeNumber()));
            Update update = Update.update("status", BIKE_LOCK)
                    .set("location.coordinates", bikeLocation.getCoordinates());
            mongoTemplate.updateFirst(query, update, "bike-position");

        } catch (Exception e) {
            log.error("fail to lock bike", e);
            throw new MaMaBikeException("锁定单车失败");
        }

    }

    /**
     * 单车上报坐标
     */
    public void reportLocation(BikeLocation bikeLocation) throws MaMaBikeException {
        try {
            //数据库中查询该单车尚未完结的订单
            RideRecord record = rideRecordMapper.selectBikeRecordOnGoing(bikeLocation.getBikeNumber());
            if(record==null){
                throw new MaMaBikeException("骑行记录不存在");
            }

            //查询mongo中是否已经有骑行的坐标记录数据
            DBObject obj = mongoTemplate.getCollection("ride_contrail")
                    .findOne(new BasicDBObject("record_no",record.getRecordNo()));

            //判断mongo中是否已经有骑行的坐标记录数据
            if(obj==null){
                //没有则插入
                List<BasicDBObject> list = new ArrayList();
                BasicDBObject temp = new BasicDBObject("loc",bikeLocation.getCoordinates());
                list.add(temp);
                BasicDBObject insertObj = new BasicDBObject("record_no",record.getRecordNo())
                        .append("bike_no",record.getBikeNo())
                        .append("contrail",list);
                mongoTemplate.insert(insertObj,"ride_contrail");
            }else {
                //已经存在 添加坐标
                Query query = new Query( Criteria.where("record_no").is(record.getRecordNo()));
                Update update = new Update().push("contrail", new BasicDBObject("loc",bikeLocation.getCoordinates()));
                mongoTemplate.updateFirst(query,update,"ride_contrail");
            }

        } catch (Exception e) {
            log.error("fail to report location", e);
            throw new MaMaBikeException("单车上报坐标失败");
        }
    }

}














