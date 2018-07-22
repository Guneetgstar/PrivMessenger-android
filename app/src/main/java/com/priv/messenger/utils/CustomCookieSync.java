package com.priv.messenger.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.webkit.CookieManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.priv.messenger.R;

import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;

public class CustomCookieSync {


    public static boolean storeToWebSync(Context context){
        PersistentCookieStore store = new PersistentCookieStore(context);
        CookieManager webCookie = CookieManager.getInstance();
        try {
            List<Cookie> cookies = store.getCookies();
            for (Cookie cookie : cookies)
            {
                //if (cookie.getDomain().startsWith(context.getResources().getString(R.string.domain)))
                webCookie.setCookie(context.getResources().getString(R.string.domain), cookie.getName() + "=" + cookie.getValue() + ";");
                webCookie.flush();
            }


            Log.d("Cookie in web",webCookie.getCookie(context.getResources().getString(R.string.domain)));
            return true;
        }catch (Exception e){
            Log.d("CookieSyncError",e.toString());
            return false;
        }
    }
    public static void webToStoreSync(Context context, AsyncHttpClient client){
        CookieManager cookieManager=CookieManager.getInstance();
        String stringCookie=cookieManager.getCookie(context.getResources().getString(R.string.domain));
        if(stringCookie!=null) {
            Log.d("cookie", stringCookie);
            client.addHeader("Cookie", stringCookie);
        }
    }

}
