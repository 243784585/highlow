package com.omdd.gdyb.utils;

/**
 * Created by Administrator on 2016/6/14 0014.
 */
public class SessionValidate {

    /**
     * 校验传输数据是否完全
     * @return 数据完整返回true,否则false
     */
    public static boolean isDataIntegrity(){
        return !"".equals(Session.getString(Session.KEY_LOGIN_PWD))
                && !"".equals(Session.getString(Session.KEY_LOGIN_USER));
    }
}
