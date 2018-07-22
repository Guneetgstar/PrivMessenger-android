package com.priv.messenger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.priv.messenger.my.Conversation;
import com.priv.messenger.my.MessegesRecyclerAdapter;
import com.priv.messenger.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Chat extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Conversation> conversations;
    LinearLayoutManager linearLayoutManager;
    Button send;
    EditText msg;
    MessegesRecyclerAdapter adapter;

    AsyncHttpClient client;
    PersistentCookieStore store;
    String profile;
    static int count=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        send=(Button)findViewById(R.id.send);
        msg=(EditText)findViewById(R.id.enter);
        Bundle bundle=getIntent().getExtras();
        profile=null;
        if(bundle!=null){
            profile=bundle.getString("profile");
        }else Log.d("bundle","not found");

        recyclerView=(RecyclerView)findViewById(R.id.msgs);
        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(linearLayoutManager);
        client = new AsyncHttpClient();
        store = new PersistentCookieStore(getApplicationContext());

        client.setCookieStore(store);

        Log.d("chat", "calling");

        RequestParams param=new RequestParams();
        param.add(Utils.FRIEND,profile);

        client.post(getResources().getString(R.string.site)+"Messeges",param, new JsonHttpResponseHandler(){
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseBody) {
                Log.d("cookie", "connected: " + statusCode);

                if(responseBody!=null) {
                    conversations=new ArrayList<>();
                    for (int i=0;i<responseBody.length();i++) {
                        JSONObject object = null;
                        try {
                            object = (JSONObject) responseBody.get(i);
                            conversations.add(new Conversation(object, false));
                        } catch (JSONException e) {
                            Log.d("JSON", "json object not found" + e);
                        }
                    }
                    adapter=new MessegesRecyclerAdapter(getApplicationContext(), conversations);
                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error,JSONArray responseBody) {
                Log.d("cookie", "failed to conct\n" + error + statusCode);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams param=new RequestParams();
                param.add("msg",msg.getText().toString());
                param.add("rcvr",profile);
                client.post(getResources().getString(R.string.site) + "NewMsg", param, new JsonHttpResponseHandler() {
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                Log.d("cookie", "failed to conct\n" + throwable + statusCode);
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject responseString) {
                                Log.d("cookie", "connected: " + statusCode);
                                if (statusCode == 200) {
                                    msg.setText("");
                                    if(conversations==null) {
                                        conversations = new ArrayList<>();
                                        adapter=new MessegesRecyclerAdapter(getApplicationContext(), conversations);
                                        recyclerView.scrollToPosition(adapter.getItemCount()-1);
                                        recyclerView.setAdapter(adapter);
                                    }
                                    conversations.add(new Conversation(responseString,false));
                                    adapter.addJsonObjetct(responseString);
                                    adapter.notifyDataSetChanged();
                                    recyclerView.smoothScrollToPosition(adapter.getItemCount());

                                }
                            }
                        });
                         /*   conversations=new ArrayList<>();
                            for (int i=0;i<responseBody.length();i++) {
                                JSONObject object=null;
                                try {
                                    object=(JSONObject)responseBody.get(i);
                                    conversations.add(new Conversation(object, false));
                                } catch (JSONException e) {
                                    Log.d("JSON","json object not found"+e);
                                }

                            }
                            adapter=new MessegesRecyclerAdapter(getApplicationContext(),conversations);
                            recyclerView.setAdapter(adapter);

                        }*/

                Log.d("adpter","adapted");
            }
        });
    }

}
