package com.priv.messenger.fire;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.loopj.android.http.*;
import com.priv.messenger.R;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

public class HandleFirebaseToken extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token", "Refreshed token: " + refreshedToken);
        if (refreshedToken != null) {
            SyncHttpClient client = new SyncHttpClient();
            PersistentCookieStore store=new PersistentCookieStore(getApplicationContext());

            client.setCookieStore(store);
            RequestParams params = new RequestParams();
            params.add("token", refreshedToken);
            client.post(getResources().getString(R.string.site) + "Token", params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("Token", "request to server failed" + throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("Token", "sent to server :" + responseString);
                }
            });
        }
    }
}
