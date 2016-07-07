package com.omdd.gdyb.utils;

/**
 * Created by Administrator on 2016/6/11 0011.
 */
public interface Constant {

    String NAMESPACE ="http://impl.service.webservice.business.platform.gz3h.com/";

    String PATH = "/";

    String WIFI_SSID = "flashair123";

    String WIFI_PWD = "12345678";

    /** 三和服务器 Url 开始 */

    String URL_MAIN = "http://121.33.208.196:9000/jg_xcjz_server_platform/service/platformDataService";
    /**  用户登录  */
    String METHOD_LOGIN = "CheckUserLogin";
    /**  退出登录  */
    String METHOD_LOGOUT = "FinishSendData";
    /**  获取计划列表  */
    String METHOD_GETPLANLIST = "GetPlanList";
    /**  获取计划详情  */
    String METHOD_GETPLAN = "GetPlan";
    /**  上传文件  */
    String METHOD_UPLOAD = "SendTestData";

    /** 三和服务器 Url 结束 */

    /**  获取文件列表  */
    String FA_CMD_GETFILELIST = "http://flashair/command.cgi?op=100&DIR=";

    /**  获取文件数量  */
    String FA_CMD_GETFILECOUNT = "http://flashair/command.cgi?op=101&DIR=/DCIM";

    /**  下载文件  */
    String FA_CMD_DOWNLOAD = "http://flashair";


    String RESPONSE_NAME_LOGIN = "CheckUserLoginResponse";  //登录失败返回

    String RESPONSE_NAME_LOGOUT = "FinishSendDataResponse";

    String RESPONSE_NAME_PLAN = "GetPlanResponse";

    String RESPONSE_NAME_PLANLIST = "GetPlanListResponse";

    String RESPONSE_NAME_UPLOAD = "SendTestDataResponse";//0：成功；1：数据类型不正确； 2:文件已存在；3:用户信息错误，请重新登录 ；4:系统错误；

}
