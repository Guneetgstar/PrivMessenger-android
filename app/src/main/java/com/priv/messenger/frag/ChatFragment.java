package com.priv.messenger.frag;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.priv.messenger.Main;
import com.priv.messenger.R;
import com.priv.messenger.my.ChatRecycler.ChatRecyclerAdapter;
import com.priv.messenger.my.ChatRecycler.ChatTouchHelperCallback;
import com.priv.messenger.my.ChatRecycler.SimpleChatTouchListener;
import com.priv.messenger.my.Conversation;
import com.priv.messenger.utils.CustomCookieSync;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 *
 * Created by Gunee on 21-03-2018.
 */

public class ChatFragment extends Fragment {

    ArrayList<Conversation> conversations;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progress;
    SwipeRefreshLayout refreshLayout;

    AsyncHttpClient client;
    ChatRecyclerAdapter adapter;
    PersistentCookieStore store;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView=(RecyclerView)getView().findViewById(R.id.chat_list);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(linearLayoutManager);
        refreshLayout=(SwipeRefreshLayout)getActivity().findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                client.get(getResources().getString(R.string.site) + "Conversations", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray responseBody) {
                        Log.d("cookie", "connected: " + statusCode);
                        if (adapter != null) {
                            adapter.clearAll();
                            conversations.clear();
                            adapter.notifyDataSetChanged();
                        progress.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);
                        if (responseBody != null) {
                            for (int i = 0; i < responseBody.length(); i++) {
                                JSONObject object = null;
                                try {
                                    object = (JSONObject) responseBody.get(i);
                                    conversations.add(new Conversation(object, false));
                                } catch (JSONException e) {
                                    Log.d("JSON", "json object not found" + e);
                                }

                            }
                        }
                        adapter.addAll(conversations);
                        adapter.notifyDataSetChanged();

                        }else {
                            conversations=new ArrayList<>();
                            progress.setVisibility(View.GONE);
                            refreshLayout.setRefreshing(false);
                            if (responseBody != null) {
                                for (int i = 0; i < responseBody.length(); i++) {
                                    JSONObject object = null;
                                    try {
                                        object = (JSONObject) responseBody.get(i);
                                        conversations.add(new Conversation(object, false));
                                    } catch (JSONException e) {
                                        Log.d("JSON", "json object not found" + e);
                                    }

                                }
                            }
                            adapter = new ChatRecyclerAdapter(getActivity(), conversations);
                            recyclerView.setAdapter(adapter);
                            ItemTouchHelper.Callback listener = new SimpleChatTouchListener(adapter);
                            ItemTouchHelper helper = new ItemTouchHelper(listener);
                            helper.attachToRecyclerView(recyclerView);
                        }
                        }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable error,JSONArray responseBody) {
                        Log.d("cookie", "failed to conct\n" + error + statusCode);
                        progress.setVisibility(View.INVISIBLE);
                        refreshLayout.setRefreshing(false);

                    }
                });
            }
        });
        progress= (ProgressBar)getActivity().findViewById(R.id.progress);
        DoubleBounce bounce=new DoubleBounce();
        bounce.setColor(getResources().getColor(R.color.colorPrimary));
        progress.setIndeterminateDrawable(bounce);
        client = new AsyncHttpClient();
        CustomCookieSync.webToStoreSync(getActivity().getApplicationContext(),client);
        store = new PersistentCookieStore(getActivity().getApplicationContext());
        client.setCookieStore(store);
        Log.d("Cookie Validation:", String.valueOf(store.getCookies().size()));
        Log.d("chat", "calling");

        client.get(getResources().getString(R.string.site) + "Conversations", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseBody) {
                Log.d("cookie", "connected: " + statusCode+store.getCookies().size());
                Main.activity.finish();
                progress.setVisibility(View.GONE);
                if(responseBody!=null) {
                    conversations=new ArrayList<>();
                    for (int i=0;i<responseBody.length();i++) {
                        JSONObject object=null;
                        try {
                            object=(JSONObject)responseBody.get(i);
                            conversations.add(new Conversation(object, false));
                        } catch (JSONException e) {
                            Log.d("JSON","json object not found"+e);
                        }

                    }
                    adapter=new ChatRecyclerAdapter(getActivity(),conversations);
                    recyclerView.setAdapter(adapter);
                    ItemTouchHelper.Callback listener=new SimpleChatTouchListener(adapter);
                    ItemTouchHelper helper=new ItemTouchHelper(listener);
                    helper.attachToRecyclerView(recyclerView);

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error,JSONArray responseBody) {
                Log.d("cookie", "failed to conct\n" + error + statusCode);
                progress.setVisibility(View.INVISIBLE);
                if(statusCode==204){
                    store.clear();
                    Intent intent=new Intent(getActivity().getCallingActivity().getClassName());
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }



            }
        });
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_list, container, false);
    }
}
