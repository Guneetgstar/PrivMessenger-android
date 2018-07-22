package com.priv.messenger;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

public class NewMsg extends AppCompatActivity {
    ProgressBar progressBar;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_msg);
        progressBar=(ProgressBar) findViewById(R.id.searching);
        editText=(EditText)findViewById(R.id.search);
    }

    public void find(View view) {
        if(editText.getText().toString().equals(""))
            Toast.makeText(getApplicationContext(),"Enter Text first",Toast.LENGTH_SHORT).show();
        else {
            progressBar.setMax(100);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(25);
            AsyncHttpClient client = new AsyncHttpClient();
            PersistentCookieStore store = new PersistentCookieStore(this);
            client.setCookieStore(store);
            client.get(getResources().getString(R.string.site) + "Find"
                    , new RequestParams("keyword", editText.getText().toString()), new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Error searching, Try later",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            if(statusCode==200){
                                Intent intent=new Intent();
                                intent.setClass(getApplicationContext(),Chat.class);
                                intent.putExtra("profile",responseString);
                                startActivity(intent);
                                finish();
                            }else if(statusCode==204){
                                alert();
                                editText.setText("");
                                progressBar.setVisibility(View.GONE);
                            }
                            else
                                Toast.makeText(getApplicationContext(),statusCode,Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void alert() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("User Not Found").setMessage("No result found for the given user, please try a different one.");
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog= builder.create();
        dialog.show();

    }
}
