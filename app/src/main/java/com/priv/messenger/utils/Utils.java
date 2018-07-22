package com.priv.messenger.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;


import com.loopj.android.http.PersistentCookieStore;
import cz.msebera.android.httpclient.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Gunee on 22-03-2018.
 */

public class Utils {
    final public static String RECIVER_ID="reciver";
    final public static String SENDER_ID="sender";
    final public static String MSG_ID="msg_ID";
    final public static String MSG="msg";
    final public static String DATE="date";
    final public static String FRIEND="friend";


     static public String getString(JSONObject object,String key){
         String value=null;
         try {
             value=object.getString(key);
         } catch (JSONException e) {
             Log.d("JSON",key+" key not found"+e);
         }
         return value;
     }
     public static String getUsername(Context context){
         PersistentCookieStore store=new PersistentCookieStore(context);
         List<Cookie> cookies=store.getCookies();
         String uname="";
         if(!cookies.isEmpty()){
             for(Cookie cookie:cookies){
                 if(cookie.getName().equals("email"))
                     uname=cookie.getValue();
             }
         }
         return uname;
     }


}
