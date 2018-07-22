package com.priv.messenger.frag;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.priv.messenger.MainAfLogin;
import com.priv.messenger.R;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Gunee on 16-02-2018.
 */

public class SignUpFragment extends Fragment {
    TextView click;
    boolean check,check1,check2;
    AutoCompleteTextView email;
    AutoCompleteTextView mob;
    EditText pw;
    EditText pw1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup_frag, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        click=(TextView)getView().findViewById(R.id.clickme);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm=getFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                ft.replace(R.id.frame,new LoginFragment());
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();
            }
        });

        email=(AutoCompleteTextView)getView().findViewById(R.id.un);
        mob=(AutoCompleteTextView)getView().findViewById(R.id.mob);
        pw=(EditText) getView().findViewById(R.id.pw);
        pw1=(EditText)getView().findViewById(R.id.pw1);

        Button signup=(Button)getView().findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check=validate(email.getText().toString());
                if(mob.getText().toString().length()!=10) {
                    Toast.makeText(getActivity(), "invalid details", Toast.LENGTH_SHORT).show();
                    check1 = false;
                }
                else
                    check1 = true;
                if(pw.getText().toString().equals(pw1.getText().toString()))
                    check2=true;
                else{
                    Toast.makeText(getActivity(), "invalid details", Toast.LENGTH_SHORT).show();
                    check2=false;
                }
                Log.d("sign","starting");

                if(check2&&check&&check1){
                    AsyncHttpClient client=new AsyncHttpClient();
                    PersistentCookieStore store=new PersistentCookieStore(getActivity());
                    client.setCookieStore(store);
                    RequestParams params=new RequestParams();
                    params.add("email",email.getText().toString());
                    params.add("number",mob.getText().toString());
                    params.add("password",pw.getText().toString());
                    client.post("http://192.168.0.126:8084/PrivMessenger/SignUp",params, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Intent intent=new Intent(getActivity(), MainAfLogin.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Snackbar.make(getView(),"Please try again",Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });



    }
    boolean validate(String query){
        if(query.contains("@")&&query.contains("."))
            return true;
        else
            return false;

        }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
