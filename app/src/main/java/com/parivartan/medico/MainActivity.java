package com.parivartan.medico;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.telecom.Call;
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
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.parivartan.medico.activity.MyProfile;
import com.parivartan.medico.activity.PreProfileUpdate;
import com.parivartan.medico.activity.PresentQR;
import com.parivartan.medico.activity.TrackRecord;
import com.parivartan.medico.adapter.MedicineAdapter;
import com.parivartan.medico.model.Medicine;
import com.parivartan.medico.model.PatientDetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int count = 2;
    private String TAG = "MainActivity.class";
    MedicineAdapter medicineAdapter;

    public static final MediaType MEDIA_TYPE =
            MediaType.parse("application/json");


    FirebaseAuth mAuth;

    public static String email;
    public static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final OkHttpClient client = new OkHttpClient();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        username = preferences.getString("username", "");


//        Intent intent = getIntent();
//        String email = intent.getStringExtra("email");
//        String username = intent.getStringExtra("username");

        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, username, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, EmployeeRegistration.class));
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        List<Medicine> medicines = new ArrayList<>();
        ListView listView = (ListView) findViewById(R.id.list_view);
        medicineAdapter = new MedicineAdapter(getApplicationContext(), R.layout.list_medicine, medicines);
        listView.setAdapter(medicineAdapter);

        JSONObject postdata = new JSONObject();

        try {
            postdata.put("User_Name", username);
            postdata.put("email", email);
        } catch(JSONException e){
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE,
                postdata.toString());

        final Request request = new Request.Builder()
                .url("https://api.illiteracy22.hasura-app.io/User_Personal_Details/upload_user_details")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                //call.cancel();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {

                String mMessage = response.body().string();
                if (response.isSuccessful()){
                    try {
                        JSONObject json = new JSONObject(mMessage);
                        Log.e("JSON",json.toString());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            try {
                FirebaseAuth.getInstance().signOut();
            } catch (Exception e){
                e.printStackTrace();
            }
            startActivity(new Intent(this,EmployeeRegistration.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_profile) {

            startActivity(new Intent(this, MyProfile.class));

        } else if (id == R.id.nav_history) {

            startActivity(new Intent(this, TrackRecord.class));

        } else if (id == R.id.nav_pre_update) {

            startActivity(new Intent(this, PreProfileUpdate.class));

        } else if (id == R.id.nav_reports) {

            startActivity(new Intent(this, PresentQR.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        count--;
        Log.d(TAG,"COUNT == "+count);
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            if(count == 0){
                count = 0 ;
                moveTaskToBack(true);
                Log.d(TAG,"COUNT == 0");
            }else{
                Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        count = 2;
    }
}
