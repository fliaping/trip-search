package com.fliaping.trip.search;

/**
 * Created by Payne on 4/28/16.
 */
public class Util {

    public static int notNull(String string, int defaultVal){
        if(null != string){
            return Integer.parseInt(string) ;
        }else {
            return defaultVal;
        }
    }
    public static String notNull(String string, String defaultVal){
        if(null != string){
            return string ;
        }else {
            return defaultVal;
        }
    }
}
