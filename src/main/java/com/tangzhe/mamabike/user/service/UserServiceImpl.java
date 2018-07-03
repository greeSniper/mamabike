package com.tangzhe.mamabike.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tangzhe.mamabike.cache.CommonCacheUtil;
import com.tangzhe.mamabike.common.constants.Constants;
import com.tangzhe.mamabike.common.exception.MaMaBikeException;
import com.tangzhe.mamabike.common.utils.QiniuFileUploadUtil;
import com.tangzhe.mamabike.common.utils.RandomNumberCode;
import com.tangzhe.mamabike.jms.SmsProcessor;
import com.tangzhe.mamabike.security.AESUtil;
import com.tangzhe.mamabike.security.Base64Util;
import com.tangzhe.mamabike.security.MD5Util;
import com.tangzhe.mamabike.security.RSAUtil;
import com.tangzhe.mamabike.user.controller.UserController;
import com.tangzhe.mamabike.user.dao.UserMapper;
import com.tangzhe.mamabike.user.entity.User;
import com.tangzhe.mamabike.user.entity.UserElement;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.jms.Destination;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户业务层
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    private Logger log = LoggerFactory.getLogger(UserController.class);

    //存在redis中验证次数的key
    private static final String VERIFYCODE_PREFIX = "verify.code.";

    //发送短信验证码消息队列名称
    private static final String SMS_QUEUE ="sms.queue" ;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommonCacheUtil commonCacheUtil;
    @Autowired
    private SmsProcessor smsProcessor;

    public User findById(long id) {
        User user = userMapper.selectByPrimaryKey(id);
        return user;
    }

    /**
     * 用户登录
     */
    public String login(String data, String key) throws MaMaBikeException {
        //token
        String token = null;
        //解密后的明文数据
        String decryptData = null;
        try {
            //RSA解密AES的密钥key
            byte[] keyBytes = RSAUtil.decryptByPrivateKey(Base64Util.decode(key));

            //AES解密密文数据data
            decryptData = AESUtil.decrypt(data, new String(keyBytes, "UTF-8"));

            //校验数据
            if(decryptData == null) {
                throw new Exception();
            }

            //获取手机号及验证码
            JSONObject jsonObject = JSON.parseObject(decryptData);
            String mobile = jsonObject.getString("mobile");
            String code = jsonObject.getString("code");
            String platform = jsonObject.getString("platform");
            String channelId = jsonObject.getString("channelId"); //推送频道编码 单个设备唯一

            if(StringUtils.isBlank(mobile) || StringUtils.isBlank(code) || StringUtils.isBlank(platform) || StringUtils.isBlank(channelId)) {
                throw new Exception();
            }

            //去redis取验证码 比较手机号码和验证码是不是匹配 若匹配则说明是本人手机
            String verCode = commonCacheUtil.getCacheValue(mobile);
            User user;
            //判断安卓端传过来的验证码与redis中验证码是否相同
            if(code.equals(verCode)) {
                //手机验证码匹配，调用mapper通过手机号查询用户
                user = userMapper.selectByMobile(mobile);
                //用户不存在，则帮用户注册
                if(user == null) {
                    user = new User();
                    user.setMobile(mobile);
                    user.setNickname(mobile);
                    userMapper.insertSelective(user);
                }
            } else {
                throw new MaMaBikeException("手机号验证码不匹配");
            }

            //生成token
            try {
                token = generateToken(user);
            } catch (Exception e) {
                throw new MaMaBikeException("生成token失败");
            }

            //将token存入redis中，key为token，value为用户信息
            UserElement ue = new UserElement();
            ue.setMobile(mobile);
            ue.setPlatform(platform);
            ue.setToken(token);
            ue.setUserId(user.getId());
            ue.setPushChannelId(channelId);
            commonCacheUtil.putTokenWhenLogin(ue);

        } catch (Exception e) {
            log.error("Fail to decrypt data", e);
            throw new MaMaBikeException("数据解析错误");
        }

        return token;
    }

    /**
     * 更新用户昵称
     */
    public void modifyNickName(User user) throws MaMaBikeException {
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 短信验证码
     */
    public void sendVercode(String mobile, String ip) throws MaMaBikeException {
        //生成验证码
        String verCode = RandomNumberCode.verCoder();

        //校验
        int result = commonCacheUtil.cacheForVerificationCode(VERIFYCODE_PREFIX+mobile, verCode,"reg",60, ip);;
        if (result == 1) {
            log.info("当前验证码未过期，请稍后重试");
            throw new MaMaBikeException("当前验证码未过期，请稍后重试");
        } else if (result == 2) {
            log.info("超过当日验证码次数上线");
            throw new MaMaBikeException("超过当日验证码次数上限");
        } else if (result == 3) {
            log.info("超过当日验证码次数上限 {}", ip);
            throw new MaMaBikeException(ip + "超过当日验证码次数上限");
        }

        //记录日志
        log.info("Sending verify code {} for phone {}", verCode, mobile);

        //校验通过 发送短信 发消息到队列
        Destination destination = new ActiveMQQueue(SMS_QUEUE);
        Map<String, String> smsParam = new HashMap<String,String>();
        smsParam.put("mobile",mobile);
        smsParam.put("tplId", Constants.MDSMS_VERCODE_TPLID);
        smsParam.put("vercode",verCode);
        String message = JSON.toJSONString(smsParam);

        //发送消息队列给另外一个服务，由另外那个服务发送手机短信验证码
        smsProcessor.sendSmsToQueue(destination,message);
    }

    /**
     * 修改头像
     */
    public String uploadHeadImg(MultipartFile file, Long userId) throws MaMaBikeException {
        try {
            //获取user 得到原来的头像地址
            User user = userMapper.selectByPrimaryKey(userId);
            // 调用七牛
            String imgUrlName = QiniuFileUploadUtil.uploadHeadImg(file);
            user.setHeadImg(imgUrlName);
            //更新用户头像URL
            userMapper.updateByPrimaryKeySelective(user);
            return Constants.QINIU_HEAD_IMG_BUCKET_URL+"/"+Constants.QINIU_HEAD_IMG_BUCKET_NAME+"/"+imgUrlName;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new MaMaBikeException("头像上传失败");
        }
    }

    /**
     * 生成token
     * @param user
     * @return
     */
    private String generateToken(User user) {
        String source = user.getId()+":"+user.getMobile()+":"+System.currentTimeMillis();
        return MD5Util.getMD5(source);
    }

}


















