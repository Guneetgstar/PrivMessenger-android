package com.priv.messenger.utils.waste;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Gunee on 28-01-2018.
 */

public class QueryString {

    private StringBuilder query = new StringBuilder();

    public QueryString() {
    }

    public synchronized void add(String name, String value) {
        if(!query.toString().equals(""))
            query.append('&');
        encode(name, value);
    }

    private synchronized void encode(String name, String value) {
        try {
            query.append(URLEncoder.encode(name, "UTF-8"));
            query.append('=');
            query.append(URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Broken VM does not support UTF-8");
        }
    }

    private synchronized String getQuery() {
        return query.toString();
    }

    @Override
    public String toString() {
        return getQuery();
    }
}