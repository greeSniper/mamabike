package com.tangzhe.mamabike.user.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangzhe 2017/09/10.
 */
public class UserElement {

    private Long userId;

    private String mobile;

    private String token;

    private String platform;

    private String pushUserId;

    private String pushChannelId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPushUserId() {
        return pushUserId;
    }

    public void setPushUserId(String pushUserId) {
        this.pushUserId = pushUserId;
    }

    public String getPushChannelId() {
        return pushChannelId;
    }

    public void setPushChannelId(String pushChannelId) {
        this.pushChannelId = pushChannelId;
    }

    /**
     * 转 map
     * @return
     */
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("platform", this.platform);
        map.put("userId", this.userId + "");
        map.put("token", token);
        map.put("mobile", mobile);
        if (this.pushUserId != null) {
            map.put("pushUserId", this.pushUserId);
        }
        if (this.pushChannelId != null) {
            map.put("pushChannelId", this.pushChannelId);
        }
        return map;
    }

    /**
     * map转对象
     * @param map
     * @return
     */
    public static UserElement fromMap(Map<String, String> map) {
        UserElement ue = new UserElement();
        ue.setPlatform(map.get("platform"));
        ue.setToken(map.get("token"));
        ue.setMobile(map.get("mobile"));
        ue.setUserId(Long.parseLong(map.get("userId")));
        ue.setPushUserId(map.get("pushUserId"));
        ue.setPushChannelId(map.get("pushChannelId"));
        return ue;
    }

}
