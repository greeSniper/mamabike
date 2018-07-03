package com.tangzhe.mamabike.common.constants;

/**
 * Created by tangzhe 2017/9/9.
 */
public class Constants {

    /**自定义状态码 start**/
    public static final int RESP_STATUS_OK = 200;

    public static final int RESP_STATUS_NOAUTH = 401;

    public static final int RESP_STATUS_INTERNAL_ERROR = 500;

    public static final int RESP_STATUS_BADREQUEST = 400;
    /**自定义状态码 end**/

    //用户token
    public static final String REQUEST_TOKEN_KEY = "user-token";

    //安卓版本
    public static final String REQUEST_VERSION_KEY ="version" ;

    /**秒滴SMS start**/
    public static final String MDSMS_ACCOUNT_SID ="21f38a48531a44988cb71fcd95789448";

    public static final String MDSMS_AUTH_TOKEN ="cb4e99b72a3b4d61b20d88b1ecb09573";

    public static final String MDSMS_REST_URL="https://api.miaodiyun.com/20150822";

    public static final String MDSMS_VERCODE_TPLID = "71666425"; //请用自己的短信模版ID替换
    /**秒滴SMS end**/

    /***七牛keys start****/
    public static final String QINIU_ACCESS_KEY="dw3yz9IlIugQKwc48jmPmyxmiB2CiOYKps19CbVo";

    public static final String QINIU_SECRET_KEY="_oGRNapUhgEH-XqktHU7fJFoh6pKbEtrMTyEqXAa";

    public static final String QINIU_HEAD_IMG_BUCKET_NAME="mamabike";

    public static final String QINIU_HEAD_IMG_BUCKET_URL="ow4bmc2e2.bkt.clouddn.com";
    /***七牛keys end****/

    /**百度云推送 start**/
    public static final String BAIDU_YUN_PUSH_API_KEY="";

    public static final String BAIDU_YUN_PUSH_SECRET_KEY="";

    public static final String CHANNEL_REST_URL = "api.push.baidu.com";
    /**百度云推送end**/

}
