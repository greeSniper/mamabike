package com.tangzhe.mamabike.user.controller;

import com.tangzhe.mamabike.common.constants.Constants;
import com.tangzhe.mamabike.common.exception.MaMaBikeException;
import com.tangzhe.mamabike.common.resp.ApiResult;
import com.tangzhe.mamabike.common.rest.BaseController;
import com.tangzhe.mamabike.user.entity.LoginInfo;
import com.tangzhe.mamabike.user.entity.User;
import com.tangzhe.mamabike.user.entity.UserElement;
import com.tangzhe.mamabike.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("user")
public class UserController extends BaseController {

    private Logger log = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    @RequestMapping("/hello")
    public String hello() {
        return "hello spring boot";
    }

    @RequestMapping("/test")
    public User test() {
        User user = userService.findById(1L);
        return user;
    }

    /**
     * 用户登录
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<String> login(@RequestBody LoginInfo loginInfo) {

        ApiResult<String> apiResult = new ApiResult<String>();
        try {
            //获取数据密文
            String data = loginInfo.getData();
            //获取密钥密文
            String key = loginInfo.getKey();

            //校验数据
            if(StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
                throw new MaMaBikeException("参数校验失败");
            }

            //调用业务返回token
            String token = userService.login(data, key);
            apiResult.setCode(Constants.RESP_STATUS_OK);
            apiResult.setMessage("登录成功");
            apiResult.setData(token);

        } catch (MaMaBikeException e) {
            apiResult.setCode(e.getStatusCode());
            apiResult.setMessage(e.getMessage());
        } catch (Exception e) {
            //记录日志
            log.error("Fail to login", e);
            apiResult.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            apiResult.setMessage("内部错误");
        }

        return apiResult;
    }

    /**
     * 修改用户昵称
     */
    @RequestMapping("/modifyNickName")
    public ApiResult modifyNickName(@RequestBody User user) {

        ApiResult apiResult = new ApiResult();
        try {
            //通过请求头中带过来的token获取用户信息
            UserElement ue = getCurrentUser();
            user.setId(ue.getUserId());
            //调用业务通过id更新用户昵称
            userService.modifyNickName(user);

        } catch (MaMaBikeException e) {
            apiResult.setCode(e.getStatusCode());
            apiResult.setMessage(e.getMessage());
        } catch (Exception e) {
            //记录日志
            log.error("Fail to modify user nickname", e);
            apiResult.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            apiResult.setMessage("内部错误");
        }

        return apiResult;
    }

    /**
     * 短信验证码
     */
    @RequestMapping("/sendVercode")
    public ApiResult sendVercode(@RequestBody User user, HttpServletRequest request) {

        ApiResult apiResult = new ApiResult();
        try {
            //调用业务发送手机短信验证码
            String ip = getIpFromRequest(request);
            userService.sendVercode(user.getMobile(), ip);

        } catch (MaMaBikeException e) {
            apiResult.setCode(e.getStatusCode());
            apiResult.setMessage(e.getMessage());
        } catch (Exception e) {
            //记录日志
            log.error("Fail to modify user nickname", e);
            apiResult.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            apiResult.setMessage("内部错误");
        }

        return apiResult;
    }

    /**
     * 修改头像
     */
    @RequestMapping(value = "/uploadHeadImg", method = RequestMethod.POST)
    public ApiResult<String> uploadHeadImg(HttpServletRequest req, @RequestParam(required=false) MultipartFile file) {

        ApiResult<String> resp = new ApiResult<String>();
        try {
            UserElement ue = getCurrentUser();
            userService.uploadHeadImg(file, ue.getUserId());
            resp.setMessage("上传成功");
        } catch (MaMaBikeException e) {
            resp.setCode(e.getStatusCode());
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to update user info", e);
            resp.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误");
        }
        return resp;
    }

}





















