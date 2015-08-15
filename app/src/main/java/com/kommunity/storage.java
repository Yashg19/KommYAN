package com.kommunity;

/**
 * Created by spidy on 4/19/15.
 */
public class storage {

    public static double latitude;
    public static double longitude;
    public static String header;
    public static String content;
   // public static String going;

    public storage(double lat,double longit, String head, String cont, String going){
        latitude = lat;
        longitude = longit;
        header = head;
        content = cont;
       // this.going = going;
    }
}
