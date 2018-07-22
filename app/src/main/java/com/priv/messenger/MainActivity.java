package com.priv.messenger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    TextView name;
    Button login;
    LinearLayout ll;
    EditText message;
    Button send;
    PopupWindow mPopupWindow;
    ConstraintLayout cl;
    Button logout;
    ImageButton image;

    FirebaseApp app;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    DatabaseReference dataRefer;
    StorageReference storeRefer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        init();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter text!!!", Toast.LENGTH_SHORT).show();
                } else {
                    String text = message.getText().toString();
                    dataRefer.push().setValue(new Chat(text, "android"));
                }
            }
        });

        dataRefer.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                addView(chat);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)
                        getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.pop, null);
                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                );
                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                final EditText id = (EditText) customView.findViewById(R.id.usname);
                final EditText pw = (EditText) customView.findViewById(R.id.pw);
                Button valid = (Button) customView.findViewById(R.id.validate);
                valid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (id.getText().toString().isEmpty() || pw.getText().toString().isEmpty())
                            Toast.makeText(getApplicationContext(), "Enter valid id/password!", Toast.LENGTH_SHORT).show();
                        else {
                            auth.signInWithEmailAndPassword(id.getText().toString(), pw.getText().toString());
                            mPopupWindow.dismiss();
                        }
                    }
                });
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                    }
                });
                mPopupWindow.setFocusable(true);
                mPopupWindow.showAtLocation(cl, Gravity.CENTER, 0, 0);
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent,1);
            }
        });
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    name.setText(firebaseAuth.getCurrentUser().getEmail());
                    login.setVisibility(View.GONE);

                    logout.setText("Logout");
                    logout.setVisibility(View.VISIBLE);
                    logout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            auth.signOut();
                        }
                    });


                } else {
                    logout.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    name.setText("android");

                }

            }
        });


    }


    void init() {
        name = (TextView) findViewById(R.id.name);
        login = (Button) findViewById(R.id.login);
        ll = (LinearLayout) findViewById(R.id.ll);
        logout = (Button) findViewById(R.id.logout);
        image=(ImageButton)findViewById(R.id.image);
        message = (EditText) findViewById(R.id.text);
        send = (Button) findViewById(R.id.send);
        cl = (ConstraintLayout) findViewById(R.id.cl);

        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        auth = FirebaseAuth.getInstance(app);
        storage = FirebaseStorage.getInstance(app);
        dataRefer = database.getReference("chat");
        storeRefer= storage.getReference("chat-photos");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData){
        if (requestCode == 1 && resultCode == this.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("file: ", "Uri: " + uri.toString());
                final StorageReference photoRefer=storeRefer.child(UUID.randomUUID().toString());
                photoRefer.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl=taskSnapshot.getDownloadUrl();
                        message.setText(downloadUrl.toString());
                    }
                });
            }
        }
    }

    void addView(Chat text) {
        TextView addname = new TextView(this);
        addname.setText(text.name);
        addname.setTypeface(Typeface.DEFAULT_BOLD);
        addname.setTextSize(25);
        addname.setTextColor(getResources().getColor(R.color.colorPrimary));
        ll.addView(addname);
        if(text.text.startsWith("https://firebasestorage.googleapis.com/")){
            ImageView imageView=new ImageButton(getApplicationContext());
            Picasso.with(getApplicationContext()).load(text.text).into(imageView);
            imageView.setFocusable(true);
            imageView.setFocusableInTouchMode(true);
            ll.addView(imageView);
            imageView.requestFocus();
            message.setText("");
        }
        else {
            TextView addText = new TextView(this);
            addText.setText(text.text);
            addText.setTextSize(22);
            addText.requestFocus();
            addText.setTextColor(getResources().getColor(R.color.colorPrimary));
            ll.addView(addText);
            message.setText("");
            addText.setFocusable(true);
            addText.setFocusableInTouchMode(true);
            addText.requestFocus();
        }
    }

    public static class Chat implements Serializable {
        public String text;
        public String name;

        Chat(String a, String b) {
            text = a;
            name = b;
        }

        Chat() {

        }

    }
}
