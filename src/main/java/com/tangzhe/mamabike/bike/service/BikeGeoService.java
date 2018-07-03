package com.tangzhe.mamabike.bike.service;

import com.mongodb.*;
import com.mongodb.util.JSON;
import com.tangzhe.mamabike.bike.entity.BikeLocation;
import com.tangzhe.mamabike.bike.entity.Point;
import com.tangzhe.mamabike.common.exception.MaMaBikeException;
import com.tangzhe.mamabike.record.entity.RideContrail;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tangzhe 2017/9/12.
 * 单车定位服务类 使用mogoDB
 */
@Component
public class BikeGeoService {

    private Logger log = LoggerFactory.getLogger(BikeGeoService.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 查找某经纬坐标点附近某范围内坐标点 由近到远
     *
     * db.getCollection('bike-position').find(
     *  {location:
     *      { $nearSphere:
     *          { $geometry:
     *              {
     *                  type: "Point",
     *                  coordinates: [120.78146, 31.680294]
     *              },
     *            $minDistance: 0,
     *            $maxDistance: 50
     *          }
     *      },
     *   status:1
     *  })
     */
    public List<BikeLocation> geoNearSphere(String collection, String locationField, Point center,
                                            long minDistance, long maxDistance, DBObject query,
                                            DBObject fields, int limit) throws MaMaBikeException {
        try {
            if(query == null){
                query = new BasicDBObject();
            }
            query.put(locationField,
                    new BasicDBObject("$nearSphere",
                            new BasicDBObject("$geometry",
                                    new BasicDBObject("type", "Point")
                                            .append("coordinates", new double[]{center.getLongitude(), center.getLatitude()}))
                                    .append("$minDistance", minDistance)
                                    .append("$maxDistance", maxDistance)
                    ));
            query.put("status",1);

            List<DBObject> objList =  mongoTemplate.getCollection(collection).find(query, fields).limit(limit).toArray();
            List<BikeLocation> result = new ArrayList<BikeLocation>();
            for(DBObject obj : objList){
                BikeLocation location = new BikeLocation();
                location.setBikeNumber(((Integer)obj.get("bike_no")).longValue());
                location.setStatus((Integer)obj.get("status"));
                BasicDBList coordinates = (BasicDBList)((BasicDBObject)obj.get("location")).get("coordinates");
                Double[] temp =new Double[2];
                coordinates.toArray(temp);
                location.setCoordinates(temp);
                result.add(location);
            }
            return result;
        } catch (Exception e) {
            log.error("fail to find around bike", e);
            throw new MaMaBikeException("查找附近单车失败");
        }
    }

    /**
     * 查找某经纬坐标点附近某范围内坐标点 由近到远 并且计算距离
     */
    public List<BikeLocation> geoNear(String collection, DBObject query, Point point, int limit, long maxDistance) throws MaMaBikeException {
        try {
            if(query==null){
                query = new BasicDBObject();
            }
            List<DBObject> pipeLine = new ArrayList<DBObject>();
            BasicDBObject aggregate = new BasicDBObject("$geoNear",
                    new BasicDBObject("near",new BasicDBObject("type","Point").append("coordinates",new double[]{point.getLongitude(), point.getLatitude()}))
                            .append("distanceField","distance")
                            .append("num", limit)
                            .append("maxDistance", maxDistance)
                            .append("spherical",true)
                            .append("query" , new BasicDBObject("status",1))
            );
            pipeLine.add(aggregate);
            Cursor cursor = mongoTemplate.getCollection(collection).aggregate(pipeLine, AggregationOptions.builder().build());
            List<BikeLocation> result = new ArrayList<BikeLocation>();
            while (cursor.hasNext()) {
                DBObject obj = cursor.next();
                BikeLocation location = new BikeLocation();
                location.setBikeNumber(((Integer)obj.get("bike_no")).longValue());
                BasicDBList coordinates = (BasicDBList)((BasicDBObject)obj.get("location")).get("coordinates");
                Double[] temp =new Double[2];
                coordinates.toArray(temp);
                location.setCoordinates(temp);
                location.setDistance((Double)obj.get("distance"));
                result.add(location);
            }

            return result;
        } catch (Exception e) {
            log.error("fail to find around bike",e);
            throw new MaMaBikeException("查找附近单车失败");
        }
    }

    /**
     * 测试
     * db.getCollection('bike-position').find({})
     */
    public void test() {
        DBObject queryObject = new BasicDBObject();
        queryObject.put("_id",new ObjectId("59b7ef3d2567c874c5178ef2"));
        DBObject fields = new BasicDBObject();
        fields.put("_id",false);
        fields.put("name",true);
        List<DBObject> list = mongoTemplate.getCollection("test").find(queryObject, fields).toArray();
        list.size();

        DBObject dbObject = (DBObject) JSON.parse("{'name':'mkyong', 'age':30}");
        String str=JSON.serialize(dbObject);
    }

    /**
     * 查询骑行轨迹
     */
    public RideContrail rideContrail(String collection, String recordNo) throws MaMaBikeException {
        try {
            DBObject obj = mongoTemplate.getCollection(collection).findOne(new BasicDBObject("record_no", recordNo));
            RideContrail rideContrail = new RideContrail();
            rideContrail.setRideRecordNo((String) obj.get("record_no"));
            rideContrail.setBikeNo(((Long) obj.get("bike_no")).longValue());
            BasicDBList locList = (BasicDBList) obj.get("contrail");
            List<Point> pointList = new ArrayList<>();
            for (Object object : locList) {
                BasicDBList locObj = (BasicDBList) ((BasicDBObject) object).get("loc");
                Double[] temp = new Double[2];
                locObj.toArray(temp);
                Point point = new Point(temp[0], temp[1]);
                pointList.add(point);
            }
            rideContrail.setContrail(pointList);
            return rideContrail;
        } catch (Exception e) {
            log.error("fail to query ride contrail", e);
            throw new MaMaBikeException("查询单车轨迹失败");
        }
    }

}
