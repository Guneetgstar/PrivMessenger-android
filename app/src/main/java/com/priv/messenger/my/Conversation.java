package com.priv.messenger.my;


import org.json.JSONObject;

import java.io.Serializable;


/**
 * Created by Gunee on 21-03-2018.
 */

public class Conversation implements Serializable {
    private JSONObject object;
    boolean active;

    public boolean isActive() {
        return active;
    }

    public  Conversation(JSONObject object,boolean active){
        this.object=object;
        this.active=active;
    }

    public JSONObject getJSONObject() {
        return object;
    }



}
