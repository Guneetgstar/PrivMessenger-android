package com.priv.messenger.frag;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.priv.messenger.MainAfLogin;
import com.priv.messenger.R;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Gunee on 16-02-2018.
 */

public class LoginFragment extends Fragment {
    public LoginFragment() {
        super();
    }

    AutoCompleteTextView un;
    EditText pw;
    Button login;
    TextView click;

    PersistentCookieStore store;
    AsyncHttpClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        login = (Button) getView().findViewById(R.id.login1);
        click = (TextView) getView().findViewById(R.id.clickme);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frame, new SignUpFragment());
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });

        store = new PersistentCookieStore(getActivity().getApplicationContext());
        client = new AsyncHttpClient();
        client.setCookieStore(store);
        un = (AutoCompleteTextView) getView().findViewById(R.id.un);
        pw = (EditText) getView().findViewById(R.id.pw);
        if (store.getCookies().size()<2)
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!(un.getText().toString().equals("") || pw.getText().toString().equals(""))) {
                        Toast.makeText(getActivity(), "processing", Toast.LENGTH_SHORT).show();
                        Log.d("login", "processing");
                        RequestParams params = new RequestParams();
                        params.add("uname", un.getText().toString());
                        params.add("password", pw.getText().toString());
                        client.setConnectTimeout(3000);
                        client.post(getResources().getString(R.string.site) + "LogIn", params, new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                Log.d("login", "failed: " + statusCode + throwable.toString());

                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                for (cz.msebera.android.httpclient.cookie.Cookie cookie : store.getCookies()) {
                                    Log.d("Cookies is", cookie.getName() + "-->" + cookie.getValue() + ": max age" + cookie.getExpiryDate());
                                }
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), MainAfLogin.class);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            }


                        });

                    } else Toast.makeText(getActivity(), "Enter credentials", Toast.LENGTH_SHORT).show();

                }
                // }
            });
        else {
            Intent intent = new Intent(getActivity(), MainAfLogin.class);
            getActivity().startActivity(intent);
        }

        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_frag, container, false);
    }
}
