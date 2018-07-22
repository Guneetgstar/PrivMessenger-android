package com.priv.messenger.utils.waste;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Gunee on 28-01-2018.
 */

public class RequestAsyncTask extends AsyncTask {
    Context context;
    OnAsyncFinish onAsyncFinish;
    String query;


    URL url;
    ProgressDialog progressDialog;
    URLConnection connection;
    OutputStream os;
    InputStream is;
    Scanner scanner;

    ArrayList<String> result;

    public RequestAsyncTask(String url,String query,Context context,OnAsyncFinish onAsyncFinish) {
        super();
        try {
            this.url=new URL(url);
            Log.d("Url","constructed");
        } catch (MalformedURLException e) {
            Log.d("Url","malformed");
            System.exit(0);
        }
        this.query=query;
        this.context=context;
        this.onAsyncFinish = onAsyncFinish;
        result=new ArrayList<>();
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            connection=url.openConnection();
            Log.d("Url","connection opend");
            connection.setDoOutput(true);
            try {
                os=connection.getOutputStream();
            }catch (IOException Ex){
                Log.d("Url","outputstream not found"+Ex);
            }
            PrintWriter pw=new PrintWriter(os);
            pw.write(query);
            pw.write("\r\n");
            pw.flush();
            is=connection.getInputStream();
            scanner=new Scanner(is);
            while (scanner.hasNext()){
                result.add(scanner.nextLine());
            }

        }
        catch (IOException io){
            Log.d("URL","stream not found");
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        Log.d("Url","on pre execute");
        /*progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait");
        progressDialog.show();*/
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        //progressDialog.dismiss();
        onAsyncFinish.onFinish(result);
        super.onPostExecute(o);
    }
}
