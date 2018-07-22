package com.priv.messenger.frag;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.priv.messenger.R;
import com.priv.messenger.utils.CustomCookieSync;

public class UploadFragment extends Fragment {
    private WebView webView;
    ProgressBar progressBar;
    public ValueCallback<Uri[]> uploadMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        progressBar=(ProgressBar)getView().findViewById(R.id.progressBar);
        progressBar.setMax(100);
        //progressBar.setVisibility(View.GONE);
        webView =(WebView)getView().findViewById(R.id.web);
        CustomCookieSync.storeToWebSync(getActivity().getApplicationContext());
        webView.loadUrl(getResources().getString(R.string.site_p));
        progressBar.setProgress(0);
        //progressBar.setVisibility(View.VISIBLE);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setClickable(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOnTouchListener(new View.OnTouchListener() {
            float m_downX;
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getPointerCount() > 1) {
                    //Multi touch detected
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        // save the x
                        m_downX = event.getX();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        // set x so that it doesn't move
                        event.setLocation(m_downX, event.getY());
                        break;
                    }

                }
                return false;
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return (false);
            }
        });
        webView.setWebChromeClient(new Callback());
        //webView.addJavascriptInterface(new MainActivity.Web(this),"Android");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.web, container,false);
    }
    private class Callback extends WebChromeClient {  //HERE IS THE MAIN CHANGE.
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(newProgress==100){
                progressBar.setVisibility(View.GONE);
            }
            else {
                progressBar.setVisibility(View.VISIBLE);
            }
            progressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }
            uploadMessage = filePathCallback;
            Intent intent = fileChooserParams.createIntent();
            try
            {
                startActivityForResult(intent, 1);
            } catch (ActivityNotFoundException e)
            {
                uploadMessage = null;
                Toast.makeText(getActivity().getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if (requestCode == 1)
        {
            if (uploadMessage == null)
                return;
            uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
            uploadMessage = null;
        }


    }
    private class Web{
        Context con;
        Web(Context con){
            this.con=con;
        }
        @JavascriptInterface
        void fun(){
            Toast.makeText(con,"hey this is java script from android",Toast.LENGTH_SHORT).show();
        }
        @JavascriptInterface
        void not(){
            Toast.makeText(con,"hey this is java script from android",Toast.LENGTH_SHORT).show();

        }
        @JavascriptInterface
        void ask(){
            Toast.makeText(con,"hey this is java script from android",Toast.LENGTH_SHORT).show();
        }
        void fuck(){
            Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent,1);
        }
    }

}
