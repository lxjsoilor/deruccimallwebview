package com.derucci.deruccimallwebview.config;

/**
 * Author: 林雄军
 * Description: Description
 * Date: 2023/6/6
 */
public class KeyConfig {
    static public String TOKEN_KEY = "token";
    static public String JS_CANAL = "android";
    //    mall4Development
    //    test
    //    uat
    //    production
//    static public String H5_URL = "http://10.34.24.100:81";
    static public String H5_URL = "http://10.16.2.79:30452";
    static public String H5_URL_DEV = "http://10.16.2.79:30452";
    static public String H5_URL_TEST = "http://10.16.2.79:30452";
    static public String H5_URL_UAT = "http://10.16.2.79:30452";

    public enum ENV {
        PROD,
        UAT,
        TEST,
        DEV
    }
}
