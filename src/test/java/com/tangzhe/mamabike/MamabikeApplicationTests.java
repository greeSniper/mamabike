package com.tangzhe.mamabike;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.tangzhe.mamabike.bike.entity.BikeLocation;
import com.tangzhe.mamabike.bike.entity.Point;
import com.tangzhe.mamabike.bike.service.BikeGeoService;
import com.tangzhe.mamabike.bike.service.BikeService;
import com.tangzhe.mamabike.common.exception.MaMaBikeException;
import com.tangzhe.mamabike.user.entity.UserElement;
import com.tangzhe.mamabike.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MamabikeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MamabikeApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;
	@LocalServerPort
	private int port;
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;
	@Autowired
	private BikeGeoService geoService;
	@Autowired
	@Qualifier("bikeServiceImpl")
	private BikeService bikeService;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	public void contextLoads() {
		String result = restTemplate.getForObject("/user/hello", String.class);
		System.out.println(result);
	}

//	@Test
//	public void test() {
//		Logger logger = LoggerFactory.getLogger(MamabikeApplicationTests.class);
//		try {
//			userService.login();
//		} catch (Exception e) {
//			logger.error("出错了", e);
//		}
//	}

	@Test
	public void testMongoDBGeoHash() throws MaMaBikeException {
		geoService.geoNearSphere("bike-position","location", new Point(120.78146, 31.680294),0,500,null,null,10);

		//geoService.geoNear("bike-position",null, new Point(120.78146, 31.680294),10,500);

		//geoService.test();

//		try{
//			// 连接到 mongodb 服务
//			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
//
//			// 连接到数据库
//			MongoDatabase mongoDatabase = mongoClient.getDatabase("mamabike");
//			System.out.println("Connect to database successfully");
//
//			MongoCollection collection = mongoDatabase.getCollection("test");
//			FindIterable findIterable = collection.find();
//			MongoCursor mongoCursor = findIterable.iterator();
//			while(mongoCursor.hasNext()){
//				System.out.println(mongoCursor.next());
//			}
//
//		}catch(Exception e){
//			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//		}
	}

	/**
	 * 测试DB的名称，看看是不是mamabike，默认是test
	 * 配置文件中没有配置好
	 *   #springdata
		 data:
		 #mongoDB    #mongodb note:mongo3.x will not use host and port,only use uri
		 mongodb:
		 uri: mongodb://localhost:27017/mamabike
	 * 所以使用mongoTemplate操作不了MongoDB
	 */
	@Test
	public void testMongoDB() throws MaMaBikeException {
		String dbName = mongoTemplate.getDb().getName();
		System.out.println(dbName);
	}

	/**
	 * 测试解锁单车
	 */
	@Test
	public void testUnLockBike() throws MaMaBikeException {
		UserElement ue = new UserElement();
		ue.setUserId(1L);
		//ue.setPushChannelId("12345");
		ue.setPlatform("android");
		bikeService.unLockBike(ue, 28000001L);
	}

	/**
	 * 测试锁定单车
	 */
	@Test
	public void testLockBike() throws MaMaBikeException {
		BikeLocation location = new BikeLocation();
		location.setBikeNumber(28000001L);
		//Double[] bikePosition = new Double[]{120.78156, 31.680204};
		Double[] bikePosition = new Double[]{120.78156, 32.680204};
		location.setCoordinates(bikePosition);
		bikeService.lockBike(location);
	}

	/**
	 * 测试单车上报坐标
	 */
	@Test
	public void testReportLocation() throws MaMaBikeException {
		BikeLocation bikeLocation = new BikeLocation();
		bikeLocation.setBikeNumber(28000001L);
		Double[] bikePosition = new Double[]{120.78156, 32.680204};
		bikeLocation.setCoordinates(bikePosition);
		bikeService.reportLocation(bikeLocation);
	}

	/**
	 * 测试查询骑行轨迹
	 */
	@Test
	public void rideContrail() throws MaMaBikeException {
		//geoService.rideContrail("ride_contrail", "1503396355465162725396");
		geoService.rideContrail("ride_contrail", "Wed Sep 13 17:56:13 CST 201715052965737053127176");
	}

}
















