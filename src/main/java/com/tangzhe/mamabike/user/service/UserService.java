package com.tangzhe.mamabike.user.service;

import com.tangzhe.mamabike.common.exception.MaMaBikeException;
import com.tangzhe.mamabike.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User findById(long id);
    String login(String data, String key) throws MaMaBikeException;
    void modifyNickName(User user) throws MaMaBikeException;
    void sendVercode(String mobile, String ip) throws MaMaBikeException;
    String uploadHeadImg(MultipartFile file, Long userId) throws MaMaBikeException;

}
