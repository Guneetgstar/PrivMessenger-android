package com.priv.messenger.my.ChatRecycler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.loopj.android.http.PersistentCookieStore;
import com.priv.messenger.Chat;
import com.priv.messenger.R;
import com.priv.messenger.my.Conversation;
import com.priv.messenger.utils.waste.OnSwipe;
import com.priv.messenger.utils.waste.SetOnSwipeListener;
import com.priv.messenger.utils.Utils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder> implements ChatTouchHelperCallback {
    Context context;
    ArrayList<Conversation> conversationList;
    ArrayList<String> profile;
    ArrayList<JSONObject> objects;


    public ChatRecyclerAdapter(Context context,ArrayList<Conversation> conversationList) {
        this.context = context;
        this.conversationList = conversationList;
        this.objects = new ArrayList<>();
        this.profile=new ArrayList<>();
        for (Conversation conversation : conversationList) {
            this.objects.add(conversation.getJSONObject());
        }
    }
    public void clearAll(){
        conversationList.clear();
        profile.clear();
        objects.clear();
    }
    public void addAll(ArrayList<Conversation> conversationList){
        this.conversationList=conversationList;
        this.objects = new ArrayList<>();
        this.profile=new ArrayList<>();
        for (Conversation conversation : conversationList) {
            this.objects.add(conversation.getJSONObject());
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.chat, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String uname="";
        uname=Utils.getUsername(context);
        String profile= Utils.getString(objects.get(position),Utils.RECIVER_ID).equals(uname)
                ?Utils.getString(objects.get(position),Utils.SENDER_ID)
                :Utils.getString(objects.get(position),Utils.RECIVER_ID);
        holder.con_name.setText(profile);
        this.profile.add(position,profile);
        holder.con_msg.setText(Utils.getString(objects.get(position),Utils.MSG));
        if(conversationList.get(position).isActive()){
            holder.status.setText("Active");
        }else
            holder.status.setText("");
        Log.d("url",context.getResources().getString(R.string.site)+"user/"+profile+".jpg");
        Picasso.with(context)
                .load(context.getResources().getString(R.string.site)+"user/"+profile+".jpg")
                .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Conversation prev=conversationList.remove(fromPosition);
        conversationList.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        conversationList.remove(position);
        profile.remove(position);
        objects.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,ChatTouchViewHolder {
        TextView con_name;
        TextView con_msg;
        TextView status;
        ImageView imageView;


        public ViewHolder(final View view){

            super(view);
            con_name=(TextView)view.findViewById(R.id.con_name);
            con_msg=(TextView)view.findViewById(R.id.con_msg);
            status=(TextView)view.findViewById(R.id.stat);
            imageView=(ImageView)view.findViewById(R.id.image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent=new Intent(context,Chat.class);
            intent.putExtra("profile",profile.get(getLayoutPosition()));
            context.startActivity(intent);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
