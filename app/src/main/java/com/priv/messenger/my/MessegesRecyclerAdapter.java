package com.priv.messenger.my;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.PersistentCookieStore;
import com.priv.messenger.R;
import com.priv.messenger.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;



public class MessegesRecyclerAdapter extends RecyclerView.Adapter<MessegesRecyclerAdapter.ViewHolder> {
    Context context;
    ArrayList<Conversation> mssgs;
    ArrayList<JSONObject> objects;


    public MessegesRecyclerAdapter(Context context,ArrayList<Conversation> mssgs) {
        this.context = context;
        this.mssgs = mssgs;
        this.objects = new ArrayList<>();
        for (Conversation conversation : mssgs) {
            this.objects.add(conversation.getJSONObject());
        }
    }
    public void addJsonObjetct(JSONObject object){
        this.objects.add(object);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.msgs, null);
        return new ViewHolder(view);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PersistentCookieStore store=new PersistentCookieStore(context);
        List<Cookie> cookies=store.getCookies();
        String uname="";
        if(!cookies.isEmpty()){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals("email"))
                    uname=cookie.getValue();
            }
        }
        String profile= Utils.getString(objects.get(position),Utils.RECIVER_ID).equals(uname)
              ?Utils.getString(objects.get(position),Utils.SENDER_ID)
                :Utils.getString(objects.get(position),Utils.RECIVER_ID);
        holder.msg.setText(Utils.getString(objects.get(position),Utils.MSG));

    }


    @Override
    public int getItemCount() {
        return mssgs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView msg;

        public ViewHolder(View itemView) {
            super(itemView);
            msg=(TextView)itemView.findViewById(R.id.msg);
        }
    }
}
