package com.parivartan.medico.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.parivartan.medico.EmployeeRegistration;
import com.parivartan.medico.MainActivity;
import com.parivartan.medico.R;
import com.parivartan.medico.model.PatientDetail;
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

public class TrackRecord extends AppCompatActivity implements LoaderManager.LoaderCallbacks<PatientHistory> {

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
    int code;
    private static final int PatientHistoryLoader_LOADER_ID = 1;
    private String urlToSend;

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

        updateHistoryUi();
    }

    private void updateHistoryUi() {
        FirebaseVariables.mDatabaseReference.child(FirebaseVariables.user.getUid()).child("history").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((boolean) dataSnapshot.getValue()) {
                    Toast.makeText(TrackRecord.this, "Fetching your previously filled data", Toast.LENGTH_SHORT).show();
                    // Get a reference to the ConnectivityManager to check state of network connectivity
                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);

                    // Get details on the currently active default data network
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                    // If there is a network connection, fetch data
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // Get a reference to the LoaderManager, in order to interact with loaders.
                        LoaderManager loaderManager = getLoaderManager();

                        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                        // because this activity implements the LoaderCallbacks interface).
                        loaderManager.initLoader(PatientHistoryLoader_LOADER_ID, null, TrackRecord.this);
                    }

                    urlToSend = "https://api-illiteracy22.azurewebsites.net/User_History_Upload/update_user_history";
                } else {
                    urlToSend = "https://api-illiteracy22.azurewebsites.net/User_History_Upload/upload_user_history";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void extractUserInputsFromUI() {
        allergens = Arrays.asList(mAllergens.getText().toString().trim().replaceAll("\\s*,\\s*", ",").split(","));
        resistance = Arrays.asList(mResistance.getText().toString().trim().replaceAll("\\s*,\\s*", ",").split(","));
        pregnancy = mPregnancy.isChecked();
        diabetes = mDiabetes.isChecked();
        highbloodPressure = mHighBloodPressure.isChecked();
        highCholestrol = mHighCholestrol.isChecked();
        others = Arrays.asList(mOther.getText().toString().trim().replaceAll("\\s*,\\s*", ",").split(","));
        geneticDisesase = Arrays.asList(mGeneticDiseaseEditText.getText().toString().trim().replaceAll("\\s*,\\s*", ",").split(","));
    }

    private void saveUserDetails() {

        postdata = new JSONObject();

        try {
            postdata.put("User_Name", username);
            postdata.put("Session_token", FirebaseVariables.user.getUid());
            postdata.put("Allergens", allergens);
            postdata.put("Resistance", resistance);
            postdata.put("Pregnancy", pregnancy);
            postdata.put("diabetes", diabetes);
            postdata.put("highBloodPressure", highbloodPressure);
            postdata.put("highCholesterol", highCholestrol);
            postdata.put("other", others);
            postdata.put("geneticDisease", geneticDisesase);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE,
                postdata.toString());

        final Request request = new Request.Builder()
                .url(urlToSend)
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
                        code = json.getInt("code");
                        Log.e("Yash", code + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                /*if (code == 401) {
                    //Toast.makeText(TrackRecord.this, "Session Expires", Toast.LENGTH_SHORT).show();
                } else if (code == 404) {
                    //Toast.makeText(TrackRecord.this, "User not found", Toast.LENGTH_SHORT).show();
                } else if (code == 200) {
                    FirebaseVariables.mDatabaseReference.child(FirebaseVariables.user.getUid()).child("history").setValue(true);
                    //Toast.makeText(TrackRecord.this, "Medical history uploaded successfully", Toast.LENGTH_SHORT).show();
                }*/

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == 401) {
                            Toast.makeText(TrackRecord.this, "Session Expires", Toast.LENGTH_SHORT).show();
                        } else if (code == 404) {
                            Toast.makeText(TrackRecord.this, "User not found", Toast.LENGTH_SHORT).show();
                        } else if (code == 200) {
                            Toast.makeText(TrackRecord.this, "Medical history uploaded successfully", Toast.LENGTH_SHORT).show();
                            FirebaseVariables.mDatabaseReference.child(FirebaseVariables.user.getUid()).child("history").setValue(true);
                        }
                    }
                });
            }
        });
    }

    @Override
    public Loader<PatientHistory> onCreateLoader(int i, Bundle bundle) {
        return new PatientHistoryLoader(this, "https://api-illiteracy22.azurewebsites.net/User_History_Upload/download_user_history/" + username + "/" + FirebaseVariables.user.getUid());
    }

    @Override
    public void onLoadFinished(Loader<PatientHistory> loader, PatientHistory patientHistory) {
        mAllergens.setText(patientHistory.getAllergens().toString().replaceAll("[\\[\\]]", ""));
        mResistance.setText(patientHistory.getResistance().toString().replaceAll("[\\[\\]]", ""));
        mPregnancy.setChecked(patientHistory.isPregnancy());
        mDiabetes.setChecked(patientHistory.isDiabetes());
        mHighBloodPressure.setChecked(patientHistory.isHighbloodPressure());
        mHighCholestrol.setChecked(patientHistory.isHighCholestrol());
        mOther.setText(patientHistory.getOthers().toString().replaceAll("[\\[\\]]", ""));
        mGeneticDiseaseEditText.setText(patientHistory.getGeneticDisesase().toString().replaceAll("[\\[\\]]", ""));
    }

    @Override
    public void onLoaderReset(Loader<PatientHistory> loader) {
        // Loader reset, so we can clear out our existing data.
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

        Intent newIntent = getIntent();
        if (!newIntent.getBooleanExtra("checkSkip", false)) {
            menu.findItem(R.id.bt_skip).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save_user_history:
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

            case R.id.bt_skip:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
