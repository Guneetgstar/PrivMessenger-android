package com.priv.messenger;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ImageView;
import android.widget.TextView;
import com.jaeger.library.StatusBarUtil;
import com.loopj.android.http.PersistentCookieStore;
import com.priv.messenger.frag.AboutFragment;
import com.priv.messenger.frag.ChatFragment;
import com.priv.messenger.frag.UploadFragment;
import com.priv.messenger.utils.Utils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainAfLogin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FragmentManager fm;
    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        fm=getFragmentManager();
        ft=fm.beginTransaction();
        ft.add(R.id.frame2,new ChatFragment(),"chat");
        ft.commit();
        Log.d("frag","added");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StatusBarUtil.setTransparent(this);
        NavigationView navigationView=(NavigationView)findViewById(R.id.nav_view);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        ImageView pic=(ImageView)headerView.findViewById(R.id.imageView);
        TextView name=(TextView)headerView.findViewById(R.id.textView);
        String uname=Utils.getUsername(this);
        Picasso.with(this)
                .load(this.getResources().getString(R.string.site)+"user/"+uname+".jpg")
                .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                .into(pic);
        name.setText(uname);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final CoordinatorLayout cl=(CoordinatorLayout) findViewById(R.id.coordinator);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),NewMsg.class);
                startActivity(intent);
                //Snackbar.make(cl, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Menu menu = navigationView.getMenu();
        MenuItem tools= menu.findItem(R.id.nav_gallery);
        SpannableString s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length(), 0);
        tools.setTitle(s);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            FragmentTransaction ft=fm.beginTransaction();
            ft.replace(R.id.frame2,new ChatFragment());
            ft.commit();
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            PersistentCookieStore store=new PersistentCookieStore(this);
            store.clear();
            Intent intent=new Intent(this,Main.class);
            startActivity(intent);
            this.finish();
        } else if (id == R.id.nav_manage) {
            FragmentTransaction ft=fm.beginTransaction();
            ft.replace(R.id.frame2,new UploadFragment());
            ft.commit();
        } else if (id == R.id.nav_share) {
            FragmentTransaction ft=fm.beginTransaction();
            ft.replace(R.id.frame2,new AboutFragment());
            ft.commit();

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
