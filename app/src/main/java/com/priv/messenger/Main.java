package com.priv.messenger;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.priv.messenger.frag.LoginFragment;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.cookie.Cookie;

import java.util.List;

/**
 * Created by Gunee on 16-02-2018.
 */

public class Main extends AppCompatActivity {
    public static AppCompatActivity activity;
    PersistentCookieStore store;

    @Override
    protected void onResume() {
        super.onResume();
        validateCookie();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);
        activity = this;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frame, new LoginFragment(), "log");
        ft.commit();
        validateCookie();
    }

    private void validateCookie() {
        store = new PersistentCookieStore(getApplicationContext());
        AsyncHttpClient client = new AsyncHttpClient();
        client.setCookieStore(store);
        if (store.getCookies().isEmpty()) {
            final RequestParams params = new RequestParams();
            params.add("token", FirebaseInstanceId.getInstance().getToken());
            client.post(getResources().getString(R.string.site) + "Token", params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("Token", "request to server failed :" + params + throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("Token", "sent to server :" + responseString+store.getCookies().size());
                }
            });
        }
        else {
            StringBuilder cookie=new StringBuilder();
            List<Cookie> cookies=store.getCookies();
            for(Cookie cookie1:cookies){
                cookie.append(cookie1.getName()+"-");
            }
            Log.d("Cookie Validation :", String.valueOf(store.getCookies().size())+cookie);
        }
    }
}
