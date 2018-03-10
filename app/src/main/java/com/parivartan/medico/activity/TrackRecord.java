package com.parivartan.medico.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.gson.Gson;
import com.parivartan.medico.EmployeeRegistration;
import com.parivartan.medico.MainActivity;
import com.parivartan.medico.R;
import com.parivartan.medico.model.PatientHistory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by root on 3/7/18.
 */

public class TrackRecord extends AppCompatActivity {

    public static final String LOG_TAG = TrackRecord.class.getName();
    public static final MediaType MEDIA_TYPE =
            MediaType.parse("application/json");
    String username;
    List<String> allergens;
    List<String> resistance;
    boolean pregnancy;
    boolean diabetes;
    boolean highbloodPressure;
    boolean highCholestrol;
    List<String> others;
    List<String> geneticDisesase;
    OkHttpClient client;
    JSONObject postdata;
    private EditText mAllergens;
    private EditText mResistance;
    private CheckBox mPregnancy;
    private CheckBox mDiabetes;
    private CheckBox mHighBloodPressure;
    private CheckBox mHighCholestrol;
    private EditText mOther;
    private EditText mGeneticDiseaseEditText;
    private String json;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        client = new OkHttpClient();

        mAllergens = (EditText) findViewById(R.id.allergens_edit_text);
        mResistance = (EditText) findViewById(R.id.resistance_edit_text);
        mPregnancy = (CheckBox) findViewById(R.id.pregnancy_checkbox);
        mDiabetes = (CheckBox) findViewById(R.id.diabetes_checkbox);
        mHighBloodPressure = (CheckBox) findViewById(R.id.highBloodPressure_checkbox);
        mHighCholestrol = (CheckBox) findViewById(R.id.highCholesterol_checkbox);
        mOther = (EditText) findViewById(R.id.other_edit_text);
        mGeneticDiseaseEditText = (EditText) findViewById(R.id.genetic_edit_text);

        username = MainActivity.username;
    }

    private void extractUserInputsFromUI() {
        allergens = Arrays.asList(mAllergens.getText().toString().trim().split(","));
        resistance = Arrays.asList(mResistance.getText().toString().trim().split(","));
        pregnancy = mPregnancy.isChecked();
        diabetes = mDiabetes.isChecked();
        highbloodPressure = mHighBloodPressure.isChecked();
        highCholestrol = mHighCholestrol.isChecked();
        others = Arrays.asList(mOther.getText().toString().trim().split(","));
        geneticDisesase = Arrays.asList(mGeneticDiseaseEditText.getText().toString().trim().split(","));
    }

    private void saveUserDetails() {

        postdata = new JSONObject();

        try {
            postdata.put("User_Name", username);
            postdata.put("Allergens", allergens);
            postdata.put("Resistance", resistance);
            postdata.put("Pregnancy", pregnancy);
            postdata.put("diabetes", diabetes);
            postdata.put("highBloodPressure", highbloodPressure);
            postdata.put("highCholesterol", highbloodPressure);
            postdata.put("other", others);
            postdata.put("geneticDisease", geneticDisesase);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE,
                postdata.toString());

        final Request request = new Request.Builder()
                .url("https://api.illiteracy22.hasura-app.io/User_History_Upload/upload_user_history")
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
                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(mMessage);
                        Log.e("JSON", json.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Toast.makeText(TrackRecord.this, "Data Uploaded", Toast.LENGTH_SHORT).show();
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener saveButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save your changes and quit editing?");
        builder.setPositiveButton("Save", saveButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_track_record, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                extractUserInputsFromUI();
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener saveButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                saveUserDetails();
                                finish();
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(saveButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
