package com.parivartan.medico;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.telecom.Call;
import android.text.TextUtils;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.parivartan.medico.activity.FirebaseVariables;
import com.parivartan.medico.activity.MyProfile;
import com.parivartan.medico.activity.PreProfileUpdate;
import com.parivartan.medico.activity.PresentQR;
import com.parivartan.medico.activity.TrackRecord;
import com.parivartan.medico.adapter.MedicineAdapter;
import com.parivartan.medico.model.Medicine;
import com.parivartan.medico.model.PatientDetail;
import com.parivartan.medico.model.UserFills;

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

    public static final MediaType MEDIA_TYPE =
            MediaType.parse("application/json");
    public static String email;
    public static String username;
    Spinner spinner_type;
    EditText et_medicine_name;
    EditText et_medicine_dosage;
    String med_name;
    String med_dosage;
    String med_type;
    String type;
    JSONObject postdata;
    Button bt_analyse;
    //Percentage TextViews
    TextView mAllergy, mPreg, mAge, mDisease, mDosage, mResistance, mOverall;
    String allergy, disease, pregnancy, resistance, age, dosage, overall;
    private int count = 2;
    private String TAG = "MainActivity.class";
    MedicineAdapter medicineAdapter;


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

        if (FirebaseVariables.user == null) {
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


        spinner_type = (Spinner) findViewById(R.id.spinner_types);
        setupSpinner();

        //EditText id reference
        et_medicine_name = (EditText) findViewById(R.id.et_medicine_name);
        et_medicine_dosage = (EditText) findViewById(R.id.et_dosage);
        bt_analyse = (Button) findViewById(R.id.bt_analyse);

        //Text view ids
        mDosage = (TextView) findViewById(R.id.dosage_percentage);
        mAge = (TextView) findViewById(R.id.age_percentage);
        mPreg = (TextView) findViewById(R.id.pregnancy_percentage);
        mResistance = (TextView) findViewById(R.id.resistance_percentage);
        mAllergy = (TextView) findViewById(R.id.allergy_percentage);
        mDisease = (TextView) findViewById(R.id.Disease_condition_percentage);
        mOverall = (TextView) findViewById(R.id.overall_percentage);

        postdata = new JSONObject();

        bt_analyse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                med_name = et_medicine_name.getText().toString();
                med_dosage = et_medicine_dosage.getText().toString();

                if (TextUtils.isEmpty(med_name)) {
                    et_medicine_name.setError("You can't leave this empty.");
                }

                if (TextUtils.isEmpty(med_dosage)) {
                    et_medicine_dosage.setError("You can't leave this empty.");
                }

                postdata = new JSONObject();
                try {
                    postdata.put("Type", med_type);
                    postdata.put("User_Name", username);
                    postdata.put("Medicine_Name", med_name);
                    postdata.put("Dosage", med_dosage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(MEDIA_TYPE,
                        postdata.toString());
                final Request request = new Request.Builder()
                        .url("https://api.illiteracy22.hasura-app.io/ML_Analysis/Evaluation")
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("status", "200")
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
                        if (response.isSuccessful()) {
                            try {
                                JSONObject json = new JSONObject(mMessage);
                                allergy = json.getString("allergy_percentage");
                                disease = json.getString("Disease_condition_percentage");
                                pregnancy = json.getString("Pregnancy_percentage");
                                resistance = json.getString("Resistance_percentage");
                                age = json.getString("age_percentage");
                                dosage = json.getString("dosage_percentage");
                                overall = json.getString("aggerage_percentage");

                                Log.e("Values", allergy + disease + pregnancy + resistance + age + dosage + overall);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                updateUi();
            }
        });
    }

    private void updateUi() {

        mAllergy.setText(allergy);
        mDisease.setText(disease);
        mPreg.setText(pregnancy);
        mResistance.setText(resistance);
        mAge.setText(age);
        mDosage.setText(dosage);
        mOverall.setText("Overall Percentage - " + overall);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(new Intent(this, EmployeeRegistration.class));
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
        Log.d(TAG, "COUNT == " + count);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (count == 0) {
                count = 0;
                moveTaskToBack(true);
                Log.d(TAG, "COUNT == 0");
            } else {
                Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        count = 2;
    }

    private void setupSpinner() {
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.array_med_types,
                R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_type.setAdapter(adapter);
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                med_type = (String) adapterView.getItemAtPosition(i);
                if (med_type.equals("Alimentary_System")) {
                    med_type = "1";
                } else if (med_type.equals("Antibiotics")) {
                    med_type = "2";
                } else if (med_type.equals("Cardiovascular_System")) {
                    med_type = "3";
                } else if (med_type.equals("Central_Nervous_System")) {
                    med_type = "4";
                } else if (med_type.equals("Eye")) {
                    med_type = "5";
                } else if (med_type.equals("Genito-Urinary_Tract")) {
                    med_type = "6";
                } else if (med_type.equals("Hormones")) {
                    med_type = "7";
                } else if (med_type.equals("Metabolism")) {
                    med_type = "8";
                } else if (med_type.equals("Musculo-Skeletal_Disorder")) {
                    med_type = "9";
                } else if (med_type.equals("Nutrition")) {
                    med_type = "10";
                } else if (med_type.equals("Oropharyngeal")) {
                    med_type = "11";
                } else if (med_type.equals("Respiratory_System_and_Anti-Allergics")) {
                    med_type = "12";
                } else if (med_type.equals("Skin")) {
                    med_type = "13";
                } else if (med_type.equals("Surgical_and_Vaccines")) {
                    med_type = "14";
                }

                Log.e("TYPE", med_type);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });
    }
}
